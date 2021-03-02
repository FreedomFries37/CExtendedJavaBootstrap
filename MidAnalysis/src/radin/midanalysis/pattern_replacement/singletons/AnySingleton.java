package radin.midanalysis.pattern_replacement.singletons;

import radin.core.semantics.TypeEnvironment;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.pattern_replacement.PatternSingleton;

public class AnySingleton extends PatternSingleton {
    
    
    @Override
    public boolean match(TypeAugmentedSemanticNode node, TypeEnvironment environment) {
        return true;
    }
}
