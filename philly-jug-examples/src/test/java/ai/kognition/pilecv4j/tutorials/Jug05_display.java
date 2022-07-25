
package ai.kognition.pilecv4j.tutorials;

import org.junit.Test;

import ai.kognition.pilecv4j.ffmpeg.Ffmpeg;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg.MediaContext;
import ai.kognition.pilecv4j.image.display.ImageDisplay;

public class Jug05_display extends BaseTest {

    @Test
    public void test() throws Exception {

        try(
            // create image display
            ImageDisplay id = new ImageDisplay.Builder().build();
//            ImageDisplay od = new ImageDisplay.Builder().build();

            // create a MediaContext with the given URL for the video
            MediaContext sc = Ffmpeg.createMediaContext(VIDEO)

                // Use the provided callback on each video frame.
                // The callback just updates the display
                .processVideoFrames(f -> {

                    id.update(f.bgr(false));
                })

                // start the processing
                .play();

        ) {

        }
    }
}
