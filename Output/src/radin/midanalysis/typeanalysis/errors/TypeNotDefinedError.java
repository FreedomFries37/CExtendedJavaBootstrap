package radin.midanalysis.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

public class TypeNotDefinedError extends AbstractCompilationError {
    
    public TypeNotDefinedError(Token correspondingToken) {
        super(correspondingToken, "Not a valid type");
    }
}
