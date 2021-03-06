package radin.output.typeanalysis.errors;

import radin.core.semantics.types.CXIdentifier;

public class IdentifierDoesNotExistError extends Error {
    
    public IdentifierDoesNotExistError(String id) {
        super(id + " does not exist");
    }

    public IdentifierDoesNotExistError(CXIdentifier id) {
        super(id + " does not exist");
    }
}
