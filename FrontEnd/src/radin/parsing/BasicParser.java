package radin.parsing;

import radin.lexing.Lexer;
import radin.lexing.Token;
import radin.lexing.TokenType;

public abstract class BasicParser {
    
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
    
    protected void next() { lexer.getNext(); }
    
    final protected boolean consume(TokenType type) {
        if(getCurrentType().equals(type)) {
            getNext();
            return true;
        }
        return false;
    }
    
    final protected void consumeAndAddAsLeaf(CategoryNode parent) {
        LeafNode leafNode = new LeafNode(getCurrent());
        next();
        parent.addChild(leafNode);
    }
    
    
    final protected boolean match(TokenType type) {
        return getCurrentType().equals(type);
    }
    
    protected TokenType getCurrentType() {
        return getCurrent().getType();
    }
    
    protected TokenType getNextType() {
        return getNext().getType();
    }
}
