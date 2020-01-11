package radin.core.output.backend.compilation;



import radin.core.output.midanalysis.TypeAugmentedSemanticNode;

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
