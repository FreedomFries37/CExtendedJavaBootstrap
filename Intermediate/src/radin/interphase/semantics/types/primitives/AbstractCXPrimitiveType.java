package radin.interphase.semantics.types.primitives;

import radin.interphase.semantics.types.CXType;

public abstract class AbstractCXPrimitiveType extends CXType {
    
    
    public boolean isPrimitive() {
        return true;
    }
    
    public abstract boolean isIntegral();
    public boolean isFloatingPoint() {
        return false;
    }
    
    public boolean isNumber() {
        return isIntegral() || isFloatingPoint();
    }
    
    
    public String generateCDefinition(String identifier) {
        return generateCDefinition() + " " + identifier;
    }
    
}
