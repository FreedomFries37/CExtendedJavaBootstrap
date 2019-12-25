package radin.interphase.semantics.exceptions;

public class TypeDefinitionAlreadyExistsError extends Error {
    
    public TypeDefinitionAlreadyExistsError(String type) {
        super(String.format("%s already exists as type", type));
    }
}
