package radin.v2;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.frontend.ITokenizer;
import radin.core.frontend.Tokenizer;
import radin.core.lexical.Token;

import java.util.LinkedList;
import java.util.List;

public class BasicLexer extends Tokenizer<Token> {
    
    private List<AbstractCompilationError> errors;
    
    public BasicLexer(String inputString, String filename) {
        super(inputString, filename);
        errors = new LinkedList<>();
    }
    
    @Override
    public Token getNext() {
        return null;
    }
    
    
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return errors;
    }
}
