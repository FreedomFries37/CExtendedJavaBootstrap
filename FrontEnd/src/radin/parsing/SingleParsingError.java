package radin.parsing;

import radin.interphase.lexical.Token;

import java.util.Collections;
import java.util.List;

public class SingleParsingError extends ParsingError {
    
    public SingleParsingError(String error, Token correspondingTokens, String errorInformation) {
        super(error, Collections.singletonList(correspondingTokens), errorInformation);
    }
}
