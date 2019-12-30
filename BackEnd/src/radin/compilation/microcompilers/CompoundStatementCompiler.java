package radin.compilation.microcompilers;

import radin.compilation.AbstractIndentedOutputCompiler;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.PrintWriter;

public class CompoundStatementCompiler extends AbstractIndentedOutputCompiler {
    
    public CompoundStatementCompiler(PrintWriter printWriter, int indent) {
        super(printWriter, indent);
    }
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        StatementCompiler statementCompiler = new StatementCompiler(getPrintWriter());
        //print(getIndentString());
        for (TypeAugmentedSemanticNode child : node.getChildren()) {
           
            if(!statementCompiler.compile(child)) return false;
            println();
        }
        return true;
    }
}
