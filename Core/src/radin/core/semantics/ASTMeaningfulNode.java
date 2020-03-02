package radin.core.semantics;

import radin.core.AbstractTree;
import radin.core.lexical.Token;

import java.util.List;

/**
 * Represents a node that has a {@link ASTNodeType}
 * @param <R>
 */
public abstract class ASTMeaningfulNode<T extends ASTMeaningfulNode<T>> extends AbstractTree<T> {
    
    abstract public ASTNodeType getType();
    abstract public Token getToken();
    
    @Override
    abstract public List<T> getDirectChildren();
    public ASTMeaningfulNode<T> getChildWithASTType(ASTNodeType type) {
        return getChildWithASTType(type, 0);
    }
    
    /**
     * Gets the n-th child with n-th node of ASTNodeType {@code type}
     * @param type the type
     * @param count the count
     * @return the child
     */
    public ASTMeaningfulNode<T> getChildWithASTType(ASTNodeType type, int count) {
        int found = -1;
        for (ASTMeaningfulNode<T> directChild : getDirectChildren()) {
            if(directChild.getType().equals(type)) {
                if(++found == count) {
                    return directChild;
                }
            }
        }
        return null;
    }
    
    
    
    public ASTMeaningfulNode<T> getChildAtIndex(int index) {
        return getDirectChildren().get(index);
    }
}
