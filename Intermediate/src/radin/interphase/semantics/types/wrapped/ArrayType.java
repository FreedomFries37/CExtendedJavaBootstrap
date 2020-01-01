package radin.interphase.semantics.types.wrapped;

import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.ICXWrapper;
import radin.interphase.semantics.types.primitives.AbstractCXPrimitiveType;

public class ArrayType extends AbstractCXPrimitiveType implements ICXWrapper {

    private CXType baseType;
    private AbstractSyntaxNode size;
    
    public ArrayType(CXType baseType) {
        this.baseType = baseType;
        this.size = null;
    }
    
    public ArrayType(CXType baseType, AbstractSyntaxNode size) {
        this.baseType = baseType;
        this.size = size;
    }
    
    public CXType getBaseType() {
        return baseType;
    }
    
    
    public AbstractSyntaxNode getSize() {
        return size;
    }
    
    @Override
    public String generateCDefinition() {
        if(size != null) {
            return baseType.generateCDefinition() + "[" + size.getRepresentation() + "]";
        }
        return baseType.generateCDefinition() + "[]";
    }
    
    @Override
    public String generateCDefinition(String identifier) {
        if(size != null) {
            return baseType.generateCDefinition() + " " + identifier + "[" + "$REPLACE ME$" + "]";
        }
        return baseType.generateCDefinition() + " " + identifier + "[]";
    }
    
    @Override
    public boolean isIntegral() {
        return false;
    }
    
    @Override
    public CXType getTypeRedirection(TypeEnvironment e) {
        return new ArrayType(baseType.getTypeRedirection(e), size);
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
    public CXType getWrappedType() {
        return getBaseType();
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(!(other instanceof ArrayType || other instanceof PointerType)) {
            return false;
        }
        CXType baseType;
        if(other instanceof ArrayType) {
            baseType = ((ArrayType) other).baseType;
        }else {
            baseType = ((PointerType) other).getSubType();
        }
        return e.is(this.baseType, baseType);
        //return this.baseType.is(baseType, e);
    }
}
