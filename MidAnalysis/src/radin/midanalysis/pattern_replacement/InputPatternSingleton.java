package radin.midanalysis.pattern_replacement;

import radin.midanalysis.TypeAugmentedSemanticNode;

public abstract class InputPatternSingleton {
    
    public abstract boolean match(TypeAugmentedSemanticNode node);
    
    public abstract void determineData(TypeAugmentedSemanticNode node);
    
    
}
