package radin.interphase.semantics.types;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.exceptions.InvalidPrimitiveException;

public class UnsignedPrimitive implements IPrimitiveCXType {
    
    private IPrimitiveCXType primitiveCXType;
    
    public UnsignedPrimitive(PrimitiveCXType cPrimitiveType) throws InvalidPrimitiveException {
        if(cPrimitiveType.equals(PrimitiveCXType.VOID) ||
                cPrimitiveType.equals(PrimitiveCXType.FLOAT) ||
                cPrimitiveType.equals(PrimitiveCXType.DOUBLE)){
            throw new InvalidPrimitiveException();
        }
        this.primitiveCXType = cPrimitiveType;
    }
    
    public UnsignedPrimitive() throws InvalidPrimitiveException {
        this(PrimitiveCXType.INTEGER);
    }
    
    @Override
    public String generateCDefinition() {
        return "unsigned " + primitiveCXType.generateCDefinition();
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return true;
    }
}
