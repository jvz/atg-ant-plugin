package atg.tools.ant.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.Manifest;

public class AtgModule {

    private final String name;
    private Manifest manifest;
    private final AtgSystem atgSystem;
    private File moduleDir;

    public AtgModule(AtgSystem atgSystem, String moduleName) {
        this.atgSystem = atgSystem;
        this.name = moduleName;
        moduleDir = new File(atgSystem.getAtgHomeDirectory(), moduleName.replace(".", "/"));
        File manifestFile = new File(moduleDir, "/META-INF/MANIFEST.MF");
        if (!manifestFile.isFile()) {
            throw new IllegalArgumentException("Module " + moduleName + " can't be found: Can't find " + manifestFile);
        }
        try {
            FileInputStream manifestStream = new FileInputStream(manifestFile);
            try {
                manifest = new Manifest(manifestStream);
            } finally {
                manifestStream.close();
            }
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Error reading module's manifest " + manifestFile);
        }
    }

    public List<AtgModule> getRequired() {
        String requiredString = manifest.getMainAttributes().getValue("ATG-Required");
        if (requiredString == null) {
            return Collections.emptyList();
        }
        String[] moduleNames = requiredString.split("\\s+");
        List<AtgModule> moduleList = new ArrayList<AtgModule>(moduleNames.length);
        for (String name : moduleNames) {
            AtgModule module = atgSystem.getModule(name);
            moduleList.add(module);
        }
        return moduleList;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<File> getLocalClassPath() {
        String cpString = manifest.getMainAttributes().getValue("ATG-Class-Path");
        if (cpString == null) {
            return Collections.emptyList();
        }
        String[] cpNames = cpString.split("\\s+");
        List<File> cpList = new ArrayList<File>(cpNames.length);
        for (String name : cpNames) {
            File cpEntry = new File(moduleDir, name);
            cpList.add(cpEntry);
        }
        return cpList;
    }

    public File getDirectory() {
        return moduleDir;
    }

}
