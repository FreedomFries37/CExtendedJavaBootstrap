package radin.core.semantics.generics;

import radin.core.lexical.Token;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;

import java.util.ArrayList;

public abstract class AbstractCXGenericFactoryType <T> extends CXType implements ICXGenericFactory<T> {
    
    private TypeEnvironment environment;
    private ArrayList<CXParameterizedClassType> parameterizedTypes;
    private AbstractSyntaxNode originalRelevantTree;
    
    private Token declarationToken;
   
    
    public AbstractCXGenericFactoryType(TypeEnvironment environment, ArrayList<CXParameterizedClassType> parameterizedTypes, AbstractSyntaxNode originalRelevantTree, Token declarationToken) {
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
    public ArrayList<CXParameterizedClassType> getParameterizedTypes() {
        return parameterizedTypes;
    }
    
    @Override
    public AbstractSyntaxNode getOriginalRelevantTree() {
        return originalRelevantTree;
    }
    
    @Override
    public void setOriginalRelevantTree(AbstractSyntaxNode originalRelevantTree) {
        this.originalRelevantTree = originalRelevantTree;
    }
}
