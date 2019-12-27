package radin.interphase.semantics.types;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.primitives.AbstractCXPrimitiveType;

public class ArrayType extends AbstractCXPrimitiveType {

    private CXType baseType;
    private boolean constSize;
    private long size;
    
    public ArrayType(CXType baseType) {
        this.baseType = baseType;
        constSize = false;
    }
    
    public ArrayType(CXType baseType, long size) {
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
    public CXType getTypeRedirection(TypeEnvironment e) {
        return new ArrayType(baseType.getTypeRedirection(e));
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return false;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return 0;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e) {
        if(!(other instanceof ArrayType || other instanceof PointerType)) {
            return false;
        }
        CXType baseType;
        if(other instanceof ArrayType) {
            baseType = ((ArrayType) other).baseType;
        }else {
            baseType = ((PointerType) other).getSubType();
        }
        return this.baseType.is(other, e);
    }
}
