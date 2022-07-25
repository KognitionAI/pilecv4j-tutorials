package ai.kognition.pilecv4j.tutorials;

import org.junit.Test;

import ai.kognition.pilecv4j.ffmpeg.Ffmpeg;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg.MediaContext;
import ai.kognition.pilecv4j.ffmpeg.Muxer;
import ai.kognition.pilecv4j.image.display.ImageDisplay;

public class Jug07_fullFunctionality extends BaseTest {

    @Test
    public void test() throws Exception {

        try(

            // create image display
            final ImageDisplay id = new ImageDisplay.Builder().build();

            // create a MediaContext with the given URL for the video
            final MediaContext sc = Ffmpeg.createMediaContext()

                // explicitly add a media source
                .source(VIDEO_FILE)

        ) {
            sc

                // ===================================================
                // open a processing chain
                .chain("first chain")

                // prefer bgr
                .preferBgr()

                // create a packet filter.
                .selectFirstVideoStream()

                // remux
                .remux(Muxer.create("/tmp/out.mp4"))

                // Use the provided callback on each video frame.
                // The callback just updates the display
                .processVideoFrames(f -> id.update(f))

                // resume the media context
                .mediaContext()
                // ===================================================

                // ===================================================
                // open a second processing chain
                .chain("second")

                .remux(Muxer.create("/tmp/out2.mp4"))

                .mediaContext()
                // ===================================================

                // start the processing
                .play();

        }
    }
}
