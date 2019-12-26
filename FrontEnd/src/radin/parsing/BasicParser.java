package radin.parsing;

import radin.lexing.Lexer;
import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;

import java.util.Stack;

public abstract class BasicParser {
    
    protected Lexer lexer;
    protected Stack<Integer> states;
    private Stack<Void> suppressErrors;
    
    public BasicParser(Lexer lexer) {
        this.lexer = lexer;
        states = new Stack<>();
        suppressErrors = new Stack<>();
    }
    
    
    
    protected Token getCurrent() {
        return lexer.getCurrent();
    }
    
    protected Token getNext() {
        lexer.getNext();
        return getCurrent();
    }
    
    protected void next() { lexer.getNext(); }
    
    protected void pushState() {
        states.push(lexer.getTokenIndex());
    }
    
    protected boolean popState() {
        if(states.empty()) return false;
        states.pop();
        return true;
    }
    
    protected boolean applyState() {
        if(states.empty()) return false;
        lexer.setTokenIndex(states.pop());
        return true;
    }
    
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
    
    final protected void addAsLeaf(CategoryNode parent, Token t) {
        LeafNode leafNode = new LeafNode(t);
        parent.addChild(leafNode);
    }
    
    final protected boolean consumeAndAddAsLeaf(TokenType t_id, CategoryNode parent) {
        if(!match(t_id)) return false;
        consumeAndAddAsLeaf(parent);
        return true;
    }
    
    protected boolean error(String msg) {
        if(suppressErrors.empty()) {
            System.err.println(String.format("At token %s at line %d column %d: %s",
                    getCurrent(),
                    getCurrent().getLineNumber(),
                    getCurrent().getColumn(),
                    msg));
            new Exception("Parser error stack trace").printStackTrace();
        }
        return false;
    }
    
    
    
    @FunctionalInterface
    protected interface ParseFunction {
        boolean parse(CategoryNode parent);
    }
    
    final protected boolean attemptParse(ParseFunction function, CategoryNode parent) {
        pushState();
        suppressErrors.push(null);
        if(!function.parse(parent)) {
            applyState();
            suppressErrors.pop();
            return false;
        }
        suppressErrors.pop();
        popState();
        return true;
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
