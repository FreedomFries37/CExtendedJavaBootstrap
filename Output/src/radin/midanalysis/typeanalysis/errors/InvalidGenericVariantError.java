package radin.midanalysis.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.CXType;

public class InvalidGenericVariantError extends AbstractCompilationError {
    
    public InvalidGenericVariantError(Token correspondingToken, CXType found) {
        super(correspondingToken, String.format("The use of type %s is illegal here", found));
    }
}
