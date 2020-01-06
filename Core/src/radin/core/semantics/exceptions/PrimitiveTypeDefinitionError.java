package radin.core.semantics.exceptions;

public class PrimitiveTypeDefinitionError extends Error {
    
    public PrimitiveTypeDefinitionError(String type) {
        super(String.format("Can't typedef over primitive %s", type));
    }
}
