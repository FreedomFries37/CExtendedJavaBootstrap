package radin.core.output.tags;

import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;


import java.util.Collections;

public class ArrayWithSizeTag extends MultiDimensionalArrayWithSizeTag {
    
    
    
    public ArrayWithSizeTag(AbstractSyntaxNode expression, TypeEnvironment environment, ConstantDeterminer determiner) {
        super(1, Collections.singletonList(expression), environment, determiner);
        
    }
    
    public TypeAugmentedSemanticNode getExpression() {
        return getExpressions().get(0);
    }
    
    public boolean isConstant() {
        return determiner.isConstant(getExpression());
    }
}
