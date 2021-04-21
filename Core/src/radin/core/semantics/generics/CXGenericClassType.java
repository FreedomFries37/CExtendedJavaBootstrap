package radin.core.semantics.generics;

import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.AbstractCXClassType;
import radin.core.semantics.types.compound.ICXCompoundType;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.utility.Reference;

import java.util.HashMap;
import java.util.List;

public class CXGenericClassType extends CXGenericType<AbstractCXClassType> implements CXCheckedCallable, ICXCompoundType {
    
    public CXGenericClassType(HashMap<CXParameterizedClassType, CXParameterizedTypeInstance<? extends CXType>> parameterMap, AbstractCXClassType baseType, TypeEnvironment environment) {
        super(parameterMap, baseType, environment);
    }
    
    @Override
    public List<CXMethod> getAllMethods() {
        return getBaseType().getAllMethods();
    }
    
    @Override
    public CXMethod getMethodChecked(Token name, ParameterTypeList parameterTypeList, Reference<Boolean> isVirtual) throws GetMethodError {
        for (CXMethod allMethod : getBaseType().getAllMethods()) {
            if(allMethod.getName().getIdentifier().equals(name)) {
    
    
                CXType returnType = allMethod.getReturnType();
                if(returnType instanceof CXParameterizedClassType) {
                    if (getParameterMap().containsKey(returnType)) {
                        if (getParameterMap().get(returnType).getVariance() == CXParameterizedTypeInstance.Variance.CONTRAVARIANCE) {
                            // TODO: Fix this
                            throw new GetMethodError(new VarianceMatchError(name, null, null), name, parameterTypeList);
                        }
                    }
                }
                
                
    
            }
        }
        throw new GetMethodError(name, parameterTypeList);
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
