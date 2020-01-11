package radin.core.output.typeanalysis.errors;

import radin.core.output.midanalysis.TypeAugmentedSemanticNode;

public class IllegalLValueError extends Error {
    
    public IllegalLValueError(TypeAugmentedSemanticNode e) {
        super("Illegal L-Value: " + e);
    }
}
