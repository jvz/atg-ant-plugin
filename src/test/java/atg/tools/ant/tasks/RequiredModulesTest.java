package atg.tools.ant.tasks;

import atg.tools.ant.types.AtgInstallation;
import atg.tools.ant.types.Module;
import atg.tools.ant.types.ModuleCollection;
import atg.tools.ant.util.ManifestUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

/**
 * @author msicker
 * @version 2.0
 */
@RunWith(JUnit4.class)
@Ignore
public class RequiredModulesTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private AtgInstallation atg;

    private RequiredModules requiredModules;

    private ModuleCollection modules;

    private Module core;

    private Module dependant;

    private Module son;

    private Module daughter;

    @Before
    public void setUp()
            throws Exception {
        atg = new AtgInstallation();
        atg.setHome(temp.getRoot());
        requiredModules = new RequiredModules();
        requiredModules.setAtg(atg);
        modules = requiredModules.createModules();
        core = modules.createModule();
        dependant = modules.createModule();
        son = modules.createModule();
    }

    private void createMockManifestFile(final Module module) {
        final File manifest = ManifestUtils.getManifestIn(module.getFile());
    }
}
