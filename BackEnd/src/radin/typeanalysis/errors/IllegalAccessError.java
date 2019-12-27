package radin.typeanalysis.errors;

import radin.interphase.semantics.types.CXType;

public class IllegalAccessError extends Error {
    
    public IllegalAccessError() {
    }
    
    public IllegalAccessError(CXType type, String access) {
        super("Can't access " + access + " for type " + type);
    }
}
