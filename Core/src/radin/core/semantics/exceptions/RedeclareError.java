package radin.core.semantics.exceptions;

import radin.core.semantics.types.CXIdentifier;

public class RedeclareError extends Error {
    
    public RedeclareError(String name) {
        super(name + " already defined");
    }

    public RedeclareError(CXIdentifier name) {
        super(name + " already defined");
    }
}
