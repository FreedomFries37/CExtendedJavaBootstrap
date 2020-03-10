package radin.core.semantics.types.primitives;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.exceptions.InvalidPrimitiveException;
import radin.core.semantics.types.CXType;

public class UnsignedPrimitive extends AbstractCXPrimitiveType {
    
    private AbstractCXPrimitiveType primitiveCXType;
    
    public UnsignedPrimitive(AbstractCXPrimitiveType cPrimitiveType) throws InvalidPrimitiveException {
        if(cPrimitiveType.equals(CXPrimitiveType.VOID) ||
                cPrimitiveType.equals(CXPrimitiveType.FLOAT) ||
                cPrimitiveType.equals(CXPrimitiveType.DOUBLE)){
            throw new InvalidPrimitiveException();
        }
        this.primitiveCXType = cPrimitiveType;
    }
    
    public UnsignedPrimitive() throws InvalidPrimitiveException {
        this(CXPrimitiveType.INTEGER);
    }
    
    public static UnsignedPrimitive createUnsignedShort() {
        try {
            return new UnsignedPrimitive(CXPrimitiveType.CHAR);
        } catch (InvalidPrimitiveException e) {
            return null;
        }
    }
    
    public static UnsignedPrimitive createUnsigned(AbstractCXPrimitiveType other) {
        try {
            return new UnsignedPrimitive(other);
        } catch (InvalidPrimitiveException e) {
            return null;
        }
    }
    
    public AbstractCXPrimitiveType getPrimitiveCXType() {
        return primitiveCXType;
    }
    
    @Override
    public String generateCDefinition() {
        return "unsigned " + primitiveCXType.generateCDefinition();
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return true;
    }
    
    @Override
    public boolean isIntegral() {
        return true;
    }
    
    @Override
    public boolean isChar() {
        return primitiveCXType.isChar();
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        return primitiveCXType.is(other, e, strictPrimitiveEquality);
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return primitiveCXType.getDataSize(e);
    }
}
