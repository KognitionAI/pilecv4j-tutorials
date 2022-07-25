package ai.kognition.pilecv4j.tutorials;

import org.junit.Test;

import ai.kognition.pilecv4j.image.CvMat;
import ai.kognition.pilecv4j.image.ImageFile;
import ai.kognition.pilecv4j.image.display.ImageDisplay;

public class Jug03_bulk extends BaseTest {
    public static final String TEST_IMAGE = "target/test-classes/HD-Country-Image.jpg";

    @Test
    public void test() throws Exception {
        try(final CvMat image = ImageFile.readMatFromFile(TEST_IMAGE);

            ImageDisplay id = new ImageDisplay.Builder().build();
            ImageDisplay od = new ImageDisplay.Builder().build();) {

            od.update(image);

            /* Direct bulk pixel access */
            image.bulkAccess(bb -> {
                final int numBytes = bb.capacity();
                for(int i = 0; i < numBytes; i += 3) {
                    bb.put(i, (byte)0);
                    bb.put(i + 1, (byte)0);
                }
            });
            // ===========================

            id.update(image);
            id.waitUntilClosed();
        }
    }
}
