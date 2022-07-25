package ai.kognition.pilecv4j.tutorials;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Jug01_opencv extends BaseTest {
    public static final String TEST_IMAGE = "target/test-classes/HD-Country-Image.jpg";

    @Test
    public void test() throws Exception {
        final Mat image = Imgcodecs.imread(TEST_IMAGE);
        System.out.println(image);

        // ==================================================
        // remove high frequency noise
        Imgproc.GaussianBlur(image, image, new Size(11, 11), 0, 0);

        final Mat dx = new Mat();
        final Mat dy = new Mat();
        // take the gradient
        Imgproc.Scharr(image, dx, CvType.CV_16S, 1, 0);
        Imgproc.Scharr(image, dy, CvType.CV_16S, 0, 1);

        // abs value
        Core.absdiff(dx, new Scalar(0), dx);
        Core.absdiff(dy, new Scalar(0), dy);

        // edge detection
        Imgproc.Canny(dx, dy, image, 50, 150);

        System.out.println(dx);
//        dx.convertTo(dx, CvType.CV_8U);
//        HighGui.imshow(TEST_IMAGE + " gradient X", dx);
//        // ==================================================
//
        HighGui.imshow(TEST_IMAGE, image);
        HighGui.waitKey();
    }
}
