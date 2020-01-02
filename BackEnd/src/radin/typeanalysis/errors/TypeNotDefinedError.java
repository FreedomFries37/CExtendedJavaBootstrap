package radin.typeanalysis.errors;

import radin.interphase.errorhandling.AbstractCompilationError;
import radin.interphase.lexical.Token;

public class TypeNotDefinedError extends AbstractCompilationError {
    
    public TypeNotDefinedError(Token correspondingToken) {
        super(correspondingToken, "Not a valid type");
    }
}
