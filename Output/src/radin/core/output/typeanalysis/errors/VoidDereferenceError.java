package radin.core.output.typeanalysis.errors;

public class VoidDereferenceError extends Error {
    
    public VoidDereferenceError() {
        super("Can't dereference a void pointer!");
    }
}
