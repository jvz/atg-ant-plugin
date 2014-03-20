package atg.tools.ant.tasks;

import atg.tools.ant.types.AtgInstallation;
import atg.tools.ant.types.ModuleCollection;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.taskdefs.ManifestTask;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import static atg.tools.ant.util.AtgAttribute.ATG_CLASS_PATH;
import static atg.tools.ant.util.AtgAttribute.ATG_CONFIG_PATH;
import static atg.tools.ant.util.AtgAttribute.ATG_REQUIRED;
import static atg.tools.ant.util.ModuleUtils.MODULE_NAME_EXTRACTOR;
import static atg.tools.ant.util.ModuleUtils.RESOURCE_NAME_EXTRACTOR;

/**
 * @author msicker
 * @version 2.0
 */
public class GenerateManifest
        extends ManifestTask {

    private AtgInstallation atg;

    private ModuleCollection modules;

    private Path classpath;

    private Path configpath;

    public void setAtg(final AtgInstallation atg) {
        this.atg = atg;
    }

    public void setAtg(final Reference reference) {
        setAtg((AtgInstallation) reference.getReferencedObject());
    }

    public void setModules(final ModuleCollection modules) {
        this.modules = modules;
    }

    public void setModules(final Reference reference) {
        setModules((ModuleCollection) reference.getReferencedObject());
    }

    public ModuleCollection createModules() {
        modules = new ModuleCollection();
        if (atg != null) {
            modules.setAtg(atg);
        }
        return modules;
    }

    public void setClasspath(final Path classpath) {
        this.classpath = classpath;
    }

    public void setClasspathRef(final Reference reference) {
        setClasspath((Path) reference.getReferencedObject());
    }

    public Path createClasspath() {
        classpath = new Path(getProject());
        return classpath;
    }

    public void setConfigpath(final Path configpath) {
        this.configpath = configpath;
    }

    public void setConfigpathRef(final Reference reference) {
        setConfigpath((Path) reference.getReferencedObject());
    }

    public Path createConfigpath() {
        configpath = new Path(getProject());
        return configpath;
    }

    @Override
    public void execute()
            throws BuildException {
        if (modules == null) {
            throw new BuildException("No modules were defined.");
        }
        if (classpath == null) {
            throw new BuildException("No classpath was defined.");
        }
        try {
            addAtgAttributes();
        } catch (ManifestException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        }
        super.execute();
    }

    @SuppressWarnings("unchecked")
    private void addAtgAttributes()
            throws ManifestException {
        addConfiguredAttribute(ATG_REQUIRED.using(modules.iterator(), MODULE_NAME_EXTRACTOR));
        addConfiguredAttribute(ATG_CLASS_PATH.using(classpath.iterator(), RESOURCE_NAME_EXTRACTOR));
        addConfiguredAttribute(ATG_CONFIG_PATH.using(configpath.iterator(), RESOURCE_NAME_EXTRACTOR));
    }
}
