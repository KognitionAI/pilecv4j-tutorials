package ai.kognition.pilecv4j.tutorials;

import org.junit.Test;
import org.opencv.core.Core;

import ai.kognition.pilecv4j.ffmpeg.Ffmpeg;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg.EncodingContext;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg.EncodingContext.VideoEncoder;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg.MediaContext;
import ai.kognition.pilecv4j.ffmpeg.Muxer;
import ai.kognition.pilecv4j.image.CvMat;
import ai.kognition.pilecv4j.image.display.ImageDisplay;

public class Jug09_fullEncFunctionality extends BaseTest {

    @Test
    public void test() throws Exception {

        try(ImageDisplay id = new ImageDisplay.Builder().build();

            // create a MediaContext with the given URL for the video
            final MediaContext mediaContext = Ffmpeg.createMediaContext(VIDEO_FILE);

            // Create a context for the encoding
            final EncodingContext encCtx = Ffmpeg.createEncoder()
                .muxer(Muxer.create("/tmp/out3.mp4"));) {

            final VideoEncoder ve1 = encCtx.videoEncoder("libx264", "first");
            final VideoEncoder ve2 = encCtx.videoEncoder("libx264", "second");

            // read the input context and set the output frame rate to the same.
            ve1.setFps(mediaContext);
            ve2.setFps(mediaContext);

            // play the media ...
            mediaContext
                .selectFirstVideoStream()
                // re-encode the frames.
                .processVideoFrames(f -> {
                    ve1.enable(f, f.isRgb);
                    ve2.enable(f, f.isRgb);
                },
                    f -> {
                        id.update(f);
                        ve1.encode(f);
                        try(CvMat flipped = new CvMat();) {
                            Core.flip(f, flipped, 1);
                            ve2.encode(flipped, f.isRgb);
                        }
                    })

                // start the processing
                .play();
        }
    }
}
