package radin.core.semantics.types.wrapped;

import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.generics.AbstractCXGenericFactoryType;
import radin.core.semantics.generics.CXGenericType;
import radin.core.semantics.generics.CXParameterizedClassType;
import radin.core.semantics.generics.GenericInstance;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.AbstractCXClassType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CXGenericClassFactoryType extends AbstractCXGenericFactoryType<AbstractCXClassType> {
    
    private final CXIdentifier id;
    private final List<CXGenericType<AbstractCXClassType>> createdClasses;
    private final List<AbstractSyntaxNode> createdClassASTs;
    
    
    public CXGenericClassFactoryType(TypeEnvironment environment, ArrayList<CXParameterizedClassType> parameterizedTypes, AbstractSyntaxNode originalRelevantTree, CXIdentifier id) {
        super(environment, parameterizedTypes, originalRelevantTree, id.getBase());
        this.id = id;
        this.createdClasses = new LinkedList<>();
        this.createdClassASTs = new LinkedList<>();
    }
    
    @Override
    public CXIdentifier getIdentifier() {
        return id;
    }
    
    @Override
    public Collection<AbstractSyntaxNode> getCreatedTrees() {
        return createdClassASTs;
    }
    
    
    @Override
    public GenericInstance<AbstractCXClassType> createInstance(List<CXType> types) {
        return null;
    }
    
    @Override
    public String generateCDefinition() {
        return null;
    }
    
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
}
