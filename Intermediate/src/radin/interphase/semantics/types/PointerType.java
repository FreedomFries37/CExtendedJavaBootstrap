package radin.interphase.semantics.types;

import radin.interphase.semantics.TypeEnvironment;

public class PointerType implements IPrimitiveCXType {
    
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
}
