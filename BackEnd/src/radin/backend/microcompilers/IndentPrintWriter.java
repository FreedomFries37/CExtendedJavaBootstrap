package radin.backend.microcompilers;

import java.io.PrintWriter;

public class IndentPrintWriter extends PrintWriter {
    
    private int indent;
    private String buffer;
    private String indentString;
    
    public IndentPrintWriter(PrintWriter parent, int indent, String indentString) {
        super(parent);
        this.indent = indent;
        this.indentString = indentString;
        this.buffer = getIndentString();
    }
    
    public int getIndent() {
        return indent;
    }
    
    public static IndentPrintWriter create(PrintWriter parent, int indent, String indentString) {
        return new IndentPrintWriter(parent, indent, indentString);
    }
    
    public static IndentPrintWriter create(IndentPrintWriter parent, int indent, String indentString) {
        return new IndentPrintWriter(parent, indent, indentString);
    }
    
    protected String getIndentString(String s) {
        return indentString.repeat(indent) + s;
    }
    
    protected String getIndentString() {
        return indentString.repeat(indent);
    }
    
    @Override
    public void print(String s) {
        if(buffer != null) {
            super.print(buffer);
            buffer = null;
        }
        String[] split = s.split("\n");
        if(split.length > 1) {
            for (int i = 0; i < split.length - 1; i++) {
                println(split[i]);
            }
            print(split[split.length - 1]);
        }
        else super.print(s);
    }
    
    @Override
    public void print(Object obj) {
        if(buffer != null) {
            super.print(buffer);
            buffer = null;
        }
        print(obj.toString());
    }
    
    @Override
    public void println() {
        super.println();
        if(indent > 0)
            buffer = getIndentString();
    }
    
    @Override
    public void println(String x) {
        super.println(x);
        if(indent > 0)
            buffer = getIndentString();
    }
    
    @Override
    public void println(Object x) {
        super.println(x);
        if(indent > 0)
            buffer = getIndentString();
    }
}
