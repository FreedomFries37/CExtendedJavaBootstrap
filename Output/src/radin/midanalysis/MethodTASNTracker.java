package radin.midanalysis;

import radin.core.semantics.types.methods.CXMethod;

import java.util.HashMap;

/**
 * Tracks methods and their corresponding TypeAugmentedSemanticNodes
 */
public class MethodTASNTracker {
    private HashMap<CXMethod, TypeAugmentedSemanticNode> methodToTreeMap;
    
    private static MethodTASNTracker instance = new MethodTASNTracker();
    
    public static MethodTASNTracker getInstance() {
        return instance;
    }
    
    public void clear() {
        methodToTreeMap.clear();
    }
    
    public MethodTASNTracker() {
        methodToTreeMap = new HashMap<>();
    }
    
    public void add(CXMethod method, TypeAugmentedSemanticNode node) {
        methodToTreeMap.put(method, node);
    }
    
    public TypeAugmentedSemanticNode get(CXMethod method) {
        TypeAugmentedSemanticNode output = methodToTreeMap.getOrDefault(method, null);
        if(output == null) {
            for (CXMethod cxMethod : methodToTreeMap.keySet()) {
                if(cxMethod.getCFunctionName().equals(method.getCFunctionName())) {
                    return methodToTreeMap.get(cxMethod);
                }
            }
        }
        return output;
    }
}
