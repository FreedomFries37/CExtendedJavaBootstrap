package radin.core.semantics.generics;

import radin.core.semantics.types.compound.CXClassType;

public class ParameterType {
    public enum Variance {
        invariance(""),
        covariance("out "),
        contravariance("in ");
        
        String s;
        
        Variance(String s) {
            this.s = s;
        }
        
        @Override
        public String toString() {
            return s;
        }
    }
    
    private CXClassType bound;
    private Variance variance;
    private CXClassType value;
    
    public ParameterType(CXClassType bound) {
        this(bound, Variance.invariance);
    }
    
    public ParameterType(CXClassType bound, Variance variance) {
        this.bound = bound;
        this.variance = variance;
    }
    
    public CXClassType getValue() {
        return value;
    }
    public class InvalidGenericType extends Error {
        
        public InvalidGenericType(CXClassType type) {
            super("Invalid generic type " + type + " for bound " + ParameterType.this.toString());
        }
    }
    
    
    public void setValue(CXClassType value) {
        switch (variance) {
            case invariance:
                if(value != bound) throw new InvalidGenericType(value);
                break;
            case covariance:
                if(!bound.getEnvironment().is(value, bound)) throw new InvalidGenericType(value);
                break;
            case contravariance:
                if(!bound.getEnvironment().is(bound, value)) throw new InvalidGenericType(value);
                break;
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "`" + bound;
    }
}
