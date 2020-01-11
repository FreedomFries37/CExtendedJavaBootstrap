package radin.core.output.tags;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.methods.CXMethod;

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
