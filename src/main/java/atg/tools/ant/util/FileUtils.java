package atg.tools.ant.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Do we care if a stream can't be closed? Not really.
 *
 * @author msicker
 * @version 2.0
 */
public class FileUtils {

    public static void closeSilently(final InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (final IOException ignored) {
            }
        }
    }

    public static void closeSilently(final OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (final IOException ignored) {
            }
        }
    }
}
