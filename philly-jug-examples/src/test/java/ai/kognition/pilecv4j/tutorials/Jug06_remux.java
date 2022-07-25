package ai.kognition.pilecv4j.tutorials;

import org.junit.Test;

import ai.kognition.pilecv4j.ffmpeg.Ffmpeg;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg.MediaContext;
import ai.kognition.pilecv4j.ffmpeg.Muxer;

public class Jug06_remux extends BaseTest {

    @Test
    public void test() throws Exception {

        try(
            // create a MediaContext with the given URL for the video
            final MediaContext c = Ffmpeg.createMediaContext(VIDEO)

                // send the demuxed input to the provided muxer
                .remux(Muxer.create("/tmp/junk.avi"))

                // start the processing
                .play();

        ) {}
    }
}
