package atg.tools.ant.types;

import atg.tools.ant.util.AtgAttribute;
import atg.tools.ant.util.ManifestUtils;
import atg.tools.ant.util.ModuleUtils;
import atg.tools.ant.util.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.resources.FileResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import static atg.tools.ant.util.AtgAttribute.ATG_CLASS_PATH;
import static atg.tools.ant.util.AtgAttribute.ATG_REQUIRED;

/**
 * A Module is a FileResource whose {@code basedir} is the {@link AtgInstallation} and whose
 * {@code file} is the module directory.
 *
 * @author msicker
 * @version 2.0
 */
public class Module
        extends FileResource {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private AtgInstallation atg;

    private String module;

    private Manifest manifest;

    /**
     * Sets the location of the root ATG installation directory.
     *
     * @param atgInstallation
     *         location of ATG installation.
     */
    public void setAtg(final AtgInstallation atgInstallation) {
        this.atg = atgInstallation;
        setBaseDir(atgInstallation.getFile());
    }

    public void setAtg(final Reference reference) {
        setAtg((AtgInstallation) reference.getReferencedObject());
    }

    /**
     * Sets the location of this module.
     *
     * @param module
     *         name of ATG module.
     */
    public void setModule(final String module) {
        this.module = module;
        setFile(new File(getBaseDir(), ModuleUtils.moduleNameToPath(module)));
    }

    public String getModule() {
        return module;
    }

    /**
     * Parses this module's manifest file and returns it.
     *
     * @return this module's manifest file.
     */
    private Manifest getManifest() {
        if (manifest == null) {
            try {
                parseManifestFile();
            } catch (FileNotFoundException e) {
                throw new BuildException(
                        "No MANIFEST.MF file could be found for module at `" + getFile() + "'.", e
                );
            } catch (IOException e) {
                error("There was a problem parsing the manifest file.");
                throw new BuildException(e.getLocalizedMessage(), e);
            }
        }
        return manifest;
    }

    private void parseManifestFile()
            throws IOException {
        final File manifestFile = ManifestUtils.getManifestIn(getFile());
        if (!manifestFile.exists()) {
            error("File `" + manifestFile + "' does not exist. Can't parse module `" + getModule() + "'.");
            throw new BuildException();
        }
        debug("Reading manifest file from `" + manifestFile + "'.");
        manifest = ManifestUtils.load(manifestFile);
    }

    /**
     * Parses the ATG modules required by this module's manifest file.
     *
     * @return collection of required modules.
     */
    public ModuleCollection getRequiredModules() {
        debug("Locating required modules for module `" + getFile() + "'.");
        final ModuleCollection requiredModules = new ModuleCollection();
        final String[] modules = splitAttribute(ATG_REQUIRED);
        for (String module : modules) {
            if (StringUtils.isNotBlank(module)) {
                debug("Adding module `" + module + "' to collection.");
                requiredModules.add(atg.getModule(module));
            }
        }
        return requiredModules;
    }

    /**
     * Parses the ATG class path for this module. Note that this is not a transitive listing.
     *
     * @return path containing the libraries specified in the manifest file.
     */
    public Path getLocalClassPath() {
        debug("Calculating local class path for module `" + getFile() + "'.");
        final FileList files = new FileList();
        files.setDir(getFile());
        final String[] fileNames = splitAttribute(ATG_CLASS_PATH);
        for (String fileName : fileNames) {
            debug("Adding file `" + fileName + "' to path.");
            files.addConfiguredFile(named(fileName));
        }
        final Path path = new Path(getProject());
        path.add(files);
        return path;
    }

    private String[] splitAttribute(final AtgAttribute attribute) {
        return WHITESPACE.split(StringUtils.trimToEmpty(attribute.extractValueFrom(getManifest())));
    }

    private static FileList.FileName named(final String name) {
        final FileList.FileName fileName = new FileList.FileName();
        fileName.setName(name);
        return fileName;
    }

    private void debug(final String message) {
        log(message, Project.MSG_DEBUG);
    }

    private void error(final String message) {
        log(message, Project.MSG_ERR);
    }

}
