package radin.core.output.tags;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.methods.CXMethod;

public class ImplementMethodTag extends AbstractCompilationTag {
    private CXMethod method;
    
    public ImplementMethodTag(CXMethod method) {
        super("METHOD IMPLEMENTATION");
        this.method = method;
    }
    
    @Override
    public String toString() {
        return super.toString() + " " + method.getCFunctionName();
    }
    
    public CXMethod getMethod() {
        return method;
    }
}
