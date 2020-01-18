package radin.core.semantics.generics;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.ICXWrapper;

public class CXGenericType extends CXType implements ICXWrapper {

   
    private ParameterType parameterType;
    
    
    public CXGenericType(ParameterType parameterType) {
        this.parameterType = parameterType;
    }
    
    
    @Override
    public String toString() {
        return parameterType.toString();
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        return parameterType.getValue().generateCDeclaration(identifier);
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return false;
    }
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return parameterType.getValue().getDataSize(e);
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        return parameterType.getValue().is(other, e, strictPrimitiveEquality);
    }
    
    @Override
    public String generateCDefinition() {
        return parameterType.getValue().generateCDefinition();
    }
    
    @Override
    public String generateCDeclaration() {
        return parameterType.getValue().generateCDeclaration();
    }
    
    @Override
    public CXType getWrappedType() {
        return parameterType.getValue();
    }
}
