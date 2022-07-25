package ai.kognition.pilecv4j.tutorials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import ai.kognition.pilecv4j.ffmpeg.AsyncVideoFrameConsumer;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg.MediaContext;
import ai.kognition.pilecv4j.image.Closer;
import ai.kognition.pilecv4j.image.CvMat;
import ai.kognition.pilecv4j.image.Utils;
import ai.kognition.pilecv4j.image.Utils.LetterboxDetails;
import ai.kognition.pilecv4j.image.display.ImageDisplay;
import ai.kognition.pilecv4j.python.ParamBlock;
import ai.kognition.pilecv4j.python.PyObject;
import ai.kognition.pilecv4j.python.PythonHandle;
import ai.kognition.pilecv4j.python.ResultBlock;

public class Jug11_yolo_funcCallOnly extends BaseTest {

    final static Scalar BOX_COLOR = new Scalar(0, 255, 255);

    @Test
    public void test() throws Exception {

        try(
            // create a MediaContext with the given URL for the video
            final MediaContext c = Ffmpeg.createMediaContext(VIDEO);

            // create the Python system manager
            final PythonHandle python = new PythonHandle();

            // An image display
            final ImageDisplay id = new ImageDisplay.Builder().build();) {

            // add the module path
            python.addModulePath("./src/test/resources/python");

            // parameters to pass to the python function
            PyObject model;
            try(ResultBlock qc = python.runPythonFunction("runyolo", "load", ParamBlock.builder()
                .arg("/data/offline-test-data/od-models/yolo-pytorch/yolov5s/yolov5s.pt"));) {
                model = qc.asPyObject();
            }

            final String[] labels;
            try(
                final ResultBlock res = python.runPythonFunction("runyolo", "labels", ParamBlock.builder()
                    .arg(model));) {

                labels = res.asList().stream()
                    .map(o -> o.toString())
                    .toArray(String[]::new);
            }
            System.out.println(Arrays.toString(labels));

            // create a MediaContext with the given URL for the video
            c
                .selectFirstVideoStream()
                .processVideoFrames(new AsyncVideoFrameConsumer(f -> {
                    // for each frame ....
                    try(
                        // letterbox to the network aperture.
                        final LetterboxDetails lbDetails = Utils.letterbox(f, 640);

                        // send the image to the python script and get the result handle back
                        ResultBlock results = python.runPythonFunction("runyolo", "yolo", ParamBlock.builder()
                            .arg(lbDetails.mat())
                            .arg(f.isRgb)
                            .arg(model));

                ) {
                        // wait for the results to be available

                        // get and process the results.
                        try(final CvMat resMat = results.asMat();) {

                            // if there are any results.
                            // if(resMat.rows() > 0) {

                            // parse the results into ObjectDetection records correcting for the letterboxing.
                            final List<ObjectDetection> detections = parseResults(resMat, lbDetails);

                            // draw the detections
                            try(CvMat toDraw = f.bgr(true);) {
                                for(final ObjectDetection od: detections) {
                                    Imgproc.rectangle(toDraw, new Rect(od.boxX, od.boxY, od.boxWidth, od.boxHeight), BOX_COLOR, 2);
                                    Imgproc.putText(toDraw, labels[od.clazz] + " " + String.format("%.2f", od.probability()), new Point(od.boxX, od.boxY),
                                        Utils.OCV_FONT_HERSHEY_SIMPLEX, 1, BOX_COLOR, 2);
                                }

                                // display the annotated image
                                id.update(toDraw);
                            }
                            // }
                        }

                    }
                }))

                // start the processing
                .play();
        }
    }

    public static record ObjectDetection(int boxX, int boxY, int boxWidth, int boxHeight, int clazz, float probability) {}

    /**
     * resMat contains a matrix of results from yolo:
     *
     * | xmin | ymin | xmax | ymax | probability | class |
     * ....
     *
     * This is in terms of the letterboxed image.
     */
    public static List<ObjectDetection> parseResults(final Mat resMat, final LetterboxDetails lbDetails) {

        // one over the scale since it's more efficent to multiply than divide and we're going to use this a number of time.
        final float oOvScale = (float)(1.0 / lbDetails.scale());

        final int numDetections = resMat.rows();

        final ArrayList<ObjectDetection> ret = new ArrayList<>(numDetections);
        if(numDetections > 0) {
            try(final CvMat reshaped = CvMat.move(resMat.reshape(0, resMat.rows() * resMat.cols()));
                final Closer closer = new Closer();) {

                final MatOfFloat f = closer.addMat(new MatOfFloat(reshaped));
                final float[] vals = f.toArray();

                int pos = 0;
                for(int i = 0; i < numDetections; i++) {
                    final int clazz = (int)vals[pos + 5];
                    float xmin = vals[pos];
                    xmin = xmin < lbDetails.leftPadding() ? lbDetails.leftPadding() : xmin;
                    float ymin = vals[pos + 1];
                    ymin = ymin < lbDetails.topPadding() ? lbDetails.topPadding() : ymin;
                    float xmax = vals[pos + 2];
                    xmax = xmax > lbDetails.width() ? lbDetails.width() : xmax;
                    float ymax = vals[pos + 3];
                    ymax = ymax > lbDetails.height() ? lbDetails.height() : ymin;
                    final float width = vals[pos + 2] - xmin;
                    final float height = vals[pos + 3] - ymin;
                    ret.add(new ObjectDetection(
                        (int)((xmin - lbDetails.leftPadding()) * oOvScale),
                        (int)((vals[pos + 1] - lbDetails.topPadding()) * oOvScale),
                        (int)(width * oOvScale), (int)(height * oOvScale), clazz, vals[pos + 4]));
                    pos += 6;
                }
            }
        }
        return ret;
    }

}
