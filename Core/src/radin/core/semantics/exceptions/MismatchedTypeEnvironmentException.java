package radin.core.semantics.exceptions;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;

public class MismatchedTypeEnvironmentException extends Error {
    
    public MismatchedTypeEnvironmentException(CXType a, TypeEnvironment aE, CXType b, TypeEnvironment bE) {
        super(a.toString() + " is in " + aE + ", but " + b + " is in " + bE);
    }
}
