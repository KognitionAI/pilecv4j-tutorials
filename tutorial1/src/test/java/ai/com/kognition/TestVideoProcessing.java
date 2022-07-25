package ai.com.kognition;

import org.junit.Test;

import ai.kognition.pilecv4j.ffmpeg.Ffmpeg2;
import ai.kognition.pilecv4j.ffmpeg.Ffmpeg2.StreamContext;
import ai.kognition.pilecv4j.image.CvMat;
import ai.kognition.pilecv4j.image.display.ImageDisplay;

public class TestVideoProcessing {
	
	public static final String TEST_VIDEO = "/tmp/test-video.mp4";

    @Test
    public void testSimpleVideoDisplay() throws Exception {

        // Most components are java resources (Closeables)
        try(

            // We will create an ImageDisplay in order to show the frames from the video
            ImageDisplay window = new ImageDisplay.Builder()
                .windowName("Tutorial 1")
                .build();

            // create a StreamContext using Ffmpeg2. StreamContexts represent
            // a source of media data and a set of processing to be done on that data.
            final StreamContext sctx = Ffmpeg2.createStreamContext()

                // create a media data source for the StreamContext. In this case the source
                // of media data will be our file.
                .createMediaDataSource(TEST_VIDEO)

                // We need to open a processing chain. A processing chain is a
                // grouping of a stream selector, with a series of media stream
                // processors.
                .openChain()

                // We are simply going to pick the first video stream from the file.
                .createFirstVideoStreamSelector()

                // Then we can add a processor. In this case we want the system to call us
                // with each subsequent frame as an OpenCV Mat.
                .createVideoFrameProcessor(videoFrame -> {

                    // we want to display each frame. PileCV4J extends the OpenCV Mat functionality
                    // for better native resource/memory management. So we can use a try-with-resource.
                    try(CvMat mat = videoFrame.bgr(false);) { // Note, we want to make sure the Mat is BGR
                        // Display the image.
                        window.update(mat);
                    }

                })

                // we need the resulting streaming context returned.
                .streamContext();

        ) {

            // play the media stream.
            sctx.play();
        }

    }

}
