package atg.tools.ant.util;

import org.apache.tools.ant.BuildException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Manifest;

/**
 * Utility class for dealing with JAR manifest files.
 *
 * @author msicker
 * @version 2.0
 */
public final class ManifestUtils {

    public static final String META_INF = "META-INF";
    public static final String MANIFEST_MF = "MANIFEST.MF";

    private ManifestUtils() {
    }

    /**
     * Gets a File reference to the {@code META-INF/MANIFEST.MF} file within the given directory.
     *
     * @param baseDirectory directory to locate manifest file within.
     *
     * @return manifest file object (which may or may not exist).
     *
     * @throws java.lang.IllegalArgumentException if {@code baseDirectory} is not an existing directory, or if
     *                                            {@code baseDirectory} contains a file called {@code META-INF} which is
     *                                            not a directory.
     */
    public static File getManifestIn(final File baseDirectory) {
        verifyDirectoryExists(baseDirectory);
        final File metaInf = new File(baseDirectory, META_INF);
        if (metaInf.exists() && !metaInf.isDirectory()) {
            throw new BuildException("The location `" + metaInf + "' already exists, but is not a directory.");
        }
        return new File(metaInf, MANIFEST_MF);
    }

    public static File touchManifestIn(final File baseDirectory) {
        verifyDirectoryExists(baseDirectory);
        final File metaInf = new File(baseDirectory, META_INF);
        if (!(metaInf.exists() || metaInf.mkdir())) {
            throw new BuildException("Could not create directory `" + metaInf + "'.");
        }
        final File manifest = new File(metaInf, MANIFEST_MF);
        if (manifest.exists()) {
            verify("Couldn't touch file `" + manifest + "'.", manifest.setLastModified(System.currentTimeMillis()));
        } else {
            try {
                verify("Couldn't create file `" + manifest + "'.", manifest.createNewFile());
            } catch (final IOException e) {
                throw new BuildException("Could not create file: " + manifest, e);
            }
        }
        return manifest;
    }

    private static void verifyDirectoryExists(final File baseDirectory) {
        if (!baseDirectory.isDirectory()) {
            throw new BuildException("Provided file `" + baseDirectory.getAbsolutePath() + "' is not a valid directory.");
        }
    }

    private static void verify(final String message, final boolean b) {
        if (!b) {
            throw new BuildException(message);
        }
    }

    /**
     * Loads a given manifest file into a Manifest.
     *
     * @param file file containing manifest metadata.
     *
     * @return said file parsed into a Manifest object.
     *
     * @throws IOException if the given file does not exist or is an invalid manifest file.
     */
    public static Manifest load(final File file)
            throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            return new Manifest(bis);
        } finally {
            FileUtils.closeSilently(bis);
        }
    }

}
