package radin.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.CXIdentifier;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AmbiguousIdentifierError extends AbstractCompilationError {
    
    public AmbiguousIdentifierError(Token pointer, List<CXIdentifier> alternatives) {
        super("Ambiguous Identifier", pointer, "Could be " +
                alternatives.stream().map(CXIdentifier::toString).collect(Collectors.joining(" or ")));
    }
}
