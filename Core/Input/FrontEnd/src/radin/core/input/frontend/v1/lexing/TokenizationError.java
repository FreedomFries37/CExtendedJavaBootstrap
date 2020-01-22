package radin.core.input.frontend.v1.lexing;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

public class TokenizationError extends AbstractCompilationError {
    
    public TokenizationError(String error, Token correspondingToken) {
        super(correspondingToken, error);
    }
}
