package radin.typeanalysis.errors;

import radin.interphase.semantics.types.CXType;

public class IllegalCastError extends Error {
    
    public IllegalCastError(CXType from, CXType to) {
        super("Can't cast from " + from + " to " + to);
    }
}
