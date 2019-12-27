package radin.interphase.semantics.types.primitives;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;

public class CXPrimitiveType extends AbstractCXPrimitiveType {
    enum Primitives {
        _char("char", true , false),
        _int("int", true , false),
        _float("float", false, true),
        _double("double", false, true),
        _void("void", false, false);
        
        String cEquivalent;
        boolean integral;
        boolean floatingPoint;
        
        
        Primitives(String cEquivalent, boolean isIntegral, boolean isFloatingPoint) {
            this.cEquivalent = cEquivalent;
        }
    }
    
    public static CXPrimitiveType INTEGER = new CXPrimitiveType(Primitives._int);
    public static CXPrimitiveType CHAR = new CXPrimitiveType(Primitives._char);
    public static CXPrimitiveType FLOAT = new CXPrimitiveType(Primitives._float);
    public static CXPrimitiveType DOUBLE = new CXPrimitiveType(Primitives._double);
    public static CXPrimitiveType VOID = new CXPrimitiveType(Primitives._void);
    
    private Primitives myPrimitive;
    
    protected CXPrimitiveType(Primitives myPrimitive) {
        this.myPrimitive = myPrimitive;
    }
    
    public Primitives getMyPrimitive() {
        return myPrimitive;
    }
    
    @Override
    public String generateCDefinition() {
        return myPrimitive.cEquivalent;
    }
    
    
    public boolean isValid(TypeEnvironment e) {
        return myPrimitive != Primitives._void;
    }
    
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CXPrimitiveType that = (CXPrimitiveType) o;
        
        return myPrimitive == that.myPrimitive;
    }
    
    @Override
    public int hashCode() {
        return myPrimitive.hashCode();
    }
    
    public long getDataSize(TypeEnvironment e) {
        switch (myPrimitive) {
            case _int: return e.getIntSize();
            case _char: return e.getCharSize();
            case _double: return e.getDoubleSize();
            case _float: return e.getFloatSize();
            case _void: return e.getVoidSize();
            default: return 0;
        }
    }
    
    public static CXPrimitiveType get(String primitive) {
        switch (primitive) {
            case "char": return CHAR;
            case "int": return INTEGER;
            case "float": return FLOAT;
            case "double": return DOUBLE;
            case "void": return VOID;
            default: return null;
        }
    }
    
    public boolean isIntegral() {
        return myPrimitive.integral;
    }
    
    public boolean isFloatingPoint() {
        return myPrimitive.floatingPoint;
    }
    
   
    
    @Override
    public boolean is(CXType other, TypeEnvironment e) {
        
        return other instanceof CXPrimitiveType || other instanceof LongPrimitive || other instanceof ShortPrimitive || other instanceof UnsignedPrimitive;
    }
}
