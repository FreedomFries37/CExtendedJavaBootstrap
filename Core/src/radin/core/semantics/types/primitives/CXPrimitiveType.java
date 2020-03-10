package radin.core.semantics.types.primitives;

import radin.core.semantics.types.wrapped.ConstantType;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;

/**
 * Represents primitive types
 */
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
            this.integral = isIntegral;
            this.floatingPoint = isFloatingPoint;
        }
    }
    
    /**
     * Represents the signed integer type
     */
    public static CXPrimitiveType INTEGER = new CXPrimitiveType(Primitives._int);
    /**
     * Represents the signed char type
     */
    public static CXPrimitiveType CHAR = new CXPrimitiveType(Primitives._char);
    /**
     * Represents the 32-bit floating point type
     */
    public static CXPrimitiveType FLOAT = new CXPrimitiveType(Primitives._float);
    /**
     * Represents the 64-bit floating point type
     */
    public static CXPrimitiveType DOUBLE = new CXPrimitiveType(Primitives._double);
    /**
     * Represents a type that has 0-size in memory. Is never valid as a specifier, unless as a subtype
     * of a {@link PointerType } instance
     */
    public static CXPrimitiveType VOID = new CXPrimitiveType(Primitives._void);
    
    private Primitives myPrimitive;
    
    protected CXPrimitiveType(Primitives myPrimitive) {
        this.myPrimitive = myPrimitive;
    }
    
    @Deprecated
    public Primitives getMyPrimitive() {
        return myPrimitive;
    }
    
    @Override
    public String generateCDefinition() {
        return myPrimitive.cEquivalent;
    }
    
    /**
     *
     * @param e the type environment to check validity
     * @return if this type isn't a {@link CXPrimitiveType#VOID} type
     */
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
    
    /**
     * Gets the data size of the primitive type. This is based on the values in the
     * {@link TypeEnvironment}
     * @param e the type environment to check in
     * @return data size in bytes
     */
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
    
    /**
     *
     * @return if the primitive is an integral
     */
    public boolean isIntegral() {
        return myPrimitive.integral;
    }
    
    /**
     *
     * @return if the primitive is a float or a double
     */
    public boolean isFloatingPoint() {
        return myPrimitive.floatingPoint;
    }
    
    @Override
    public boolean isChar() {
        return this == CHAR;
    }
    
    /**
     *
     * @param other the other type
     * @param e the type environment to check in
     * @param strictPrimitiveEquality if false, just checks if both are primitives. If true, it checks whether the
     *                                types are either both integrals, or both floating point primitives, and no
     *                                other type can be "is" {@link CXPrimitiveType#VOID}
     * @return whether this primitive is another type
     */
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(other instanceof ConstantType) {
            other = ((ConstantType) other).getSubtype();
        }
        if(strictPrimitiveEquality) {
            if (!(other instanceof AbstractCXPrimitiveType)) return false;
    
            return this == CXPrimitiveType.VOID && other == CXPrimitiveType.VOID ||
                    this.isIntegral() && ((AbstractCXPrimitiveType) other).isIntegral() || this.isFloatingPoint() && ((AbstractCXPrimitiveType) other).isFloatingPoint();
        }
        return other.isPrimitive();
        //return other instanceof CXPrimitiveType || other instanceof LongPrimitive || other instanceof
        // ShortPrimitive || other instanceof UnsignedPrimitive;
    }
}
