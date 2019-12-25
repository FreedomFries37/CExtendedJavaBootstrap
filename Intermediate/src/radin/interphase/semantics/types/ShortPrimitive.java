package radin.interphase.semantics.types;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.exceptions.InvalidPrimitiveException;

public class ShortPrimitive implements IPrimitiveCXType {
    
    private IPrimitiveCXType primitiveCXType;
    
    public ShortPrimitive(PrimitiveCXType cPrimitiveType) throws InvalidPrimitiveException {
        if(!(cPrimitiveType.equals(PrimitiveCXType.INTEGER))) {
            throw new InvalidPrimitiveException();
        }
        this.primitiveCXType = cPrimitiveType;
    }
    
    public ShortPrimitive() throws InvalidPrimitiveException {
        this(PrimitiveCXType.INTEGER);
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
}
