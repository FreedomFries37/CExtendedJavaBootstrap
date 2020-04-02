package radin.core.input.frontend.v1.parsing;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class IncorrectTokenTypesError extends AbstractCompilationError {
    
    
    
    public IncorrectTokenTypesError(List<Token> found, List<TokenType> lookingFor) {
        super(found, lookingFor.stream()
                .map((t) -> "Looking for " + t.toString()).toArray(String[]::new));
        
    }
}
