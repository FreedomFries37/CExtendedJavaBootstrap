package radin.core.output.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.CXType;

import java.util.Collections;

public class IllegalCastError extends AbstractCompilationError {
    
    public IllegalCastError(CXType from, CXType to, Token fromToken) {
        super("Can't cast from " + from + " to " + to,
                Collections.singletonList(fromToken),
                from.toString());
    }
}
