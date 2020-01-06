package radin.compilation.tags;

import radin.core.semantics.types.CXType;

public class TypeDefHelperTag extends AbstractCompilationTag {
    
    private CXType originalType;
    
    public TypeDefHelperTag(CXType originalType) {
        super("TYPE DEF HELPER");
        this.originalType = originalType;
    }
    
    public CXType getOriginalType() {
        return originalType;
    }
    
    @Override
    public String toString() {
        return super.toString() + " o=" + getOriginalType();
    }
}
