package radin.midanalysis.pattern_replacement.singletons;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.TypeEnvironment;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.pattern_replacement.PatternSingleton;

public class TypeSingleton extends PatternSingleton {
    
    private ASTNodeType type;
    
    public TypeSingleton(ASTNodeType type) {
        this.type = type;
    }
    
    @Override
    public boolean match(TypeAugmentedSemanticNode node, TypeEnvironment environment) {
        return node.getTreeType().equals(type);
    }
    
}
