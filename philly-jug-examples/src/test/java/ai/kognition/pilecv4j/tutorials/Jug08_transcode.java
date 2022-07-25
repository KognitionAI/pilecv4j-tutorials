package ai.kognition.pilecv4j.tutorials;

import org.junit.Test;

import ai.kognition.pilecv4j.ffmpeg.Ffmpeg;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg.EncodingContext;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg.MediaContext;

public class Jug08_transcode extends BaseTest {

    @Test
    public void test() throws Exception {

        try(
            // create a MediaContext with the given URL for the video
            final MediaContext mediaContext = Ffmpeg.createMediaContext(VIDEO);

            // Create a context for the encoding
            final EncodingContext encCtx = Ffmpeg.createEncoder("/tmp/junk.avi");) {

            // read the input context and set the output frame rate to the same.
            encCtx.setFps(mediaContext);

            // play the media ...
            mediaContext
                // re-encode the frames.
                .processVideoFrames(f -> encCtx.encode(f))

                // start the processing
                .play();
        }
    }
}
