package radin.core.semantics.generics;

import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.CXStructType;
import radin.core.semantics.types.compound.ICXClassType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.semantics.types.primitives.*;
import radin.core.utility.Reference;

import java.util.List;

public class CXParameterizedType extends CXType implements ICXClassType {
    
    private ICXClassType upperBound;
    private Token name;
    
    public CXParameterizedType(ICXClassType upperBound, Token name, TypeEnvironment e) {
        this.upperBound = upperBound;
        this.name = name;
    }
    
    
    
    /**
     * Creates a declaration of a variable of a certain type
     *
     * @param identifier the identifier
     * @return the C equivalent declaration
     */
    @Override
    public String generateCDeclaration(String identifier) {
        return name + " " + identifier;
    }
    
    /**
     * Gets whether a type is valid under a certain type environment A type is valid if it's a correctly formed
     * Primitive, a typedef/alias of a valid type or if the type is a compound type, all of it's members are valid
     *
     * @param e the type environment to check validity
     * @return if this type is valid
     */
    @Override
    public boolean isValid(TypeEnvironment e) {
        return false;
    }
    
    /**
     * Used as a shortcut for checking if a type is primitive (ie: a non-compound C type) Primitive Types: {@link
     * CXPrimitiveType} {@link ShortPrimitive} {@link LongPrimitive} {@link UnsignedPrimitive} {@link PointerType}
     * {@link ArrayType}
     *
     * @return if it's one of these classes
     */
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    /**
     * Returns the data size of a type.
     *
     * @param e the type environment to check in
     * @return if {@link CXType#isValid(TypeEnvironment)} returns true, its size in bytes, otherwise -1
     */
    @Override
    public long getDataSize(TypeEnvironment e) {
        return upperBound.getAsCXType().getDataSize(e);
    }
    
    /**
     * Same as {@link CXType#is(CXType, TypeEnvironment)}, except with the ability to determine whether to use strict
     * boolean equality
     * <p>
     * {@link TypeEnvironment#isStrict(CXType, CXType)} is preferred, as it properly unwraps types that need to be
     * unwrapped
     *
     * @param other                   the other type
     * @param e                       the type environment to check in
     * @param strictPrimitiveEquality Uses a more strict type check for primitives {@link CXPrimitiveType#is(CXType,
     *                                TypeEnvironment, boolean)}
     * @return where this object type "strictly is" another type
     */
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        return this == other;
    }
    
    @Override
    public String generateCDefinition() {
        return null;
    }
    
    @Override
    public CXMethod getMethod(Token name, ParameterTypeList parameterTypeList, Reference<Boolean> isVirtual) {
        return upperBound.getMethod(name, parameterTypeList, isVirtual);
    }
    
    @Override
    public TypeEnvironment getEnvironment() {
        return upperBound.getEnvironment();
    }
    
    @Override
    public CXStructType getVTable() {
        return upperBound.getVTable();
    }
    
    @Override
    public List<CXMethod> getVirtualMethodsOrder() {
        return upperBound.getVirtualMethodsOrder();
    }
    
    @Override
    public List<CXMethod> getConcreteMethodsOrder() {
        return upperBound.getConcreteMethodsOrder();
    }
    
    @Override
    public boolean isVirtual(Token name, ParameterTypeList typeList) {
        return upperBound.isVirtual(name, typeList);
    }
    
    @Override
    public List<FieldDeclaration> getFields() {
        return upperBound.getFields();
    }
    
    @Override
    public String getTypeName() {
        return name.getImage();
    }
    
    @Override
    public CXIdentifier getTypeNameIdentifier() {
        return new CXIdentifier(name);
    }
    
    @Override
    public CXType getAsCXType() {
        return this;
    }
    
    @Override
    public void addConstructors(List<CXConstructor> constructors) {
        throw new IllegalStateException();
    }
    
    @Override
    public void setEnvironment(TypeEnvironment environment) {
        throw new IllegalStateException();
    }
    
    @Override
    public CXMethod getInitMethod() {
        return null;
    }
    
    @Override
    public List<CXConstructor> getConstructors() {
        return null;
    }
    
    @Override
    public CXConstructor getConstructor(List<CXType> parameters, TypeEnvironment environment) {
        return null;
    }
    
    @Override
    public CXConstructor getConstructor(int length) {
        return null;
    }
    
    @Override
    public CXConstructor getConstructor(ParameterTypeList parameterTypeList) {
        return null;
    }
    
    @Override
    public void generateSuperMethods(String vtablename) {
    
    }
    
    @Override
    public CXMethod getSuperMethod(String name, ParameterTypeList typeList) {
        return null;
    }
    
    @Override
    public CXStructType getStructEquivalent() {
        return null;
    }
    
    @Override
    public List<CXMethod> getGeneratedSupers() {
        return null;
    }
    
    @Override
    public boolean canInstantiateDirectly() {
        return false;
    }
    
    @Override
    public String getCTypeName() {
        return null;
    }
    
    @Override
    public String toString() {
        return name.getImage();
    }
    
    @Override
    public CXType propagateGenericReplacement(CXParameterizedType original, CXType replacement) {
        if(original == this) return replacement;
        return this;
    }
    
    
}
