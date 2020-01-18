package radin.core.output.backend.microcompilers;

import radin.core.lexical.Token;
import radin.core.output.backend.compilation.AbstractIndentedOutputCompiler;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;

import java.io.PrintWriter;

public class CompoundStatementCompiler extends AbstractIndentedOutputCompiler {
    
    public CompoundStatementCompiler(PrintWriter printWriter, int indent) {
        super(printWriter, indent);
    }
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        StatementCompiler statementCompiler = new StatementCompiler(getPrintWriter());
        //print(getIndentString());
        Integer currentLine = null;
        for (TypeAugmentedSemanticNode child : node.getChildren()) {
            Token firstToken = child.findFirstToken();
            if(firstToken != null && firstToken.getLineNumber() >= 0 && (currentLine == null || firstToken.getLineNumber() >= currentLine)) {
                if (currentLine == null) {
                    currentLine = firstToken.getLineNumber();
                } else {
                    while (++currentLine != firstToken.getLineNumber()) {
                        println();
                    }
                }
            } else {
                currentLine = null;
            }
            if(!statementCompiler.compile(child)) return false;
    
            println();
            
        }
        
        return true;
    }
}
