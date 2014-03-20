package atg.tools.ant.types;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

import java.util.Iterator;

/**
 * @author msicker
 * @version 2.0
 */
public class ClassPath
        extends DataType
        implements ResourceCollection, Iterable<Resource> {
    // implements Iterable for convenience due to old versions of ant

    private AtgInstallation atgInstallation;

    private ModuleCollection modules;

    private Path path;

    public void setAtg(final AtgInstallation atgInstallation) {
        this.atgInstallation = atgInstallation;
    }

    public void setAtg(final Reference reference) {
        setAtg((AtgInstallation) reference.getReferencedObject());
    }

    public ModuleCollection createModules() {
        modules = new ModuleCollection();
        if (atgInstallation != null) {
            modules.setAtg(atgInstallation);
        }
        return modules;
    }

    private Path getPath() {
        if (path == null) {
            path = new Path(getProject());
            final ModuleCollection deps = modules.getDependencies();
            for (Resource dep : deps) {
                path.add(((Module) dep).getLocalClassPath());
            }
        }
        return path;
    }

    @Override
    public Iterator<Resource> iterator() {
        // yay old ant
        @SuppressWarnings("unchecked")
        final Iterator<Resource> iterator = getPath().iterator();
        return iterator;
    }

    @Override
    public int size() {
        return getPath().size();
    }

    @Override
    public boolean isFilesystemOnly() {
        return getPath().isFilesystemOnly();
    }
}
