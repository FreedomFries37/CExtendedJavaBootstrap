package radin.core.semantics.types.primitives;

import radin.core.semantics.exceptions.InvalidPrimitiveException;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;

public class LongPrimitive extends AbstractCXPrimitiveType {
    
    private AbstractCXPrimitiveType primitiveCXType;
    
    public LongPrimitive(CXPrimitiveType cPrimitiveType) throws InvalidPrimitiveException {
        if(!(cPrimitiveType.equals(CXPrimitiveType.INTEGER) || cPrimitiveType.equals(CXPrimitiveType.DOUBLE)))
            throw new InvalidPrimitiveException();
        this.primitiveCXType = cPrimitiveType;
    }
    
    
    public LongPrimitive(LongPrimitive type) throws InvalidPrimitiveException {
        if(!type.primitiveCXType.equals(CXPrimitiveType.INTEGER)) {
            throw new InvalidPrimitiveException();
        }
        this.primitiveCXType = type;
    }
    
    public static LongPrimitive create(AbstractCXPrimitiveType prim) throws InvalidPrimitiveException{
        if(prim instanceof CXPrimitiveType) return new LongPrimitive(((CXPrimitiveType) prim));
        else if(prim instanceof LongPrimitive) return new LongPrimitive(((LongPrimitive) prim));
        throw new InvalidPrimitiveException(prim);
    }
    
    public LongPrimitive() throws InvalidPrimitiveException {
        this(CXPrimitiveType.INTEGER);
    }
    
    public static LongPrimitive create() {
        try {
            return new LongPrimitive();
        } catch (InvalidPrimitiveException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean isIntegral() {
        return true;
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
        if(primitiveCXType.equals(CXPrimitiveType.INTEGER)) return e.getLongIntSize();
        if(primitiveCXType.equals(CXPrimitiveType.DOUBLE)) return e.getLongDoubleSize();
        return e.getLongLongSize();
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        return primitiveCXType.is(other, e);
    }
    
    
}
