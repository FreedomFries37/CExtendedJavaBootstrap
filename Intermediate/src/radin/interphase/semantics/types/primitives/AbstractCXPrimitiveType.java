package radin.interphase.semantics.types.primitives;

import radin.interphase.semantics.types.CXType;

public abstract class AbstractCXPrimitiveType extends CXType {
    
    
    public boolean isPrimitive() {
        return true;
    }
    
    
    public String generateCDefinition(String identifier) {
        return generateCDefinition() + " " + identifier;
    }
}
