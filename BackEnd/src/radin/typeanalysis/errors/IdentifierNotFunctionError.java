package radin.typeanalysis.errors;

public class IdentifierNotFunctionError extends Error {
    
    public IdentifierNotFunctionError(String id) {
        super(id + " is not a function");
    }
}
