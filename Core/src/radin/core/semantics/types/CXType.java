package radin.core.semantics.types;

import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.core.semantics.types.primitives.PointerType;

/**
 * Base type for any CXType. This needs to be inherited for a type to be properly tracked
 * Represents all C Types and Class Types
 */
public abstract class CXType implements CXEquivalent {
    
    
    /**
     * Creates a declaration of a variable of a certain type
     * @param identifier the identifier
     * @return the C equivalent declaration
     */
    abstract public String generateCDeclaration(String identifier);
    
    /**
     * Creates a token equivalent of a C definition
     * @return a {@code typename} token with the c definition of the type as the image
     */
    public Token getTokenEquivalent() {
        return new Token(TokenType.t_typename, generateCDefinition());
    }
    
    /**
     * Gets whether a type is valid under a certain type environment
     * A type is valid if it's a correctly formed Primitive, a typedef/alias of a valid type
     * or if the type is a compound type, all of it's members are valid
     * @param e the type environment to check validity
     * @return if this type is valid
     */
    abstract public boolean isValid(TypeEnvironment e);
    
    /**
     * Used as a shortcut for checking if a type is primitive (ie: a non-compound C type)
     * Primitive Types:
     *  {@link radin.core.semantics.types.primitives.CXPrimitiveType}
     *  {@link radin.core.semantics.types.primitives.ShortPrimitive}
     *  {@link radin.core.semantics.types.primitives.LongPrimitive}
     *  {@link radin.core.semantics.types.primitives.UnsignedPrimitive}
     *  {@link PointerType}
     *  {@link radin.core.semantics.types.primitives.ArrayType}
     * @return if it's one of these classes
     */
    abstract public boolean isPrimitive();
    
    /**
     * Returns the data size of a type.
     * @param e the type environment to check in
     * @return if {@link CXType#isValid(TypeEnvironment)} returns true, its size in bytes, otherwise -1
     */
    abstract public long getDataSize(TypeEnvironment e);
    
    /**
     * Returns whether a this "is" another type
     * "is" is determined on a type by type basis.
     *
     * {@link TypeEnvironment#is(CXType, CXType)} is preferred, as it properly unwraps types that need to be unwrapped
     *
     * @param other the other type
     * @param e the type environment to check within
     * @return where this object type "is" another type
     */
    public boolean is(CXType other, TypeEnvironment e) {
        return is(other, e, false);
    }
    
    /**
     * Same as {@link CXType#is(CXType, TypeEnvironment)}, except with the ability to determine
     * whether to use strict boolean equality
     *
     * {@link TypeEnvironment#isStrict(CXType, CXType)} is preferred, as it properly unwraps types that need to be
     * unwrapped
     *
     * @param other the other type
     * @param e the type environment to check in
     * @param strictPrimitiveEquality Uses a more strict type check for
     *                                primitives {@link CXPrimitiveType#is(CXType, TypeEnvironment, boolean)}
     * @return where this object type "strictly is" another type
     */
    abstract public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality);
    
    /**
     * Returns whether this type and another type are exactly the same. This occurs when
     *  {@code this} is {@code other && other} is {@code this}.
     *
     *
     * When checking for equality, {@link TypeEnvironment#isStrict(CXType, CXType)} is used
     *
     * @param other the other type
     * @param e the type environemnt to check in
     * @return whether the types are both equivalent to the exact same type
     */
    public boolean isExact(CXType other, TypeEnvironment e) {
        boolean leftLTE = e.isStrict(this, other);
        boolean rightLTE = e.isStrict(other, this);
        return leftLTE && rightLTE;
    }
    
    @Override
    public String toString() {
        return generateCDefinition().replaceAll("\\s+", " ");
    }
    
    /**
     * Gets the type indirection
     * @return
     * @deprecated unsure why this exists, system could be reworked to avoid this
     */
    @Deprecated(forRemoval = true)
    public CXType getTypeIndirection() {
        return this;
    }
    
    /**
     * Redirects a type back to it's original if needed
     * @param e
     * @return
     * @deprecated unsure why this exists, system could be reworked to avoid this
     */
    @Deprecated(forRemoval = true)
    public CXType getTypeRedirection(TypeEnvironment e) {
        return this;
    }
    
    /**
     * Gets the C type equivalent. This does nothing if the type is a type that is already defined in C
     * @return
     */
    public CXType getCTypeIndirection() {
        return this;
    }
    
    /**
     * Creates a pointer type to this type
     * @return a pointer typ object
     */
    final public PointerType toPointer() {
        return new PointerType(this);
    }
    
    
}
