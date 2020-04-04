package radin.midanalysis.typeanalysis.errors;

import radin.core.semantics.types.CXType;

public class IncorrectReturnTypeError extends Error {
    public IncorrectReturnTypeError(CXType lookingFor, CXType found) {
        super("Looking for " + lookingFor + "; found " + found);
    }
}
