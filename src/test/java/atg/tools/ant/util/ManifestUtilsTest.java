package atg.tools.ant.util;

import org.apache.tools.ant.BuildException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static atg.tools.ant.util.ManifestUtils.MANIFEST_MF;
import static atg.tools.ant.util.ManifestUtils.META_INF;
import static atg.tools.ant.util.ManifestUtils.getManifestIn;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author msicker
 * @version 2.0
 */
public class ManifestUtilsTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testGetManifestInEmptyDirectory()
            throws Exception {
        final File emptyRoot = temp.newFolder();
        final File manifest = getManifestIn(emptyRoot);
        assertThat(manifest.exists(), is(equalTo(false)));
        assertThat(manifest.getName(), is(equalTo(MANIFEST_MF)));
    }

    @Test(expected = BuildException.class)
    public void testGetManifestFailsWithMetaInfFile()
            throws Exception {
        final File root = temp.newFolder();
        final File metaInf = new File(root, META_INF);
        assert metaInf.createNewFile();
        getManifestIn(root);
    }

    @Test
    public void testGetManifestWithExistingMetaInfDirectory()
            throws Exception {
        final File root = temp.newFolder();
        final File metaInf = new File(root, META_INF);
        assert metaInf.mkdir();
        final File manifest = getManifestIn(root);
        assertThat(manifest.exists(), is(equalTo(false)));
        assertThat(manifest.getName(), is(equalTo(MANIFEST_MF)));
    }

    @Test
    public void testGetManifestWithExistManifestFile()
            throws Exception {
        final File root = temp.newFolder();
        final File metaInf = new File(root, META_INF);
        assert metaInf.mkdir();
        final File emptyManifest = new File(metaInf, MANIFEST_MF);
        assert emptyManifest.createNewFile();
        final File manifest = getManifestIn(root);
        assertThat(manifest.exists(), is(equalTo(true)));
        assertThat(manifest, is(equalTo(emptyManifest)));
        assertThat(manifest.getName(), is(equalTo(MANIFEST_MF)));
    }
}
