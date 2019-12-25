package radin.interphase.semantics.exceptions;

public class TypeUsedButNotDefinedError extends Error {
    
    public TypeUsedButNotDefinedError(String type) {
        super(String.format("Attempting to use variable of type %s, but %s is not yet defined", type, type));
    }
}
