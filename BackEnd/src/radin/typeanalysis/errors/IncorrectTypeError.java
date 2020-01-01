package radin.typeanalysis.errors;

import radin.interphase.errorhandling.AbstractCompilationError;
import radin.interphase.lexical.Token;
import radin.interphase.semantics.types.CXType;

import java.util.Arrays;

public class IncorrectTypeError extends AbstractCompilationError {
    public IncorrectTypeError(CXType expected, CXType gotten, Token expectedToken, Token gottenToken) {
        super("expected type: " + expected + " gotten type: " + gotten, Arrays.asList(expectedToken, gottenToken),
                expected.toString(), gotten.toString());
    }
}
