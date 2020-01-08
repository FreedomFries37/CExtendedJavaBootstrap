package radin.core.semantics.types.compound;

import radin.core.semantics.types.CXType;
import radin.core.semantics.types.methods.CXMethod;

public class IncompatibleReturnTypeError extends Error {
    
    public IncompatibleReturnTypeError(String name, CXType old, CXType newT) {
        super(name + " has incompatible type. Expected: " + old + "  Found: " + newT);
    }
}
