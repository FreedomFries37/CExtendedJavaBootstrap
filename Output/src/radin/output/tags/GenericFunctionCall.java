package radin.output.tags;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.generics.GenericInstance;
import radin.core.semantics.types.compound.CXFunctionPointer;

public class GenericFunctionCall extends AbstractCompilationTag{
    
    private GenericInstance<CXFunctionPointer> genericFunction;
    
    public GenericFunctionCall(GenericInstance<CXFunctionPointer> genericFunction) {
        super("GENERIC FUNCTION CALL", ASTNodeType.generic_init);
        this.genericFunction = genericFunction;
    }
}
