package radin.typeanalysis.errors;

import radin.interphase.errorhandling.AbstractCompilationError;
import radin.interphase.lexical.Token;
import radin.interphase.semantics.types.CXType;

import java.util.Collections;

public class IllegalCastError extends AbstractCompilationError {
    
    public IllegalCastError(CXType from, CXType to, Token fromToken) {
        super("Can't cast from " + from + " to " + to,
                Collections.singletonList(fromToken),
                from.toString());
    }
}
