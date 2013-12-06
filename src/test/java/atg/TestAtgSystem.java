package atg;

import atg.tools.ant.plugin.AtgModule;
import atg.tools.ant.plugin.AtgSystem;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestAtgSystem {

    private static final String ATG_HOME = System.getenv("ATG_HOME");

    private AtgSystem atgSystem;

    @Before
    public void initialize() {
        atgSystem = new AtgSystem(ATG_HOME);
    }

    @Test
    public void loadRootModule()
            throws Exception {
        AtgModule module = atgSystem.getModule("DAS");
        assertEquals("DAS", module.getName());

        List<AtgModule> reqd = module.getRequired();
        assertEquals(0, reqd.size());

        assertEquals(new File(ATG_HOME + "/DAS"), module.getDirectory());
    }

    @Test
    public void loadModule()
            throws Exception {
        AtgModule module = atgSystem.getModule("DCS");
        assertEquals("DCS", module.getName());

        List<AtgModule> reqd = module.getRequired();
        assertEquals(2, reqd.size());
        assertEquals("DPS", reqd.get(0).getName());
        assertEquals("DSS", reqd.get(1).getName());

        assertClassPathList(
                module.getLocalClassPath(),
                ATG_HOME + "/DCS/lib/resources.jar:" + ATG_HOME + "/DCS/lib/classes.jar"
        );
    }

    @Test
    public void loadDottedModule()
            throws Exception {
        AtgModule module = atgSystem.getModule("PublishingAgent.base");
        assertEquals("PublishingAgent.base", module.getName());
    }

    @Test
    public void modulesAreCached()
            throws Exception {
        AtgModule module1 = atgSystem.getModule("DCS");
        AtgModule module2 = atgSystem.getModule("DCS");
        assertSame(module1, module2);
    }

    @Test
    @Ignore("I don't have this ATG module.")
    public void b2cCommerceDependency()
            throws Exception {
        List<AtgModule> modules = atgSystem.getAllModules("B2CCommerce");
        assertModuleList(modules, "DAS:DPS:DSS:DCS:B2CCommerce");
        assertClassPathList(atgSystem.getClassPath("B2CCommerce"),
                ATG_HOME + "/DCS/lib/resources.jar:" +
                        ATG_HOME + "/DCS/lib/classes.jar:" +
                        ATG_HOME + "/DSS/lib/resources.jar:" +
                        ATG_HOME + "/DSS/lib/classes.jar:" +
                        ATG_HOME + "/DPS/lib/resources.jar:" +
                        ATG_HOME + "/DPS/lib/classes.jar:" +
                        ATG_HOME + "/DAS/lib/resources.jar:" +
                        ATG_HOME + "/DAS/lib/classes.jar:" +
                        ATG_HOME + "/DAS/lib/servlet.jar:" +
                        ATG_HOME + "/DAS/lib/ice.jar:" +
                        ATG_HOME + "/DAS/solid/SolidDriver2.1.jar");
    }

    private void assertClassPathList(List<File> classPath, String pathList) {
        StringBuilder builtList = new StringBuilder();
        for (File f : classPath) {
            builtList.append(f).append(":");
        }
        assertEquals(pathList, builtList.subSequence(0, pathList.length()));
    }

    @Test
    public void DeploymentAgentTwoHierarchyDependency()
            throws Exception {
        List<AtgModule> modules = atgSystem.getAllModules("PublishingAgent.base");
        assertModuleList(modules, "DAS:DAF.DeploymentAgent:DPS:DSS:PublishingAgent.base");
        assertClassPathList(atgSystem.getClassPath("PublishingAgent.base"),
                ATG_HOME + "/PublishingAgent/base/lib/agent.jar:" +
                        ATG_HOME + "/PublishingAgent/base/lib/classes.jar:" +
                        ATG_HOME + "/DSS/lib/resources.jar:" +
                        ATG_HOME + "/DSS/lib/classes.jar:" +
                        ATG_HOME + "/DPS/lib/resources.jar:" +
                        ATG_HOME + "/DPS/lib/classes.jar:" +
                        ATG_HOME + "/DAS/lib/resources.jar:" +
                        ATG_HOME + "/DAS/lib/classes.jar:" +
                        ATG_HOME + "/DAS/lib/servlet.jar:" +
                        ATG_HOME + "/DAS/lib/jsp-api.jar:" +
                        ATG_HOME + "/DAS/lib/ice.jar");
    }

    private void assertModuleList(List<AtgModule> modules, String moduleString) {
        StringBuilder builtList = new StringBuilder();
        for (AtgModule m : modules) {
            builtList.append(m.getName()).append(":");
        }
        assertEquals(moduleString, builtList.subSequence(0, builtList.length() - 1));
    }

    @Test
    @Ignore
    public void circularDependency()
            throws Exception {
        AtgSystem atgSystem = new AtgSystem("/Users/piran/Documents/projects/atgant/workspace/AtgAnt/testAtg");
        try {
            @SuppressWarnings({ "unused", "UnusedAssignment" })
            List<AtgModule> modPath = atgSystem.getAllModules("circ1");
            fail("Should have found circular dependency between circ1 and circ2");
        } catch (IllegalArgumentException iae) {
            String message = iae.getMessage();
            assertTrue(message, message.contains("circ1"));
            assertTrue(message, message.contains("circ2"));
        }
    }

    @Test
    @Ignore
    public void parsingModuleList()
            throws Exception {
        AtgSystem atgSystem = new AtgSystem("/Users/piran/Documents/projects/atgant/workspace/AtgAnt/testAtg");
        List<AtgModule> modules = atgSystem.getModuleList("proj1:proj2");
        assertModuleList(modules, "proj1:proj2");
    }

    @Test
    @Ignore
    public void parsingModuleAlternateSeparators()
            throws Exception {
        AtgSystem atgSystem = new AtgSystem("/Users/piran/Documents/projects/atgant/workspace/AtgAnt/testAtg");
        List<AtgModule> modules = atgSystem.getModuleList("proj1;proj2,proj3");
        assertModuleList(modules, "proj1:proj2:proj3");
    }

    @Test
    @Ignore
    public void parsingModuleWithDirectoryWildcards()
            throws Exception {
        AtgSystem atgSystem = new AtgSystem("/Users/piran/Documents/projects/atgant/workspace/AtgAnt/testAtg");
        List<AtgModule> modules = atgSystem.getModuleList("bigproj.*");
        assertModuleList(modules, "bigproj.one:bigproj.three:bigproj.two");
    }

    @Test
    @Ignore
    public void parsingModuleWithSubDirectoryWildcards()
            throws Exception {
        AtgSystem atgSystem = new AtgSystem("/Users/piran/Documents/projects/atgant/workspace/AtgAnt/testAtg");
        List<AtgModule> modules = atgSystem.getModuleList("bigproj.t*");
        assertModuleList(modules, "bigproj.three:bigproj.two");
    }

    @Test
    @Ignore
    public void parsingModuleWithDirectoryDeepWildcards()
            throws Exception {
        AtgSystem atgSystem = new AtgSystem("/Users/piran/Documents/projects/atgant/workspace/AtgAnt/testAtg");
        List<AtgModule> modules = atgSystem.getModuleList("bigproj.**");
        assertModuleList(modules, "bigproj.deep.four:bigproj.one:bigproj.three:bigproj.two");
    }

    @Test
    @Ignore
    public void parsingModuleWithNameWildcards()
            throws Exception {
        AtgSystem atgSystem = new AtgSystem("/Users/piran/Documents/projects/atgant/workspace/AtgAnt/testAtg");
        List<AtgModule> modules = atgSystem.getModuleList("proj*");
        assertModuleList(modules, "proj1:proj2:proj3");
    }

}
