package radin.midanalysis.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

public class MethodDoesNotExistError extends AbstractCompilationError {
    
    public MethodDoesNotExistError(Token method) {
        super(method, "Method does not exist");
    }
}
