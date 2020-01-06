package radin.core.semantics.types.methods;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;

import java.util.Arrays;
import java.util.List;

public class ParameterTypeList {
    
    private int size;
    private CXType[] parameters;
    
    public ParameterTypeList(List<CXType> list) {
        size = list.size();
        parameters = new CXType[size];
        list.toArray(parameters);
    }
    
    public int getSize() {
        return size;
    }
    
    public CXType[] getParameters() {
        return parameters;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ParameterTypeList that = (ParameterTypeList) o;
        
        if (size != that.size) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        
        return Arrays.equals(parameters, that.parameters);
    }
    
    
    public boolean equals(ParameterTypeList o, TypeEnvironment environment) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ParameterTypeList that = (ParameterTypeList) o;
        
        if (size != that.size) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        for (int i = 0; i < size; i++) {
            if(!environment.is(parameters[i], that.parameters[i])) return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "{" + Arrays.toString(parameters) + "}";
    }
    
    @Override
    public int hashCode() {
        return size;
    }
}
