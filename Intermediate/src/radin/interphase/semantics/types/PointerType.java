package radin.interphase.semantics.types;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.primitives.AbstractCXPrimitiveType;

public class PointerType extends AbstractCXPrimitiveType {
    
    private CXType subType;
    
    public PointerType(CXType subType) {
        this.subType = subType;
    }
    
    public CXType getSubType() {
        return subType;
    }
    
    @Override
    public String generateCDefinition() {
        return subType.generateCDefinition() + "*";
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return true;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return e.getPointerSize();
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e) {
        if(!(other instanceof PointerType)) return false;
        
        return subType.is(((PointerType) other).subType, e);
    }
    
    
    @Override
    public CXType getCTypeIndirection() {
        return new PointerType(subType.getCTypeIndirection());
    }
}
