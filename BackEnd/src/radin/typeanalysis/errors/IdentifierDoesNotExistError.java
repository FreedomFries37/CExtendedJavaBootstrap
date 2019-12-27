package radin.typeanalysis.errors;

public class IdentifierDoesNotExistError extends Error {
    
    public IdentifierDoesNotExistError(String id) {
        super(id + " does not exist");
    }
}
