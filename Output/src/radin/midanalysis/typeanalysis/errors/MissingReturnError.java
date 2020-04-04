package radin.midanalysis.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

public class MissingReturnError extends AbstractCompilationError {
    
    public MissingReturnError(Token returnToken, String returnType) {
        super("function does not return in every branch", returnToken, "Specifies that " + returnType + " must be " +
                "returned");
    }
}
