package radin.core.input.frontend.v1.parsing;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.input.IParser;
import radin.core.input.ITokenizer;
import radin.core.utility.Pair;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;

import java.util.*;

public abstract class

BasicParser implements IParser<Token, ParseNode> {
    
    protected enum AttemptStatus {
        PARSED,
        ROLLBACK,
        DESYNC
    
    }
    public class IllegalAttemptStatus extends Error {}
    
    protected ITokenizer<? extends Token> lexer;
    protected Stack<Integer> states;
    private Stack<Void> suppressErrors;
    private Stack<Boolean> forceParse;
    private List<AbstractCompilationError> allErrors;
    private List<AbstractCompilationError> tempErrors;
    
    public BasicParser() {
        states = new Stack<>();
        suppressErrors = new Stack<>();
        allErrors = new LinkedList<>();
        tempErrors = new LinkedList<>();
        forceParse = new Stack<>();
    }
    
    @Override
    public void reset() {
        lexer.reset();
        states.clear();
        suppressErrors.clear();
        allErrors.clear();
        tempErrors.clear();
        forceParse.clear();
    }
    
    
    @Override
    public void setTokenizer(ITokenizer<? extends Token> t) {
        lexer = t;
        
    }
    
    @Override
    public ITokenizer<? extends Token> getTokenizer() {
        return lexer;
    }
    
    
    
    @Override
    public Token getCurrent() {
        return lexer.getCurrent();
    }
    
    @Override
    public Token getNext() {
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
        for (int i = 0; i < forceParse.size(); i++) {
            forceParse.set(i, true);
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
        return error(msg, release, getCurrent());
    }
    
    protected boolean error(String msg, Token previous) {
        return error(msg, false, previous);
    }
    
    protected boolean missingError(String msg) {
        return error(msg, lexer.getPrevious());
    }
    
    /**
     * Recoverable version of missingError;
     * @param msg
     * @param find
     * @param stopAt
     * @return
     */
    protected boolean recoverableMissingError(String msg, TokenType find, TokenType... stopAt) {
        missingError(msg);
        Set<TokenType> stopAtSet = new HashSet<>();
        stopAtSet.add(TokenType.t_eof);
        stopAtSet.addAll(Arrays.asList(stopAt));
        while (!stopAtSet.contains(getCurrentType()) && !consume(find)) {
            getNext();
        }
       
        return !stopAtSet.contains(getCurrentType());
    }
    
    protected boolean error(String msg, boolean release, Token correspondingToken) {
        ParsingError error = new SingleParsingError(msg, correspondingToken, "here");
    
        if(suppressErrors.empty() || forceParse.peek()) {
             allErrors.add(error);
        } else {
            tempErrors.add(error);
        }
        if(release) {
            allErrors.addAll(tempErrors);
            tempErrors.clear();
        }
        return false;
    }
    
    protected boolean absorbErrors(String newError) {
        return absorbErrors(newError, false, getCurrent());
    }
    
    protected boolean absorbErrors(String newError, boolean includeTemps) {
        return absorbErrors(newError, includeTemps, getCurrent());
    }
    
    protected boolean absorbErrors(String newError, Token corresponding) {
        return absorbErrors(newError, false, getCurrent());
    }
    
    protected boolean absorbErrors(String newError, boolean includeTemps, Token correspondingToken) {
        List<AbstractCompilationError> errors = new LinkedList<>();
        if(includeTemps) {
            errors.addAll(tempErrors);
        }
        errors.addAll(allErrors);
        errors.removeIf(t -> !(t instanceof SingleParsingError));
        tempErrors.removeAll(errors);
        allErrors.removeAll(errors);
        
        List<Pair<Token, String>> pairs = new LinkedList<>();
        for (AbstractCompilationError error : errors) {
            assert error instanceof SingleParsingError;
            SingleParsingError singleParsingError = (SingleParsingError) error;
            AbstractCompilationError.ErrorInformation info = singleParsingError.getInfo(0);
            pairs.add(new Pair<>(info.getToken(), singleParsingError.getMessage()));
        }
        pairs.sort(Comparator.comparing(Pair::getVal1));
        List<Token> tokens = new LinkedList<>();
        tokens.add(correspondingToken);
        String[] infos = new String[pairs.size() + 1];
        infos[0] = null;
        for (int i = 0; i < pairs.size(); i++) {
            tokens.add(pairs.get(i).getVal1());
            infos[i + 1] = pairs.get(i).getVal2();
        }
        ParsingError error = new ParsingError(newError, tokens, infos);
        if(suppressErrors.empty() || forceParse.peek()) {
            allErrors.add(error);
        } else {
            tempErrors.add(error);
        }
        return false;
    }
    
    public void clearTempErrors() {
        tempErrors.clear();
    }
    
    @Override
    public void clearErrors() {
        tempErrors.clear();
        allErrors.clear();
    }
    
    @Override
    public final List<AbstractCompilationError> getErrors() {
        return allErrors;
    }
    
   
    
    @Override
    public ParseNode invoke() {
        return parse();
    }
    
    @FunctionalInterface
    protected interface ParseFunction {
        boolean parse(CategoryNode parent);
    }
    
    final protected AttemptStatus attemptParse(ParseFunction function, CategoryNode parent) {
        pushState();
        suppressErrors.push(null);
        forceParse.add(false);
        if(!function.parse(parent)) {
            if(!forceParse.peek()) {
                applyState();
                suppressErrors.pop();
                forceParse.pop();
                clearTempErrors();
                return AttemptStatus.ROLLBACK;
            } else {
                allErrors.addAll(tempErrors);
                tempErrors.clear();
                popState();
                suppressErrors.pop();
                forceParse.pop();
                
                
                return AttemptStatus.DESYNC;
            }
        }
        
        
        popState();
        suppressErrors.pop();
        forceParse.pop();
        clearTempErrors();
        return AttemptStatus.PARSED;
    }
    
    final protected boolean oneMustParse(CategoryNode parent, ParseFunction parseFunction, ParseFunction... functions) {
        if(functions.length == 0) return parseFunction.parse(parent);
        switch (attemptParse(parseFunction, parent)) {
            case PARSED:
                return true;
            case ROLLBACK:
                break;
            case DESYNC:
                return false;
        }
        int count = 0;
        for (ParseFunction function : functions) {
            if(count == functions.length - 1) {
                return function.parse(parent);
            }
            switch (attemptParse(function, parent)) {
                case PARSED:
                    return true;
                case ROLLBACK:
                    break;
                case DESYNC:
                    return false;
            }
            count++;
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
