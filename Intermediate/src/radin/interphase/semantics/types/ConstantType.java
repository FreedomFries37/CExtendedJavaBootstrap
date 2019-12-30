package radin.interphase.semantics.types;

import radin.interphase.semantics.TypeEnvironment;

public class ConstantType extends CXType {
    
    private CXType subtype;
    
    public ConstantType(CXType subtype) {
        this.subtype = subtype;
    }
    
    @Override
    public String generateCDefinition() {
        return "const " + subtype.generateCDefinition();
    }
    
    @Override
    public String generateCDefinition(String identifier) {
        return "const " + subtype.generateCDefinition(identifier);
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return subtype.isValid(e);
    }
    
    @Override
    public boolean isPrimitive() {
        return subtype.isPrimitive();
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return subtype.getDataSize(e);
    }
    
    public CXType getSubtype() {
        return subtype;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(other instanceof ConstantType) return subtype.is(((ConstantType) other).getSubtype(), e);
        return false;
    }
}
