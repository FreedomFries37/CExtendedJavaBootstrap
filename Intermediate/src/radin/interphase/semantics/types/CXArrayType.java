package radin.interphase.semantics.types;

import radin.interphase.semantics.TypeEnvironment;

public class CXArrayType implements IPrimitiveCXType {

    private CXType baseType;
    private boolean constSize;
    private long size;
    
    public CXArrayType(CXType baseType) {
        this.baseType = baseType;
        constSize = false;
    }
    
    public CXArrayType(CXType baseType, long size) {
        this.baseType = baseType;
        this.size = size;
        constSize = true;
    }
    
    public CXType getBaseType() {
        return baseType;
    }
    
    public boolean isConstSize() {
        return constSize;
    }
    
    public long getSize() {
        return size;
    }
    
    @Override
    public String generateCDefinition() {
        if(isConstSize()) {
            return baseType.generateCDefinition() + "[" + constSize + "]";
        }
        return baseType.generateCDefinition() + "[]";
    }
    
    @Override
    public String generateCDefinition(String identifier) {
        if(isConstSize()) {
            return baseType.generateCDefinition() + " " + identifier + "[" + constSize + "]";
        }
        return baseType.generateCDefinition() + " " + identifier + "[]";
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return false;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return 0;
    }
}
