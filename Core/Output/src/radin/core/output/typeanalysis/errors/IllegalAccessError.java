package radin.core.output.typeanalysis.errors;

import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.semantics.types.CXType;

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
