package radin.core.semantics.exceptions;

import radin.core.semantics.types.CXType;

public class InvalidPrimitiveException extends Exception {
    
    public InvalidPrimitiveException() {
        super("Invalid primitive type exception");
    }
    
    public InvalidPrimitiveException(CXType type) {
        super("Invalid primitive type exception " + type);
    }
}
