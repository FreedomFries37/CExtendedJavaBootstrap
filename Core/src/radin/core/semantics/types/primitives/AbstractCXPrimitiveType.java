package radin.core.semantics.types.primitives;

import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;

public abstract class AbstractCXPrimitiveType extends CXType {
    
    
    public boolean isPrimitive() {
        return true;
    }
    
    public abstract boolean isIntegral();
    public boolean isFloatingPoint() {
        return false;
    }
    public boolean isChar() { return false; }
    public boolean isNumber() {
        return isIntegral() || isFloatingPoint();
    }
    
    
    public String generateCDeclaration(String identifier) {
        return generateCDefinition() + " " + identifier;
    }

    @Override
    public CXIdentifier getIdentifier() {
        return CXIdentifier.from(toString());
    }
}
