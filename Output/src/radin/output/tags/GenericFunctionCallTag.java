package radin.output.tags;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.generics.GenericInstance;
import radin.core.semantics.types.compound.CXFunctionPointer;

public class GenericFunctionCallTag extends AbstractCompilationTag{
    
    private GenericInstance<CXFunctionPointer> genericFunction;
    
    public GenericFunctionCallTag(GenericInstance<CXFunctionPointer> genericFunction) {
        super("GENERIC FUNCTION CALL", ASTNodeType.function_call);
        this.genericFunction = genericFunction;
    }
    
    public GenericInstance<CXFunctionPointer> getGenericFunction() {
        return genericFunction;
    }
}
