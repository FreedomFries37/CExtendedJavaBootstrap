package radin.compilation;

import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.PrintWriter;

public abstract class AbstractIndentedOutputSingleOutputCompiler extends AbstractIndentedOutputCompiler {
    
    public AbstractIndentedOutputSingleOutputCompiler(PrintWriter printWriter, int indent) {
        super(printWriter, indent);
    }
    
    abstract public boolean compile();
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        return compile();
    }
}
