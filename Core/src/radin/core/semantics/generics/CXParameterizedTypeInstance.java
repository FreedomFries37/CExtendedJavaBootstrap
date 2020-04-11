package radin.core.semantics.generics;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;

public class CXParameterizedTypeInstance<T extends CXType> {
    
    public enum Variance {
        INVARIANCE("%s"),
        COVARIANCE("? : %s"),
        CONTRAVARIANCE("%s : ?")
        ;
        
        private final String formatString;
    
        Variance(String formatString) {
            this.formatString = formatString;
        }
    
        public String getFormatString() {
            return formatString;
        }
    }
    
    private final Variance variance;
    private final T baseType;
    private final TypeEnvironment environment;
    
    public CXParameterizedTypeInstance(Variance variance, T baseType, TypeEnvironment environment) {
        this.variance = variance;
        this.baseType = baseType;
        this.environment = environment;
    }
    
    public Variance getVariance() {
        return variance;
    }
    
    /**
     * Checks whether the incoming parameterized type fits the proper inheritence type
     *
     *  This is determined by viewing the different variants as ranges, and whether the ranges overlap
     * @param other The other parameter type
     * @param <R> the type the other parameter type extends
     * @return whether the parameter type is a valid parameter
     */
    public <R extends CXType> boolean checkIncomingType(CXParameterizedTypeInstance<R> other) {
        switch (this.variance) {
            case INVARIANCE: {
                if (this.variance != other.variance) return false;
                return baseType.isExact(other.baseType, environment);
            }
            case COVARIANCE:
                switch (other.variance) {
                    case INVARIANCE:
                    case COVARIANCE:
                        return environment.is(other.baseType, this.baseType);
                    case CONTRAVARIANCE:
                        return false;
                }
            case CONTRAVARIANCE:
                switch (other.variance) {
                    case INVARIANCE:
                    case CONTRAVARIANCE:
                        return environment.is(this.baseType, other.baseType);
                    case COVARIANCE:
                        return false;
                }
        }
        return false;
    }
    
    
}
