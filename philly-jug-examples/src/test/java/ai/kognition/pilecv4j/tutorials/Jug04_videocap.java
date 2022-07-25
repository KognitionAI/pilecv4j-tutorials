package ai.kognition.pilecv4j.tutorials;

import org.junit.Test;
import org.opencv.videoio.VideoCapture;

import ai.kognition.pilecv4j.image.CvMat;
import ai.kognition.pilecv4j.image.display.ImageDisplay;

public class Jug04_videocap extends BaseTest {

    @Test
    public void test() throws Exception {

        try(
            // create image display
            ImageDisplay id = new ImageDisplay.Builder().build();

            // resource managed Mat
            final CvMat frame = new CvMat();) {

            // Initialize the video source.
            final VideoCapture cap = new VideoCapture(VIDEO);

            while(true) {

                // Read frames
                if(cap.read(frame)) {
                    id.update(frame);
                }
            }
        }
    }
}
