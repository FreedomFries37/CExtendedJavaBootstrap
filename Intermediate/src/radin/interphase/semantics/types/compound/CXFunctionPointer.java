package radin.interphase.semantics.types.compound;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.IPrimitiveCXType;
import radin.interphase.semantics.types.PointerType;
import radin.interphase.semantics.types.PrimitiveCXType;

import java.util.Arrays;
import java.util.List;

public class CXFunctionPointer implements IPrimitiveCXType {
    
    private CXType returnType;
    private List<CXType> parameterTypes;
    
    
    public CXFunctionPointer(CXType returnType, List<CXType> parameterTypes) {
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }
    
    public CXFunctionPointer(CXType returnType) {
        this.returnType = returnType;
        parameterTypes = Arrays.asList(PrimitiveCXType.VOID);
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
    public long getDataSize(TypeEnvironment e) {
        return e.getPointerSize();
    }
    
    @Override
    public String generateCDefinition() {
        StringBuilder parameters = new StringBuilder();
        boolean first = true;
        for (CXType parameterType : parameterTypes) {
            if(first) first = false;
            parameters.append(", ");
            
            parameters.append(parameterType.generateCDefinition());
        }
        return returnType.generateCDefinition() + " (*) (" + parameters.toString() + ")";
    }
    
    @Override
    public String generateCDefinition(String identifier) {
        StringBuilder parameters = new StringBuilder();
        boolean first = true;
        for (CXType parameterType : parameterTypes) {
            if(first) first = false;
            parameters.append(", ");
        
            parameters.append(parameterType.generateCDefinition());
        }
        return returnType.generateCDefinition() + " (*" + identifier + ") (" + parameters.toString() + ")";
    }
}
