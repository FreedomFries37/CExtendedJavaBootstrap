package radin.core.semantics.generics;

import radin.core.semantics.types.compound.CXClassType;

import java.util.List;

public abstract class CXGenericDeclaration<T> {
    
    
    private T declaration;
    private List<ParameterType> parameterTypes;
    
    public CXGenericDeclaration(T declaration, List<ParameterType> parameterTypes) {
        this.declaration = declaration;
        this.parameterTypes = parameterTypes;
    }
    
    public abstract T reify(List<CXClassType> types);
    
    public T getDeclaration() {
        return declaration;
    }
}
