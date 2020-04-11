package radin.core.semantics.generics;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;

import java.util.Iterator;
import java.util.List;

public abstract class CXGenericType<T extends CXType> extends CXType {

    private List<CXParameterizedTypeInstance<? extends CXType>> parameters;
    private T baseType;
    private TypeEnvironment environment;
    
    public CXGenericType(List<CXParameterizedTypeInstance<? extends CXType>> parameters, T baseType, TypeEnvironment environment) {
        this.parameters = parameters;
        this.baseType = baseType;
        this.environment = environment;
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        return null;
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return true;
    }
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return 0;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if (!(other instanceof CXGenericType<?>)) return false;
        return checkGenericAcceptableIs(((CXGenericType<?>) other));
    }
    
    protected <R extends CXType> boolean checkGenericAcceptableIs(CXGenericType<R> other) {
        if(!environment.is(other.baseType, this.baseType)) return false;
        Iterator<CXParameterizedTypeInstance<? extends CXType>> typeInstanceIterator = other.parameters.iterator();
        for (CXParameterizedTypeInstance<? extends CXType> parameter : this.parameters) {
            if(!parameter.checkIncomingType(typeInstanceIterator.next())) return false;
        }
        
        return true;
    }
    
    @Override
    public String generateCDefinition() {
        return null;
    }
}
