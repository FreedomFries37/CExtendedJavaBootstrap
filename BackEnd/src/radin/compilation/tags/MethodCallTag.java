package radin.compilation.tags;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.methods.CXMethod;

public class MethodCallTag extends AbstractCompilationTag {
    
    private CXMethod method;
    
    public MethodCallTag(CXMethod method) {
        super("METHOD CALL", ASTNodeType.method_call);
        this.method = method;
    }
    
    public CXMethod getMethod() {
        return method;
    }
    
    @Override
    public String toString() {
        return super.toString() + " " + method.getCFunctionName();
    }
}
