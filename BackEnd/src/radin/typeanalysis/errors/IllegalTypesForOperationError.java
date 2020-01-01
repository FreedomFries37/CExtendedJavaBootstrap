package radin.typeanalysis.errors;

import radin.interphase.errorhandling.AbstractCompilationError;
import radin.interphase.lexical.Token;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;

import java.util.Collections;

public class IllegalTypesForOperationError extends AbstractCompilationError {
    
    public IllegalTypesForOperationError(Token operator, CXType lhs, CXType rhs) {
        super("Illegal types for operator " + operator + " lhs: " + lhs + " rhs:" + rhs, operator, "Not an available " +
                "operator for these types");
    }
    public IllegalTypesForOperationError(Token operator, CXType rval) {
        super("Illegal types for operator " + operator + " val: " + rval, operator, "Not an available " +
                "operator for this type");
    }
    
    public IllegalTypesForOperationError(Token operator, CXType lhs, CXType rhs, Token pointTo) {
        super("Illegal types for operator " + operator + " lhs: " + lhs + " rhs:" + rhs, pointTo, "Not an available " +
                "operator for these types");
    }
    public IllegalTypesForOperationError(Token operator, CXType rval, Token pointTo) {
        super("Illegal types for operator " + operator + " val: " + rval, pointTo, "Not an available " +
                "operator for this");
    }
}
