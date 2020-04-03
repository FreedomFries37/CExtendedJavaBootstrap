package radin.backend.compilation;

import radin.backend.microcompilers.IndentPrintWriter;
import radin.core.utility.UniversalCompilerSettings;

import java.io.PrintWriter;

public abstract class AbstractIndentedOutputCompiler extends AbstractCompiler {
    
    private int indent;
    
    
    public AbstractIndentedOutputCompiler(PrintWriter printWriter, int indent) {
        super(new IndentPrintWriter(printWriter, indent, UniversalCompilerSettings.getInstance().getSettings().getIndent()));
        this.indent = indent;
       
    }
    
    public AbstractIndentedOutputCompiler(IndentPrintWriter printWriter) {
        super(printWriter);
    }
    
    @Override
    public IndentPrintWriter getPrintWriter() {
        return (IndentPrintWriter) super.getPrintWriter();
    }
    
    public int getIndent() {
        return indent;
    }
    
    protected void setIndent(int indent) {
        this.indent = indent;
       
    }
}
