package radin.core.errorhandling;

import radin.core.lexical.Token;

import java.util.Collections;

public class CompilationError extends AbstractCompilationError{
    
    
    public CompilationError(Throwable thrownError, Token closestToken) {
        super(thrownError, Collections.singletonList(closestToken), thrownError.getMessage());
    }
    
    public CompilationError(String thrownError, Token closestToken) {
        super(thrownError, Collections.singletonList(closestToken));
    }
    
    
}
