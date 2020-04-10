package radin.midanalysis.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

public class UndefinedError extends AbstractCompilationError {
    
    public UndefinedError(Token correspondingToken) {
        super(correspondingToken, "Undefined error occured here");
    }
    
    public UndefinedError(Token correspondingToken, String error) {
        super(correspondingToken, error);
    }
}
