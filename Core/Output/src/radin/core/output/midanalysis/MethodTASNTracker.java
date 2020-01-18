package radin.core.output.midanalysis;

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
        return methodToTreeMap.getOrDefault(method, null);
    }
}
