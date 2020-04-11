package radin.core.semantics.generics;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;

import javax.naming.OperationNotSupportedException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class CXGenericType<T extends CXType> extends CXType {

    private List<CXParameterizedTypeInstance<? extends CXType>> parameters;
    private T baseType;
    private TypeEnvironment environment;
    private HashMap<CXParameterizedType, CXParameterizedTypeInstance<? extends CXType>> parameterMap;
    
    public CXGenericType(HashMap<CXParameterizedType, CXParameterizedTypeInstance<? extends CXType>> parameterMap, T baseType,
                         TypeEnvironment environment) {
        this.parameters = new LinkedList<>(parameterMap.values());
        this.parameterMap = parameterMap;
        this.baseType = baseType;
        this.environment = environment;
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        throw new UnsupportedOperationException("Generic Classes are not yet implemented in C");
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
    
    public HashMap<CXParameterizedType, CXParameterizedTypeInstance<? extends CXType>> getParameterMap() {
        return parameterMap;
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
        throw new UnsupportedOperationException("Generic Classes are not yet implemented in C");
    }
    
    public List<CXParameterizedTypeInstance<? extends CXType>> getParameters() {
        return parameters;
    }
    
    public T getBaseType() {
        return baseType;
    }
    
    @Override
    public TypeEnvironment getEnvironment() {
        return environment;
    }
}
