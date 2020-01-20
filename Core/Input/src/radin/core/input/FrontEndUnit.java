package radin.core.input;

import radin.core.IFrontEndUnit;
import radin.core.chaining.ICompilerFunction;
import radin.core.chaining.ICompilerProducer;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.AbstractTree;
import radin.core.semantics.TypeEnvironment;
import radin.core.utility.ICompilationSettings;

import java.util.LinkedList;
import java.util.List;

public class FrontEndUnit<T, P extends AbstractTree<? extends P>, S> implements IFrontEndUnit<S> {
    ITokenizer<? extends T> lexer;
    IParser<? super T, ? extends P> parser;
    ISemanticAnalyzer<? super P, ? extends S> builder;
    
    
    
    public FrontEndUnit(ITokenizer<? extends T> lexer, IParser<? super T, ? extends P> parser, ISemanticAnalyzer<? super P, ? extends S> builder) {
        this.lexer = lexer;
        this.parser = parser;
        this.parser.setTokenizer(lexer);
        this.builder = builder;
    }
    
    public S build() {
        ICompilationSettings.debugLog.info("Running Lexer");
        lexer.run();
        if(lexer.hasErrors()) {
            ICompilationSettings.debugLog.warning("Lexing resulted in errors");
            return null;
        }
        ICompilationSettings.debugLog.info("Running Parser");
        P parse = parser.parse();
        if(parse == null || parser.hasErrors()) {
            ICompilationSettings.debugLog.warning("Parsing resulted in errors");
            return null;
        }
        ICompilationSettings.debugLog.info("Created Parse Tree of depth " + parse.getDepth() + " with a total of " + parse.getTotalNodes() + " nodes");
        // parse.printTreeForm();
        ICompilationSettings.debugLog.info("Running Builder");
        return builder.analyze(parse);
    }
    
    @Override
    public S invoke() {
        return build();
    }
    
    @Override
    public TypeEnvironment getEnvironment() {
        return builder.getEnvironment();
    }
    
    @Override
    public String getUsedString() {
        return lexer.getInputString();
    }
    
    @Override
    public void reset() {
        lexer.reset();
        parser.reset();
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        List<AbstractCompilationError> output = new LinkedList<>();
        if(lexer.hasErrors()) {
            output.addAll(lexer.getErrors());
        }
        if(parser.hasErrors()) {
            output.addAll(parser.getErrors());
        }
        if(builder.hasErrors()) {
            output.addAll(builder.getErrors());
        }
        
        return output;
    }
}
