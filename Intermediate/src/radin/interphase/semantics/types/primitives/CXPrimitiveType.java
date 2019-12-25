package radin.interphase.semantics.types.primitives;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;

public class CXPrimitiveType extends AbstractCXPrimitiveType {
    enum Primitives {
        _char("char"),
        _int("int"),
        _float("float"),
        _double("double"),
        _void("void");
        
        String cEquivalent;
        
        
        Primitives(String cEquivalent) {
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
    
    @Override
    public boolean is(CXType other, TypeEnvironment e) {
        return other instanceof CXPrimitiveType;
    }
}
