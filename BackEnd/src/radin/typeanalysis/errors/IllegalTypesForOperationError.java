package radin.typeanalysis.errors;

import radin.interphase.lexical.Token;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;

public class IllegalTypesForOperationError extends Error {
    
    public IllegalTypesForOperationError(Token operator, CXType lhs, CXType rhs) {
        super("Illegal types for operator " + operator + " lhs: " + lhs + " rhs:" + rhs);
    }
    public IllegalTypesForOperationError(Token operator, CXType rval) {
        super("Illegal types for operator " + operator + " val: " + rval);
    }
}
