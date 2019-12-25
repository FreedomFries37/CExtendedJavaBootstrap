package radin.interphase.semantics.exceptions;

public class VoidTypeError extends Error {
    
    public VoidTypeError() {
        super("Can't create a reference a void type");
    }
}
