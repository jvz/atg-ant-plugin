package atg.tools.ant.util;

import atg.tools.ant.types.Module;
import atg.tools.ant.types.ModuleCollection;
import org.apache.tools.ant.types.Resource;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * @author msicker
 * @version 2.0
 */
public class DependencyBuilder {

    private final ModuleCollection modules = new ModuleCollection();

    private final Deque<Module> stack = new ArrayDeque<Module>();

    public ModuleCollection build() {
        return modules;
    }

    public DependencyBuilder withModules(final ModuleCollection modules) {
        final List<Module> copy = new ArrayList<Module>(modules.size());
        for (final Resource module : modules) {
            copy.add((Module) module);
        }
        Collections.reverse(copy);
        for (final Module module : copy) {
            addDependency(module);
        }
        return this;
    }

    private void addDependency(final Module module) {
        final StringBuilder sb = new StringBuilder("Circular module dependencies: ");
        boolean circular = false;
        for (final Module mod : stack) {
            if (mod.equals(module)) {
                circular = true;
            }
            if (circular) {
                sb.append(mod.getFile()).append(" -> ");
            }
        }
        if (circular) {
            sb.append(module.getFile());
            throw new IllegalArgumentException(sb.toString());
        }
        if (!modules.contains(module)) {
            stack.push(module);
            withModules(module.getRequiredModules()); // aww yeah double recursion
            modules.add(module);
            stack.pop();
        }
    }
}
