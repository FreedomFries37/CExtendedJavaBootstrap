package radin.core.input.frontend.v3.pplexer;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.input.Tokenizer;

import java.util.List;

public class PreProcessor2 extends Tokenizer<PreProcessingToken> {
    
    public PreProcessor2(String inputString, String filename) {
        super(inputString, filename);
    }
    
    @Override
    public PreProcessingToken invoke(Void input) {
        return null;
    }
    
    @Override
    protected PreProcessingToken singleLex() {
        return null;
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return null;
    }
}
