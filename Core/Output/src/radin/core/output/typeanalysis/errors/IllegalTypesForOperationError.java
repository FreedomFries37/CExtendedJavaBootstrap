package radin.core.output.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.CXType;

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
