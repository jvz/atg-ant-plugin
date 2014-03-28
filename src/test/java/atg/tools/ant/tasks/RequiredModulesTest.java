package atg.tools.ant.tasks;

import atg.tools.ant.mock.MockManifest;
import atg.tools.ant.types.AtgInstallation;
import atg.tools.ant.types.Module;
import atg.tools.ant.types.ModuleCollection;
import org.apache.tools.ant.Project;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author msicker
 * @version 2.0
 */
@RunWith(JUnit4.class)
public class RequiredModulesTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private Project project;

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
        project = new Project();
        setUpAtgInstallation();
        setUpDirectories();
        setUpModules();
        setUpCore();
        setUpDependant();
        setUpSon();
        setUpDaughter();
        setUpRequiredModules();
    }

    private void setUpAtgInstallation() {
        atg = new AtgInstallation();
        atg.setProject(project);
        atg.setHome(temp.getRoot());
    }

    private void setUpDirectories() {
        mkdir("Core");
        mkdir("Dependant");
        mkdir("Son");
        mkdir("Daughter");
    }

    private void mkdir(final String module) {
        assertTrue(new File(atg.getHome(), module).mkdir());
    }

    private void setUpModules() {
        modules = new ModuleCollection();
        modules.setProject(project);
        modules.setAtg(atg);
    }

    private void setUpCore()
            throws IOException {
        core = modules.createModule();
        core.setModule("Core");
        createMockManifestFile(core).save();
    }

    private void setUpDependant()
            throws IOException {
        dependant = modules.createModule();
        dependant.setModule("Dependant");
        createMockManifestFile(dependant).withRequiredModules(Collections.singleton("Core")).save();
    }

    private void setUpSon()
            throws IOException {
        son = modules.createModule();
        son.setModule("Son");
        createMockManifestFile(son).withRequiredModules(Arrays.asList("Core", "Dependant")).save();
    }

    private void setUpDaughter()
            throws IOException {
        daughter = modules.createModule();
        daughter.setModule("Daughter");
        createMockManifestFile(daughter).withRequiredModules(Collections.singleton("Dependant")).save();
    }

    private MockManifest createMockManifestFile(final Module module) {
        return new MockManifest(module.getFile());
    }

    private void setUpRequiredModules() {
        requiredModules = new RequiredModules();
        requiredModules.setProject(project);
        requiredModules.setAtg(atg);
    }

    @Test
    public void testNoRequiredModules()
            throws Exception {
        final String property = "no.required.modules";
        requiredModules.setProperty(property);
        requiredModules.createModules(); // ignored return value to simulate no modules added
        requiredModules.execute();
        final String actual = project.getProperty(property);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testCoreHasNoDependencies()
            throws Exception {
        final String property = "core.required.modules";
        requiredModules.setProperty(property);
        requiredModules.createModules().add(core);
        requiredModules.execute();
        final String actual = project.getProperty(property);
        assertThat(actual, equalTo("Core"));
    }

    @Test
    public void testDependantHasCoreDependency()
            throws Exception {
        final String property = "dependant.required.modules";
        requiredModules.setProperty(property);
        requiredModules.createModules().add(dependant);
        requiredModules.execute();
        final String actual = project.getProperty(property);
        assertThat(actual, containsString("Dependant"));
        assertThat(actual, not(containsString("Core")));
        assertThat(actual, not(containsString("Son")));
        assertThat(actual, not(containsString("Daughter")));
    }
}
