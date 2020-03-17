package radin.core.semantics.generics;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

public class NoDefinitionForGenericDeclarationError extends AbstractCompilationError {
    
    public NoDefinitionForGenericDeclarationError(Token originalPtr) {
        super(originalPtr, "No definition exists for this generic declaration, but it was instantiated");
    }
}
