package radin.parsing;

import radin.lexing.Lexer;
import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public abstract class BasicParser {
    
    protected enum AttemptStatus {
        PARSED,
        ROLLBACK,
        DESYNC;
        
        
    }
    public class IllegalAttemptStatus extends Error {}
    
    protected Lexer lexer;
    protected Stack<Integer> states;
    private Stack<Void> suppressErrors;
    private Stack<Boolean> forceParse;
    private List<String> allErrors;
    
    
    public BasicParser(Lexer lexer) {
        this.lexer = lexer;
        states = new Stack<>();
        suppressErrors = new Stack<>();
        allErrors = new LinkedList<>();
        forceParse = new Stack<>();
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
    
    public void forceParse() {
        if(!forceParse.empty()) {
            forceParse.pop();
            forceParse.push(true);
        }
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
       return error(msg, false);
    }
    
    protected boolean error(String msg, boolean release) {
        String errorMsg = String.format("At token %s at line %d column %d: %s",
                getCurrent(),
                getCurrent().getLineNumber(),
                getCurrent().getColumn(),
                msg);
        if(suppressErrors.empty()) {
            System.err.println(errorMsg);
            new Exception("Parser error stack trace").printStackTrace();
        } else {
            StringWriter writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            new Exception("Parser error stack trace").printStackTrace(pw);
            allErrors.add(errorMsg + "\n" + writer.toString());
        }
        if(release) {
            for (String s : allErrors) {
                System.err.println(s);
            }
            allErrors.clear();
        }
        return false;
    }
    
    protected void clearErrors() {
        allErrors.clear();
    }
    
    
    @FunctionalInterface
    protected interface ParseFunction {
        boolean parse(CategoryNode parent);
    }
    
    final protected AttemptStatus attemptParse(ParseFunction function, CategoryNode parent) {
        pushState();
        suppressErrors.push(null);
        forceParse.push(false);
        if(!function.parse(parent)) {
            if(!forceParse.peek()) {
                applyState();
                suppressErrors.pop();
                forceParse.pop();
                return AttemptStatus.ROLLBACK;
            } else {
                popState();
                suppressErrors.pop();
                forceParse.pop();
                
                return AttemptStatus.DESYNC;
            }
        }
        
        
        popState();
        suppressErrors.pop();
        forceParse.pop();
        return AttemptStatus.PARSED;
    }
    
    final protected boolean oneMustParse(CategoryNode parent, ParseFunction parseFunction, ParseFunction... functions) {
        switch (attemptParse(parseFunction, parent)) {
            case PARSED:
                return true;
            case ROLLBACK:
                break;
            case DESYNC:
                return false;
        }
        for (ParseFunction function : functions) {
            switch (attemptParse(function, parent)) {
                case PARSED:
                    return true;
                case ROLLBACK:
                    break;
                case DESYNC:
                    return false;
            }
        }
        return false;
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
