package radin.core.semantics.types.compound;

import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.primitives.AbstractCXPrimitiveType;
import radin.core.semantics.types.primitives.CXPrimitiveType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CXFunctionPointer extends AbstractCXPrimitiveType {
    
    private CXType returnType;
    private List<CXType> parameterTypes;
    
    
    public CXFunctionPointer(CXType returnType, List<CXType> parameterTypes) {
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }
    
    public CXFunctionPointer(CXType returnType) {
        this.returnType = returnType;
        parameterTypes = Collections.singletonList(CXPrimitiveType.VOID);
    }
    
    public List<CXType> getParameterTypes() {
        return parameterTypes;
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        if(!returnType.isValid(e)) return false;
        for (CXType parameterType : parameterTypes) {
            if(!parameterType.isValid(e)) return false;
        }
        return true;
    }
    
    @Override
    public boolean isIntegral() {
        return false;
    }
    
    public ParameterTypeList getParameterTypeList() {
        return new ParameterTypeList(parameterTypes);
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return e.getPointerSize();
    }
    
    @Override
    public String generateCDefinition() {
        StringBuilder parameters = new StringBuilder();
        boolean first = true;
        for (CXType parameterType : parameterTypes) {
            if(first) first = false;
            else parameters.append(", ");
            
            parameters.append(parameterType.generateCDefinition());
        }
        return returnType.generateCDefinition() + " (*) (" + parameters.toString() + ")";
    }
    
    public CXType getReturnType() {
        return returnType;
    }
    
    @Override
    public String toString() {
        return returnType.toString() + " (*) (" + parameterTypes.stream().map(CXType::toString).collect(Collectors.joining(
                ", ")) + ")";
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(!(other instanceof CXFunctionPointer)) return false;
        CXFunctionPointer that = (CXFunctionPointer) other;
        
        if(!that.returnType.is(returnType, e)) return false;
        
        return that.getParameterTypeList().equals(this.getParameterTypeList(), e);
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        StringBuilder parameters = new StringBuilder();
        boolean first = true;
        for (CXType parameterType : parameterTypes) {
            if(first) first = false;
            else parameters.append(", ");
        
            parameters.append(parameterType.generateCDefinition());
        }
        return returnType.generateCDefinition() + " (*" + identifier + ") (" + parameters.toString() + ")";
    }
}
