package radin.interphase.semantics.types.primitives;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.exceptions.InvalidPrimitiveException;
import radin.interphase.semantics.types.CXType;

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
    public boolean is(CXType other, TypeEnvironment e) {
        return primitiveCXType.is(other, e);
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return primitiveCXType.getDataSize(e);
    }
}
