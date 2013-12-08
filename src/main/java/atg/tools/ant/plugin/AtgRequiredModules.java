package atg.tools.ant.plugin;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DirSet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AtgRequiredModules
        extends DirSet {

    private File atgHome;
    private String modules = ".";
    private List<File> moduleDirectoryList;
    private String filterString;

    private void buildModuleDirectoryList() {
        if (moduleDirectoryList == null) {
            if (atgHome == null) {
                throw new BuildException("no atgHome set");
            }
            ModuleFilter filter = null;
            if (filterString != null) {
                filter = ModuleFilter.parseStringList(filterString);
            }
            AtgSystem atgSystem = new AtgSystem(atgHome, getProject().getBaseDir());
            List<AtgModule> allModules = atgSystem.getAllModules(atgSystem.getModuleList(modules));
            moduleDirectoryList = new ArrayList<File>(allModules.size());
            for (AtgModule m : allModules) {
                if (filter != null) {
                    if (!filter.match(m)) {
                        continue;
                    }
                }
                moduleDirectoryList.add(m.getDirectory());
            }
        }
    }

    public void setAtgHome(File atgHome) {
        this.atgHome = atgHome;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

    public void setFilters(String filterString) {
        this.filterString = filterString;
    }

    @Override
    public boolean isFilesystemOnly() {
        return true; // Everything comes from the local file system
    }

    @Override
    public int size() {
        buildModuleDirectoryList();
        return moduleDirectoryList.size();
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Iterator iterator() {
        buildModuleDirectoryList();
        return moduleDirectoryList.iterator();
    }

}
