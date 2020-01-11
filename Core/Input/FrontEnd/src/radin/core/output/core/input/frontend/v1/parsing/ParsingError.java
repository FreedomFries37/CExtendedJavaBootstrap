package radin.core.output.core.input.frontend.v1.parsing;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

import java.util.List;

public class ParsingError extends AbstractCompilationError {
    
    public ParsingError(String error, List<Token> correspondingTokens, String... errorInformation) {
        super(error, correspondingTokens, errorInformation);
    }
}
