package atg.tools.ant.tasks;

import atg.tools.ant.types.AtgInstallation;
import atg.tools.ant.types.Module;
import atg.tools.ant.types.ModuleCollection;
import atg.tools.ant.util.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Reference;

import java.util.Map;

import static atg.tools.ant.util.ModuleUtils.MODULE_NAME_EXTRACTOR;

/**
 * Ant task to generate a comma-delimited list of required ATG modules for a given ModuleCollection.
 *
 * @author msicker
 * @version 2.0
 */
public class RequiredModules
        extends Task {

    private AtgInstallation atg;

    private ModuleCollection modules;

    private String property;

    /**
     * Sets the root ATG installation. Not required if using a reference to a module collection.
     *
     * @param atg root ATG installation.
     */
    public void setAtg(final AtgInstallation atg) {
        this.atg = atg;
    }

    /**
     * Sets the root ATG installation by reference. Not required if using a reference to a module
     * collection.
     *
     * @param reference reference to root ATG installation.
     */
    public void setAtg(final Reference reference) {
        setAtg((AtgInstallation) reference.getReferencedObject());
    }

    /**
     * Specifies the module list to use for building the property.
     *
     * @param modules list of modules to include.
     */
    public void setModules(final ModuleCollection modules) {
        this.modules = modules;
        if (atg != null) {
            modules.setAtg(atg);
        }
    }

    /**
     * Specifies the module list by reference.
     *
     * @param reference reference to module list.
     */
    public void setModules(final Reference reference) {
        setModules((ModuleCollection) reference.getReferencedObject());
    }

    public ModuleCollection createModules() {
        if (atg == null) {
            log("No ATG installation was specified for this task.", Project.MSG_ERR);
            return null;
        }
        modules = new ModuleCollection();
        modules.setProject(getProject());
        modules.setAtg(atg);
        return modules;
    }

    /**
     * Specifies the project property to set with a comma-separated list of required modules using
     * the configured module collection.
     *
     * @param property property name to set.
     */
    public void setProperty(final String property) {
        this.property = property;
    }

    @Override
    public void execute()
            throws BuildException {
        final String moduleList = StringUtils.join(',', modules.iterator(), MODULE_NAME_EXTRACTOR);
        log("Calculated module list: " + moduleList, Project.MSG_DEBUG);
        getProject().setProperty(property, moduleList);
    }
}
