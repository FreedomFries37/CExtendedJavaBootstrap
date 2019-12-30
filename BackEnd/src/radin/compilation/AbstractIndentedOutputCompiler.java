package radin.compilation;

import radin.compilation.tags.AbstractCompilationTag;

import java.io.PrintWriter;

public abstract class AbstractIndentedOutputCompiler extends AbstractCompiler {
    
    private int indent;
    
    public AbstractIndentedOutputCompiler(PrintWriter printWriter, int indent) {
        super(printWriter);
        this.indent = indent;
    }
    
    private String getIndent(String s) {
        return getSettings().getIndent().repeat(indent) + s;
    }
    
    private String getIndent() {
        return getSettings().getIndent().repeat(indent);
    }
    
    
    @Override
    public void println() {
        super.println();
        if(indent > 0)
            print(getIndent());
    }
    
    @Override
    public void println(String x) {
        super.println(x);
        if(indent > 0)
            print(getIndent());
    }
    
    @Override
    public void println(Object x) {
        super.println(x);
        if(indent > 0)
            print(getIndent());
    }
}
