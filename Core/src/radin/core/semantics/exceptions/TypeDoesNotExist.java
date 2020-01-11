package radin.core.semantics.exceptions;

public class TypeDoesNotExist extends Error {
    
    public TypeDoesNotExist(String type) {
        super(type + " does not exist");
    }
}
