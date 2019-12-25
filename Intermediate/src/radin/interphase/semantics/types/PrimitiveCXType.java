package radin.interphase.semantics.types;

import radin.interphase.semantics.TypeEnvironment;

public class PrimitiveCXType implements IPrimitiveCXType {
    enum Primitives {
        _char("char"),
        _int("int"),
        _float("float"),
        _double("double"),
        _void("void");
        
        String cEquivalent;
        int size;
    
        Primitives(String cEquivalent) {
            this.cEquivalent = cEquivalent;
        }
    }
    
    public static PrimitiveCXType INTEGER = new PrimitiveCXType(Primitives._int);
    public static PrimitiveCXType CHAR = new PrimitiveCXType(Primitives._char);
    public static PrimitiveCXType FLOAT = new PrimitiveCXType(Primitives._float);
    public static PrimitiveCXType DOUBLE = new PrimitiveCXType(Primitives._double);
    public static PrimitiveCXType VOID = new PrimitiveCXType(Primitives._void);
    
    private Primitives myPrimitive;
    
    protected PrimitiveCXType(Primitives myPrimitive) {
        this.myPrimitive = myPrimitive;
    }
    
    public Primitives getMyPrimitive() {
        return myPrimitive;
    }
    
    @Override
    public String generateCDefinition() {
        return myPrimitive.cEquivalent;
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return myPrimitive != Primitives._void;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        PrimitiveCXType that = (PrimitiveCXType) o;
    
        return myPrimitive == that.myPrimitive;
    }
    
    @Override
    public int hashCode() {
        return myPrimitive.hashCode();
    }
    
    @Override
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
}
