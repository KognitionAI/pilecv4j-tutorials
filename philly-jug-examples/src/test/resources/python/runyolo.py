import cv2
import pilecv4j
import numpy as np
import torch
import torch.nn as nn

from models.common import Conv
from utils.torch_utils import select_device
from utils.general import check_img_size, is_ascii, non_max_suppression
from pathlib import Path

class Ensemble(nn.ModuleList):
    # Ensemble of models
    def __init__(self):
        super().__init__()

    def forward(self, x, augment=False, profile=False):
        y = []
        for module in self:
            y.append(module(x, augment, profile)[0])
        # y = torch.stack(y).max(0)[0]  # max ensemble
        # y = torch.stack(y).mean(0)  # mean ensemble
        y = torch.cat(y, 1)  # nms ensemble
        return y, None  # inference, train output

def load_model(weights, map_location):
    print("in load",flush=True)
    from models.yolo import Detect, Model

    # Loads an ensemble of models weights=[a,b,c] or a single model weights=[a] or weights=a
    model = Ensemble()
    for w in weights if isinstance(weights, list) else [weights]:
        ckpt = torch.load(str(Path(str(w).strip().replace("'", ''))), map_location=map_location)  # load
        model.append(ckpt['ema' if ckpt.get('ema') else 'model'].float().fuse().eval())  # FP32 model

    # Compatibility updates
    for m in model.modules():
        print(type(m),flush=True)
        if type(m) in [nn.Hardswish, nn.LeakyReLU, nn.ReLU, nn.ReLU6, nn.SiLU, Detect, Model]:
            print("pytorch 1.7 compat",flush=True)
            m.inplace = True  # pytorch 1.7.0 compatibility
        elif type(m) is Conv:
            print("pytorch 1.6 compat",flush=True)
            m._non_persistent_buffers_set = set()  # pytorch 1.6.0 compatibility

    if len(model) == 1:
        return model[-1]  # return model
    else:
        print(f'Ensemble created with {weights}\n')
        for k in ['names']:
            setattr(model, k, getattr(model[-1], k))
        model.stride = model[torch.argmax(torch.tensor([m.stride.max() for m in model])).int()].stride  # max stride
        return model  # return ensemble

def load(weights, device = 0, imgsz = [640], half=False):
    device = select_device(device)
    model = load_model(weights, map_location=device)  # load FP32 model
    # prep the model?
    imgsz *= 2 if len(imgsz) == 1 else 1  # expand
    print ("imgsz:", imgsz, flush=True)

    half &= device.type != 'cpu'  # half precision only supported on CUDA
    
    if half:
        model.half()  # to FP16
    
    model(torch.zeros(1, 3, *imgsz).to(device).type_as(next(model.parameters())))  # run once
    model.myimgsz = imgsz
    model.mydevice = device
    model.myhalf = half

    return model

def labels(model):
    return model.module.names if hasattr(model, 'module') else model.names  # get class names

def yolo(img, isRgb, model, conf_thres=0.5,
         iou_thres=0.45,  # NMS IOU threshold
         classes=None,
         agnostic_nms=False,  # class-agnostic NMS
         max_det=1000  # maximum detections per image
):
    imgsz = model.myimgsz
    device = model.mydevice
    half = model.myhalf
    
    if (not isRgb):
        img = img.transpose((2, 0, 1))[::-1]  # HWC to CHW, BGR to RGB
    else:
        img = img.transpose((2, 0, 1))
    
    img = np.ascontiguousarray(img)

    img = torch.from_numpy(img).to(device)
    img = img.half() if half else img.float()  # uint8 to fp16/32

    img = img / 255.0  # 0 - 255 to 0.0 - 1.0
    if len(img.shape) == 3:
        img = img[None]  # expand for batch dim
        
    pred = model(img, augment=False)[0]
    pred = non_max_suppression(pred, conf_thres, iou_thres, classes, agnostic_nms, max_det=max_det)

    ret = pred[0].cpu().numpy()
    return ret
    
def runyolo(weights, javaHandle,
          half=False, device = 0, imgsz = [640], conf_thres=0.5,  # confidence threshold
          iou_thres=0.45,  # NMS IOU threshold
          classes=None,
          agnostic_nms=False,  # class-agnostic NMS
          max_det=1000  # maximum detections per image
):
    torch.cuda.init()
    print(half)
    print(device)
    print(weights,flush=True)
    imgsz *= 2 if len(imgsz) == 1 else 1  # expand

    device = select_device(device)
    model = load_model(weights, map_location=device)  # load FP32 model
    half &= device.type != 'cpu'  # half precision only supported on CUDA
    #print(half)

    stride = int(model.stride.max())  # model stride
    names = model.module.names if hasattr(model, 'module') else model.names  # get class names

    # should be called before getting the images source.
    javaHandle.modelLabels(names)

    if half:
        model.half()  # to FP16

    imgsz = check_img_size(imgsz, s=stride)  # check image size
    ascii = is_ascii(names)  # names are ascii (use PIL for UTF-8)

    # prep the model?
    model(torch.zeros(1, 3, *imgsz).to(device).type_as(next(model.parameters())))  # run once

    imageSource = javaHandle.getImageSource()

    request = imageSource.next()
    while (request is not None):
        img = request.get()

        if (not request.isRgb()):
            img = img.transpose((2, 0, 1))[::-1]  # HWC to CHW, BGR to RGB
        else:
            img = img.transpose((2, 0, 1))
    
        img = np.ascontiguousarray(img)

        img = torch.from_numpy(img).to(device)
        img = img.half() if half else img.float()  # uint8 to fp16/32

        img = img / 255.0  # 0 - 255 to 0.0 - 1.0
        if len(img.shape) == 3:
            img = img[None]  # expand for batch dim
        
        pred = model(img, augment=False)[0]
        pred = non_max_suppression(pred, conf_thres, iou_thres, classes, agnostic_nms, max_det=max_det)

        request.setResult(pred[0].cpu().numpy())
    
        # next image
        request = imageSource.next()

