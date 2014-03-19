package atg.tools.ant.types;

import atg.tools.ant.util.ModuleUtils;
import atg.tools.ant.util.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.resources.FileResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import static java.util.jar.Attributes.Name;

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

    private static final Name ATG_REQUIRED = new Name(ModuleUtils.ATG_REQUIRED);

    private static final Name ATG_CLASS_PATH = new Name(ModuleUtils.ATG_CLASS_PATH);

    private AtgInstallation atg;

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
        setName(module);
        setFile(new File(getBaseDir(), ModuleUtils.moduleNameToPath(module)));
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
        final File metaInf = new File(getFile(), "META-INF");
        if (!metaInf.exists()) {
            error("File `" + metaInf + "' does not exist.");
            throw new BuildException(
                    "The module located at `" + getFile() + "' does not have a META-INF directory."
            );
        }
        final File manifestFile = new File(metaInf, "MANIFEST.MF");
        if (!manifestFile.exists()) {
            error("File `" + manifestFile + "' does not exist.");
            throw new BuildException(
                    "The module located at `" + getFile() + "' does not have a MANIFEST.MF file."
            );
        }
        debug("Reading manifest file from `" + manifestFile + "'.");
        final FileInputStream fis = new FileInputStream(manifestFile);
        manifest = new Manifest(fis);
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

    private String[] splitAttribute(final Name name) {
        return WHITESPACE.split(StringUtils.trimToEmpty(getAttribute(name)));
    }

    private String getAttribute(final Name name) {
        debug("Looking for attribute named `" + name + "'.");
        final String attribute = getManifest().getMainAttributes().getValue(name);
        if (attribute == null) {
            debug("Attribute was missing!");
            return "";
        }
        return attribute;
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
