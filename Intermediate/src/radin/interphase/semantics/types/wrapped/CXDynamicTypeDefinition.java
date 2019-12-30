package radin.interphase.semantics.types.wrapped;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.ICXWrapper;

public class CXDynamicTypeDefinition extends CXType implements ICXWrapper {
    
    private String typename;
    private CXType original;
    
    public CXDynamicTypeDefinition(String typename, CXType original) {
        this.typename = typename;
        this.original = original;
    }
    
    public CXType getOriginal() {
        return original;
    }
    
    @Override
    public String generateCDefinition(String identifier) {
        return typename + " " + identifier;
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return original.isValid(e);
    }
    
    @Override
    public boolean isPrimitive() {
        return original.isPrimitive();
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return original.getDataSize(e);
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        return e.is(original, other);
    }
    
    @Override
    public CXType getWrappedType() {
        return getOriginal();
    }
    
    @Override
    public String generateCDefinition() {
        return typename;
    }
}
