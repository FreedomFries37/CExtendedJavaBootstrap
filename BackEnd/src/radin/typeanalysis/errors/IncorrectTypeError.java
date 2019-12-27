package radin.typeanalysis.errors;

import radin.interphase.semantics.types.CXType;

public class IncorrectTypeError extends Error {
    public IncorrectTypeError(CXType expected, CXType gotten) {
        super("expected type: " + expected + " gotten type: " + gotten);
    }
}
