package radin.interphase.errorhandling;

import radin.interphase.lexical.Token;

import java.util.Collections;

public class CompilationError extends AbstractCompilationError{
    
    
    public CompilationError(Throwable thrownError, Token closestToken) {
        super(thrownError, Collections.singletonList(closestToken), thrownError.getMessage());
    }
    
    
    
    
}
