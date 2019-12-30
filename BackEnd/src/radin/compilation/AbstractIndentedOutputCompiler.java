package radin.compilation;

import java.io.PrintWriter;

public abstract class AbstractIndentedOutputCompiler extends AbstractCompiler {
    
    private int indent;
    
    public AbstractIndentedOutputCompiler(PrintWriter printWriter, int indent) {
        super(printWriter);
        this.indent = indent;
    }
    
    private String getIndentString(String s) {
        return getSettings().getIndent().repeat(indent) + s;
    }
    
    private String getIndentString() {
        return getSettings().getIndent().repeat(indent);
    }
    
    public int getIndent() {
        return indent;
    }
    
    @Override
    public void println() {
        super.println();
        if(indent > 0)
            print(getIndentString());
    }
    
    @Override
    public void println(String x) {
        super.println(x);
        if(indent > 0)
            print(getIndentString());
    }
    
    @Override
    public void println(Object x) {
        super.println(x);
        if(indent > 0)
            print(getIndentString());
    }
}
