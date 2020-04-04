package radin.midanalysis.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.CXType;

import java.util.List;

public class InstantiationError extends AbstractCompilationError {
    

    public InstantiationError(Token attempt, CXType type) {
        super("Type can't be instantiated", attempt, "The type " + type + " can't be instantiated using the new operator");
    }
}
