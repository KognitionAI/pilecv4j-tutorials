package ai.com.kognition;

import ai.kognition.pilecv4j.ffmpeg.Ffmpeg;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg.MediaContext;
import ai.kognition.pilecv4j.image.CvMat;
import ai.kognition.pilecv4j.image.display.ImageDisplay;

/**
 * This is the fully working code for the first "Jumping Right In" example from
 * the Pilecv4j README
 */
public class SimpleExample {

    public static final String TEST_VIDEO = "/tmp/test-video.mp4";

    public static void main(final String args[]) {
        // Most components are java resources (AutoCloseables)
        try(

            // We will create an ImageDisplay in order to show the frames from the video
            ImageDisplay window = new ImageDisplay.Builder()
                .windowName("Tutorial 1")
                .build();

            // create a StreamContext using Ffmpeg2. StreamContexts represent
            // a source of media data and a set of processing to be done on that data.
            final MediaContext sctx = Ffmpeg.createMediaContext(TEST_VIDEO)

                // Tell the decoding that, if you need to convert the color anyway,
                // you might as well convert it to BGR rather than RGB.
                .preferBgr()

                // We are simply going to pick the first video stream from the file.
                .selectFirstVideoStream()

                // Then we can add a processor. In this case we want the system to call us
                // with each subsequent frame as an OpenCV Mat.
                .processVideoFrames(videoFrame -> {

                    // we want to display each frame. PileCV4J extends the OpenCV Mat functionality
                    // for better native resource/memory management. So we can use a try-with-resource.
                    try(CvMat mat = videoFrame.bgr(false);) { // Note, we want to make sure the Mat is BGR
                        // Display the image.
                        window.update(mat);
                    }

                })

                // play the media stream.
                .play();

        ) {}

    }
}
