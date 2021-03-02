package radin.midanalysis.pattern_replacement.singletons;

import radin.core.semantics.TypeEnvironment;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.pattern_replacement.PatternSingleton;

public class VariableSingleton extends PatternSingleton {
    
    private String name;
    private PatternSingleton otherSingleton;
    
    public VariableSingleton(String name, PatternSingleton otherSingleton) {
        this.name = name;
        this.otherSingleton = otherSingleton;
    }
    
    @Override
    public boolean match(TypeAugmentedSemanticNode node, TypeEnvironment environment) {
        return false;
    }
    
    @Override
    public void determineData(TypeAugmentedSemanticNode node, TypeEnvironment environment) {
        otherSingleton.determineData(node, environment);
    }
    
    public String getName() {
        return name;
    }
}
