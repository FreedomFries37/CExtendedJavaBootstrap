package radin.compilation.tags;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.CXType;

import java.util.HashSet;

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
