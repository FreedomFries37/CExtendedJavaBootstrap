package radin.output.typeanalysis.errors;

import radin.core.semantics.types.CXType;

public class ConstModificationError extends Error {
    
    public ConstModificationError(String id, CXType type) {
        super("Can't modify " + type + " " + id);
    }
    
    public ConstModificationError(CXType type) {
        super("Can't modify " + type);
    }
}
