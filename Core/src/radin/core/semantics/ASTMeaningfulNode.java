package radin.core.semantics;

import radin.core.lexical.Token;

/**
 * Represents a node that has a {@link ASTNodeType}
 * @param <T>
 */
public abstract class ASTMeaningfulNode<T extends ASTMeaningfulNode<T>> extends AbstractGenericMeaningfulNode<ASTNodeType, T> {
    
    abstract public ASTNodeType getTreeType();
    abstract public Token getToken();
    
    
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
            if(directChild.getTreeType().equals(type)) {
                if(++found == count) {
                    return directChild;
                }
            }
        }
        return null;
    }
    
    
    
    
    public T getChildAtIndex(int index) {
        return getDirectChildren().get(index);
    }
}
