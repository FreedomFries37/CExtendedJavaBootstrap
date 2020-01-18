package radin.core.semantics.generics;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.ICXClassType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.utility.Reference;


import java.util.List;

public class CXGenericTypeInstance extends CXType implements ICXClassType {
    
    private CXGenericDeclaration<? extends CXClassType> genericClass;
    private List<ParameterType> types;
    private CXClassType reified;
    
    @Override
    public String generateCDeclaration(String identifier) {
        return null;
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return false;
    }
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return 0;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        return false;
    }
    
    @Override
    public String generateCDefinition() {
        return null;
    }
    
    @Override
    public TypeEnvironment getEnvironment() {
        return null;
    }
    
    @Override
    public void addConstructors(List<CXConstructor> constructors) {
    
    }
    
    @Override
    public void setEnvironment(TypeEnvironment environment) {
    
    }
    
    @Override
    public List<CXMethod> getVirtualMethodsOrder() {
        return null;
    }
    
    @Override
    public List<CXMethod> getConcreteMethodsOrder() {
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
    public CXMethod getMethod(String name, ParameterTypeList parameterTypeList, Reference<Boolean> isVirtual) {
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
    public boolean isVirtual(String name, ParameterTypeList typeList) {
        return false;
    }
    
    @Override
    public List<FieldDeclaration> getFields() {
        return null;
    }
    
    @Override
    public String getTypeName() {
        return null;
    }
    
    @Override
    public String getCTypeName() {
        return null;
    }
    
    @Override
    public CXIdentifier getTypeNameIdentifier() {
        return null;
    }
}
