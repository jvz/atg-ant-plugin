package atg.tools.ant.plugin;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class AtgSystem {

    private final File atgDir;
    private final File baseDir;
    private final Map<String, AtgModule> moduleCache = new HashMap<String, AtgModule>();

    public AtgSystem(String atgHome) {
        this(new File(atgHome));
    }

    public AtgSystem(File atgDir, File baseDir) {
        this.atgDir = atgDir;
        this.baseDir = baseDir;
        if (!atgDir.isDirectory()) {
            throw new IllegalArgumentException("Specified ATG home is not a directory: " + atgDir);
        }
    }

    public AtgSystem(File atgDir) {
        this(atgDir, new File("."));
    }

    public AtgModule getModule(String moduleName) {
        if (moduleName.equals(".")) {
            String modulePath = baseDir.getAbsolutePath();
            String atgPath = atgDir.getAbsolutePath() + File.separatorChar;
            if (!modulePath.startsWith(atgPath)) {
                throw new IllegalArgumentException("Current directory \".\" is not part of atg system: " + modulePath + " is not in " + atgPath);
            }
            moduleName = modulePath.substring(atgPath.length()).replace(File.separatorChar, '.');
        }
        AtgModule module = moduleCache.get(moduleName);
        if (module == null) {
            module = new AtgModule(this, moduleName);
            moduleCache.put(moduleName, module);
        }
        return module;
    }

    public File getAtgHomeDirectory() {
        return atgDir;
    }

    public List<File> getClassPath(String moduleName) {
        return getClassPath(singletonList(getModule(moduleName)));
    }

    private List<File> getClassPath(List<AtgModule> moduleList) {
        List<AtgModule> moduleOrder = getAllModules(moduleList);
        moduleOrder = new ArrayList<AtgModule>(moduleOrder);
        Collections.reverse(moduleOrder);
        List<File> classPath = new ArrayList<File>();
        for (AtgModule atgModule : moduleOrder) {
            classPath.addAll(atgModule.getLocalClassPath());
        }
        return classPath;
    }

    public List<AtgModule> getAllModules(String moduleName) {
        return getAllModules(singletonList(getModule(moduleName)));
    }

    public List<AtgModule> getAllModules(List<AtgModule> moduleList) {
        DependencyBuilder builder = new DependencyBuilder(moduleList);
        return builder.getModuleOrder();
    }

    public List<AtgModule> getModuleList(String moduleListString) {
        String[] moduleStrings = moduleListString.split("[,;:]");
        List<AtgModule> modules = new ArrayList<AtgModule>(moduleStrings.length);
        for (String moduleName : moduleStrings) {
            if (moduleName.endsWith("*")) {
                modules.addAll(findModules(moduleName));
            }
            else {
                modules.add(getModule(moduleName));
            }
        }
        return modules;
    }

    private List<AtgModule> findModules(String wildcard) {
        String withoutWildcard = wildcard.substring(0, wildcard.length() - 1);
        boolean recursive = false;
        if (withoutWildcard.endsWith("*")) {
            recursive = true;
            withoutWildcard = wildcard.substring(0, withoutWildcard.length() - 1);
        }
        if (withoutWildcard.contains("*")) {
            throw new IllegalArgumentException("ATG Module wildcards only allow * or ** at the end: " + wildcard);
        }
        int lastDot = withoutWildcard.lastIndexOf('.');
        String dirRoot = lastDot == -1 ? "" : withoutWildcard.substring(0, lastDot).replace('.', '/');
        String filePrefix = withoutWildcard.substring(lastDot + 1);
        List<AtgModule> results = new ArrayList<AtgModule>();
        File currDir = new File(atgDir, dirRoot);
        findModules(currDir, filePrefix, results, recursive, true);
        return results;
    }

    private void findModules(File currDir,
                             String filePrefix,
                             List<AtgModule> results,
                             boolean recursive,
                             boolean firstLevel) {
        if (!firstLevel || currDir.getName().startsWith(filePrefix)) {
            AtgModule module = testModule(currDir);
            if (module != null) {
                results.add(module);
            }
        }

        if (firstLevel || recursive) {
            File[] fileList = currDir.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (file.isDirectory() && !file.getName().contains(".")) {
                        if (recursive || file.getName().startsWith(filePrefix)) {
                            findModules(file, filePrefix, results, recursive, false);
                        }
                    }
                }
            }
        }
    }

    @Nullable
    private AtgModule testModule(File currDir) {
        try {
            return getModule(currDir);
        } catch (Throwable ignored) {
        }
        return null;
    }

    public AtgModule getModule(File currDir)
            throws IOException {
        String myPath = currDir.getCanonicalPath();
        String atgPath = atgDir.getCanonicalPath() + File.separatorChar;
        if (!myPath.startsWith(atgPath)) {
            throw new IllegalArgumentException("Module not within root: " + myPath + " not in " + atgPath);
        }
        String moduleName = myPath.substring(atgPath.length());
        moduleName = moduleName.replace(File.separatorChar, '.');
        return getModule(moduleName);
    }

}
