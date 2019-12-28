package radin.typeanalysis.errors;

import radin.interphase.semantics.exceptions.IncorrectParameterTypesError;
import radin.interphase.semantics.types.CXType;

public class IncorrectReturnTypeError extends Error {
    public IncorrectReturnTypeError(CXType lookingFor, CXType found) {
        super("Looking for " + lookingFor + "; found " + found);
    }
}
