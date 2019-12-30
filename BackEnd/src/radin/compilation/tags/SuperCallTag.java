package radin.compilation.tags;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.methods.CXMethod;

public class SuperCallTag extends AbstractCompilationTag {
    
    private CXMethod method;
    
    public SuperCallTag(CXMethod method) {
        super("SUPER CALL", ASTNodeType.method_call);
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
