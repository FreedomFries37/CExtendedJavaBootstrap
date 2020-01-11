package radin.core.semantics.types.primitives;

import radin.core.semantics.exceptions.InvalidPrimitiveException;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;

public class ShortPrimitive extends AbstractCXPrimitiveType {
    
    private AbstractCXPrimitiveType primitiveCXType;
    
    public ShortPrimitive(CXPrimitiveType cPrimitiveType) throws InvalidPrimitiveException {
        if(!(cPrimitiveType.equals(CXPrimitiveType.INTEGER))) {
            throw new InvalidPrimitiveException();
        }
        this.primitiveCXType = cPrimitiveType;
    }
    
    public ShortPrimitive() throws InvalidPrimitiveException {
        this(CXPrimitiveType.INTEGER);
    }
    
    @Override
    public String generateCDefinition() {
        return "short " + primitiveCXType.generateCDefinition();
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return true;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return e.getShortIntSize();
    }
    
    @Override
    public boolean isIntegral() {
        return true;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        return primitiveCXType.is(other, e);
    }
}
