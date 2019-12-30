package radin.compilation.tags;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.methods.CXConstructor;
import radin.typeanalysis.analyzers.ConstructorTypeAnalyzer;

public class ConstructorCallTag extends AbstractCompilationTag{
    
    private CXConstructor constructor;
    
    public ConstructorCallTag(CXConstructor constructor) {
        super("constructor", ASTNodeType.constructor_call);
        this.constructor = constructor;
    }
    
    public CXConstructor getConstructor() {
        return constructor;
    }
    
    @Override
    public String toString() {
        return constructor.getParent().toString() + " " + super.toString() + " " + constructor.getName();
    }
}
