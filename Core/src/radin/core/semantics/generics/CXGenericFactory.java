package radin.core.semantics.generics;

import radin.core.lexical.Token;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;

import java.util.ArrayList;

public abstract class CXGenericFactory <T> extends CXType implements ICXGenericFactory<T> {
    
    private TypeEnvironment environment;
    private ArrayList<CXParameterizedType> parameterizedTypes;
    private AbstractSyntaxNode originalRelevantTree;
    
    private Token declarationToken;
   
    
    public CXGenericFactory(TypeEnvironment environment, ArrayList<CXParameterizedType> parameterizedTypes, AbstractSyntaxNode originalRelevantTree, Token declarationToken) {
        this.environment = environment;
        this.parameterizedTypes = parameterizedTypes;
        this.originalRelevantTree = originalRelevantTree;
        this.declarationToken = declarationToken;
    }
    
    @Override
    public Token getDeclarationToken() {
        return declarationToken;
    }
    
    @Override
    public TypeEnvironment getEnvironment() {
        return environment;
    }
    
    @Override
    public ArrayList<CXParameterizedType> getParameterizedTypes() {
        return parameterizedTypes;
    }
    
    @Override
    public AbstractSyntaxNode getOriginalRelevantTree() {
        return originalRelevantTree;
    }
}
