package radin.interphase.semantics.types;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.exceptions.InvalidPrimitiveException;

public class LongPrimitive implements IPrimitiveCXType {
    
    private IPrimitiveCXType primitiveCXType;
    
    public LongPrimitive(PrimitiveCXType cPrimitiveType) throws InvalidPrimitiveException {
        if(!(cPrimitiveType.equals(PrimitiveCXType.INTEGER) || cPrimitiveType.equals(PrimitiveCXType.DOUBLE)))
            throw new InvalidPrimitiveException();
        this.primitiveCXType = cPrimitiveType;
    }
    
    public LongPrimitive(LongPrimitive type) throws InvalidPrimitiveException {
        if(!type.primitiveCXType.equals(PrimitiveCXType.INTEGER)) {
            throw new InvalidPrimitiveException();
        }
        this.primitiveCXType = type;
    }
    
    public LongPrimitive() throws InvalidPrimitiveException {
        this(PrimitiveCXType.INTEGER);
    }
    
    @Override
    public String generateCDefinition() {
        return "long " + primitiveCXType.generateCDefinition();
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return true;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        if(primitiveCXType.equals(PrimitiveCXType.INTEGER)) return e.getLongIntSize();
        if(primitiveCXType.equals(PrimitiveCXType.DOUBLE)) return e.getLongDoubleSize();
        return e.getLongLongSize();
    }
}
