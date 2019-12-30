package radin.typeanalysis.errors;

import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.methods.ParameterTypeList;

public class IllegalAccessError extends Error {
    
    public IllegalAccessError() {
    }
    
    public IllegalAccessError(CXType type, String access) {
        super("Can't access " + access + " for type " + type);
    }
    
    public IllegalAccessError(CXType type, String access, ParameterTypeList list) {
        super("Can't access " + access + " for type " + type + " on " + list);
    }
}
