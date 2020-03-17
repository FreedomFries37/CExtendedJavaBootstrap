package radin.core.semantics.generics;

import radin.core.lexical.Token;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface ICXGeneric<T> {
    
    Collection<AbstractSyntaxNode> getCreatedTrees();
    
    void setOriginalRelevantTree(AbstractSyntaxNode originalRelevantTree);
    
    Token getDeclarationToken();
    
    TypeEnvironment getEnvironment();
    
    default CXType getFixedCXType(CXType original, List<CXType> inputTypes) {
        CXType output = original;
        for (int i = 0; i < getParameterizedTypes().size(); i++) {
            output = output.propagateGenericReplacement(
                    getParameterizedTypes().get(i), inputTypes.get(i)
            );
        }
        return output;
    }
    default AbstractSyntaxNode createModifiedTree(AbstractSyntaxNode node, List<CXType> inputTypes) {
        if(node instanceof TypedAbstractSyntaxNode) {
            return createModifiedTree((TypedAbstractSyntaxNode) node, inputTypes);
        }
        ArrayList<AbstractSyntaxNode> fixedChildren = new ArrayList<>(node.getDirectChildren().size());
        for (AbstractSyntaxNode directChild : node.getDirectChildren()) {
            fixedChildren.add(createModifiedTree(directChild, inputTypes));
        }
        return new AbstractSyntaxNode(node, fixedChildren);
    }
    
    default TypedAbstractSyntaxNode createModifiedTree(TypedAbstractSyntaxNode node, List<CXType> inputTypes) {
        ArrayList<AbstractSyntaxNode> fixedChildren = new ArrayList<>(node.getDirectChildren().size());
        for (AbstractSyntaxNode directChild : node.getDirectChildren()) {
            fixedChildren.add(createModifiedTree(directChild, inputTypes));
        }
        CXType newType = getFixedCXType(node.getCxType(), inputTypes);
        return new TypedAbstractSyntaxNode(node, newType, fixedChildren);
    }
    
    List<CXParameterizedType> getParameterizedTypes();
    
    default boolean typesValid(List<CXType> input) {
        for (int i = 0; i < getParameterizedTypes().size(); i++) {
            if (!getEnvironment().isStrict(getParameterizedTypes().get(i), input.get(i))) return false;
        }
        return true;
    }
    
    GenericInstance<T> createInstance(List<CXType> types);
    
    AbstractSyntaxNode getOriginalRelevantTree();
}
