package radin.midanalysis.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.CXType;

import java.util.List;

public class IncorrectGenericVariantError extends AbstractCompilationError {
    
    public IncorrectGenericVariantError(Token correspondingToken, CXType found, CXType lookingFor) {
        super(correspondingToken, String.format("Looking for a generic of atleast type %s, found %s", found, lookingFor));
    }
}
