package radin.core.input.frontend.v1.parsing;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

import java.util.List;

public class ParsingError extends AbstractCompilationError {
    
    public ParsingError(String error, List<Token> correspondingTokens, String... errorInformation) {
        super(error, correspondingTokens, errorInformation);
    }
    
    @Override
    public String toString() {
        List<Token> correspondingTokens = getCorrespondingTokens();
        correspondingTokens.sort(Token::compareTo);
        if(correspondingTokens.isEmpty()) {
            return super.toString();
        }
        Token token = correspondingTokens.get(0);
        return super.toString() + " -> " + token + " @ ln: " + token.getVirtualLineNumber() + ", column: " + token.getVirtualColumn() +
                " in" +
                " PPO";
    }
}
