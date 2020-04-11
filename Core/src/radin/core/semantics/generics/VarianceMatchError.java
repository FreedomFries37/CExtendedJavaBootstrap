package radin.core.semantics.generics;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.CXType;

import java.util.Arrays;
import java.util.Collections;

public class VarianceMatchError extends AbstractCompilationError {
    
    public VarianceMatchError(Token on, CXParameterizedTypeInstance<? extends CXType> lookingFor,
                              CXParameterizedTypeInstance<? extends CXType> found) {
        super("Variance doesn't match for types", Collections.singletonList(on),
                String.format("Looking for a type that matches %s, but found %s",lookingFor, found));
    }
}
