package radin.compilation.tags;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.methods.CXConstructor;

public class PriorConstructorTag extends AbstractCompilationTag {
    
    private CXConstructor priorConstructor;
    
    public PriorConstructorTag(CXConstructor priorConstructor) {
        super("PRIOR CONSTRUCTOR CALL", ASTNodeType.constructor_definition);
        this.priorConstructor = priorConstructor;
    }
    
    public CXConstructor getPriorConstructor() {
        return priorConstructor;
    }
}
