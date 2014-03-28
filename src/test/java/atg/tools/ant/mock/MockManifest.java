package atg.tools.ant.mock;

import atg.tools.ant.util.IdentityFeatureExtractor;
import atg.tools.ant.util.ManifestUtils;
import atg.tools.ant.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static atg.tools.ant.util.AtgAttribute.ATG_REQUIRED;
import static org.junit.Assert.assertTrue;

/**
 * Helper class to generate a manifest file for unit tests.
 *
 * @author msicker
 * @version 2.0
 */
public class MockManifest {

    private static final IdentityFeatureExtractor<String> STRING_IDENTITY_EXTRACTOR =
            new IdentityFeatureExtractor<String>();

    private final Manifest manifest = new Manifest();
    private final Attributes attributes = manifest.getMainAttributes();

    private final File rootDirectory;

    public MockManifest(final File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public MockManifest withRequiredModules(final Iterable<String> moduleNames) {
        final String modules = StringUtils.join(' ', moduleNames.iterator(), STRING_IDENTITY_EXTRACTOR);
        this.attributes.put(ATG_REQUIRED.getName(), modules);
        return this;
    }

    public void save()
            throws IOException {
        if (!this.rootDirectory.exists()) {
            assertTrue(this.rootDirectory.mkdirs());
        }
        final File manifest = ManifestUtils.touchManifestIn(this.rootDirectory);
        final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(manifest));
        try {
            this.manifest.write(stream);
        } finally {
            stream.close();
        }
    }
}
