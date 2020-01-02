package radin.lexing;

import radin.interphase.errorhandling.AbstractCompilationError;
import radin.interphase.lexical.Token;

public class TokenizationError extends AbstractCompilationError {
    
    public TokenizationError(String error, Token correspondingToken) {
        super(correspondingToken, error);
    }
}
