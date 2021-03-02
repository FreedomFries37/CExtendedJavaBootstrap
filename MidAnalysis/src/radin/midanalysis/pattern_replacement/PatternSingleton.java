package radin.midanalysis.pattern_replacement;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.TypeEnvironment;
import radin.midanalysis.TypeAugmentedSemanticNode;

/**
 * A pattern singleton represents part of the tree that makes up a pattern match
 */
public abstract class PatternSingleton {

    /**
     * Backing data type
     */
    private TypeAugmentedSemanticNode data;
    
    public abstract boolean match(TypeAugmentedSemanticNode node, TypeEnvironment environment);
    
    public void determineData(TypeAugmentedSemanticNode node, TypeEnvironment environment) {
        setData(node);
    }
    
    public TypeAugmentedSemanticNode getData() {
        return data;
    }
    
    protected void setData(TypeAugmentedSemanticNode data) {
        this.data = data;
    }
    
    public boolean nodeIsType(TypeAugmentedSemanticNode node, ASTNodeType... type) {
        if(type.length == 0) throw new UnsupportedOperationException();
        for (ASTNodeType astNodeType : type) {
            if(node.getTreeType().equals(astNodeType)) return true;
        }
        return false;
    }
}
