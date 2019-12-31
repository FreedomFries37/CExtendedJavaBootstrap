package radin.interphase;

import radin.interphase.lexical.Token;

public class CompilationError<T extends Throwable> {
    
    private T thrownError;
    private Token closestToken;
    
    public CompilationError(T thrownError, Token closestToken) {
        this.thrownError = thrownError;
        this.closestToken = closestToken;
    }
    
    public T getError() {
        return thrownError;
    }
    
    public Token getClosestToken() {
        return closestToken;
    }
    
    @Override
    public String toString() {
        String error = thrownError.toString();
        if(closestToken != null) error += String.format(" at line %d column %d", closestToken.getLineNumber(),
               closestToken.getColumn());
        return error;
    }
}
