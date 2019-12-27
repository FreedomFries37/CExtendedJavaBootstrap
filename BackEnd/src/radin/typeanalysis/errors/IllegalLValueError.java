package radin.typeanalysis.errors;

import radin.typeanalysis.TypeAugmentedSemanticNode;

public class IllegalLValueError extends Error {
    
    public IllegalLValueError(TypeAugmentedSemanticNode e) {
        super("Illegal L-Value: " + e);
    }
}
