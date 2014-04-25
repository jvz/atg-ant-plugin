package atg.tools.ant.util;

import atg.tools.ant.types.Module;
import atg.tools.ant.types.ModuleCollection;
import org.apache.tools.ant.types.Resource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author msicker
 * @version 2.0
 */
public class DependencyBuilder {

    private final ModuleCollection modules = new ModuleCollection();

    private final Deque<Module> stack = new ArrayDeque<Module>();

    private final AtomicInteger index = new AtomicInteger(0);

    private final Map<Module, NodeData> connections = new ConcurrentHashMap<Module, NodeData>();

    public ModuleCollection build() {
        return modules;
    }

    public DependencyBuilder withModules(final ModuleCollection modules) {
        final List<Module> copy = new ArrayList<Module>(modules.size());
        for (final Resource module : modules) {
            copy.add((Module) module);
        }
        Collections.reverse(copy);
        // Tarjan's strongly connected component algorithm
        // https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm
        for (final Module module : copy) {
            if (!connections.containsKey(module)) {
                strongConnect(module);
            }
        }
        return this;
    }

    private void strongConnect(final Module module) {
        if (!modules.contains(module)) {
            modules.add(module);
        }
        connections.put(module, new NodeData(this.index.getAndIncrement()));
        stack.push(module);
        final NodeData v = connections.get(module);
        final ModuleCollection moduleDependencies = module.getRequiredModules();
        for (final Resource moduleDependency : moduleDependencies) {
            final Module dependency = (Module) moduleDependency;
            if (!connections.containsKey(dependency)) {
                // dependency not yet visited
                strongConnect(dependency);
                final NodeData w = connections.get(dependency);
                v.updateLowLink(w.getLowLink());
            } else if (stack.contains(dependency)) {
                // dependency is in current strongly connected component
                final NodeData w = connections.get(dependency);
                v.updateLowLink(w.getIndex());
            }
        }
        if (v.isRootNode() && !stack.isEmpty()) {
            final Collection<Module> cycle = new ArrayList<Module>();
            while (!stack.isEmpty()) {
                final Module dependency = stack.pop();
                cycle.add(dependency);
                if (dependency.equals(module)) {
                    break;
                }
            }
            if (!cycle.isEmpty()) {
                final StringBuilder sb = new StringBuilder("Found a strongly connected component in the dependency graph: ");
                for (final Module m : cycle) {
                    // FIXME: -> or <- ?
                    sb.append(m.getFile()).append(" <- ");
                }
                sb.append(module.getFile());
                throw new DependencyException(sb.toString());
            }
        }
    }

    private static class NodeData {
        private final int index;
        private int lowLink;

        private NodeData(final int index) {
            this.index = index;
            this.lowLink = index;
        }

        public int getIndex() {
            return index;
        }

        public int getLowLink() {
            return lowLink;
        }

        public void updateLowLink(final int other) {
            this.lowLink = Math.min(this.lowLink, other);
        }

        public boolean isRootNode() {
            return this.index == this.lowLink;
        }
    }
}
