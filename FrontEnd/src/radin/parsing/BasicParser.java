package radin.parsing;

import radin.lexing.Lexer;
import radin.lexing.Token;
import radin.lexing.TokenType;

public class BasicParser {
    
    private Lexer lexer;
    
    public BasicParser(Lexer lexer) {
        this.lexer = lexer;
    }
    
    protected Token getCurrent() {
        return lexer.getCurrent();
    }
    
    protected Token getNext() {
        return lexer.getNext();
    }
    
    protected TokenType getCurrentType() {
        return getCurrent().getType();
    }
    
    protected TokenType getNextType() {
        return getNext().getType();
    }
}
