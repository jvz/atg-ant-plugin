package atg.tools.ant.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class DependencyBuilder {

    private final List<AtgModule> moduleOrder = new ArrayList<AtgModule>();
    private final Stack<AtgModule> stackedModules = new Stack<AtgModule>();

    public DependencyBuilder(List<AtgModule> moduleList) {
        depthFirstAddAllModules(moduleList);
    }

    public List<AtgModule> getModuleOrder() {
        return moduleOrder;
    }

    private void depthFirstAddAllModules(List<AtgModule> moduleList) {
        moduleList = new ArrayList<AtgModule>(moduleList);
        Collections.reverse(moduleList);
        for (AtgModule module : moduleList) {
            depthFirstAddModule(module);
        }
    }

    private void depthFirstAddModule(AtgModule module) {
        if (stackedModules.contains(module)) {
            StringBuilder sequence = new StringBuilder("Circular module dependency: ");
            for (int i = stackedModules.indexOf(module); i < stackedModules.size(); i++) {
                sequence.append(stackedModules.get(i).getName()).append(" -> ");
            }
            sequence.append(module.getName());
            throw new IllegalArgumentException(sequence.toString());
        }

        if (!moduleOrder.contains(module)) {
            stackedModules.push(module);
            depthFirstAddAllModules(module.getRequired());
            moduleOrder.add(module);
            stackedModules.pop();
        }
    }

}
