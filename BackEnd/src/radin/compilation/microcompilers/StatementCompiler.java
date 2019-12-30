package radin.compilation.microcompilers;

import radin.compilation.AbstractIndentedOutputCompiler;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.PrintWriter;

public class StatementCompiler extends AbstractIndentedOutputCompiler {
    
    public StatementCompiler(PrintWriter printWriter, int indent) {
        super(printWriter, indent);
    }
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        return false;
    }
}
