package radin.core.semantics.types;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

import java.util.List;
import java.util.stream.Collectors;

public class AmbiguousIdentifierError extends AbstractCompilationError {
    
    public AmbiguousIdentifierError(Token pointer, List<CXType> alternatives) {
        super("Ambiguous Identifier", pointer, "Could be " +
                alternatives.stream().map(CXType::toString).collect(Collectors.joining(" or ")));
    }
}
