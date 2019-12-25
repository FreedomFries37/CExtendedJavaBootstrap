package radin.interphase.semantics.exceptions;

public class RedeclareError extends Error {
    
    public RedeclareError(String name) {
        super(name + " already defined");
    }
}
