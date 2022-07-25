package ai.kognition.pilecv4j.tutorials;

import ai.kognition.pilecv4j.image.CvMat;

public class BaseTest {

    /**
     * Replace this with a URL to your own RTSP camera
     */
    public static final String VIDEO = "rtsp://admin:password@172.16.2.11:554/";

    /**
     * You'll need a video at this location. Or you can change this to point
     * to your own test video.
     */
    public static final String VIDEO_FILE = "/tmp/test-video.mp4";

    static {
        CvMat.initOpenCv();
    }

}
