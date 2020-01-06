package radin.v1.parsing;

import radin.core.lexical.Token;

import java.util.Collections;

public class SingleParsingError extends ParsingError {
    
    public SingleParsingError(String error, Token correspondingTokens, String errorInformation) {
        super(error, Collections.singletonList(correspondingTokens), errorInformation);
    }
}
