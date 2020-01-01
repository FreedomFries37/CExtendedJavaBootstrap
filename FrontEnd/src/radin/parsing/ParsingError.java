package radin.parsing;

import radin.interphase.errorhandling.AbstractCompilationError;
import radin.interphase.lexical.Token;

import java.util.List;

public class ParsingError extends AbstractCompilationError {
    
    public ParsingError(String error, List<Token> correspondingTokens, String... errorInformation) {
        super(error, correspondingTokens, errorInformation);
    }
}
