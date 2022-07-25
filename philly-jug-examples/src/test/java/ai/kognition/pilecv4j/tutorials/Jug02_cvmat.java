package ai.kognition.pilecv4j.tutorials;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import ai.kognition.pilecv4j.image.CvMat;
import ai.kognition.pilecv4j.image.ImageFile;
import ai.kognition.pilecv4j.image.display.ImageDisplay;

public class Jug02_cvmat extends BaseTest {
    public static final String TEST_IMAGE = "target/test-classes/HD-Country-Image.jpg";

    @Test
    public void test() throws Exception {
        try(final CvMat image = ImageFile.readMatFromFile(TEST_IMAGE);
            final CvMat dx = new CvMat();
            final CvMat dy = new CvMat();

            ImageDisplay od = new ImageDisplay.Builder().build();
            ImageDisplay id = new ImageDisplay.Builder()
                .windowName(TEST_IMAGE)
                .build();) {

            System.out.println(image);
            od.update(image);

            // ==================================================
            // remove high frequency noise
            Imgproc.GaussianBlur(image, image, new Size(11, 11), 0, 0);

            // take the gradient
            Imgproc.Scharr(image, dx, CvType.CV_16S, 1, 0);
            Imgproc.Scharr(image, dy, CvType.CV_16S, 0, 1);

            // abs value
            Core.absdiff(dx, new Scalar(0), dx);
            Core.absdiff(dy, new Scalar(0), dy);

            // edge detection
            Imgproc.Canny(dx, dy, image, 50, 150);

//            System.out.println(dx);
//            dx.convertTo(dx, CvType.CV_8UC1);
            // ==================================================

            id.update(image);
            id.waitUntilClosed();
        }
    }
}
