package atg.tools.ant.types;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author msicker
 * @version 2.0
 */
@RunWith(Parameterized.class)
public class AtgInstallationTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][]{
                        { "Test/Module", "Test.Module" },
                        { "Module", "Module" },
                        { "Longer/Test/Versioned", "Longer.Test.Versioned" },
                        { "DAF/Endeca/Builder/What", "DAF.Endeca.Builder.What" }
                }
        );
    }

    private final String pathToModule;
    private final String nameOfModule;

    public AtgInstallationTest(final String pathToModule, final String nameOfModule) {
        this.pathToModule = pathToModule;
        this.nameOfModule = nameOfModule;
    }

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private AtgInstallation atg;

    private File home;

    private File moduleLocation;

    @Before
    public void setUp()
            throws Exception {
        atg = new AtgInstallation();
        home = temp.newFolder();
        atg.setHome(home);
        moduleLocation = new File(home, pathToModule);
    }

    @Test
    public void testGetHome()
            throws Exception {
        assertThat(atg.getHome(), is(equalTo(home)));
    }

    @Test
    public void testGetModule()
            throws Exception {
        final Module module = atg.getModule(nameOfModule);
        assertThat(module.getBaseDir(), is(equalTo(home)));
        assertThat(module.getModule(), is(equalTo(nameOfModule)));
        assertThat(module.getFile(), is(equalTo(moduleLocation)));
    }
}
