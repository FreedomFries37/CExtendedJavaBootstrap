package radin.output.tags;

import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.utility.ICompilationSettings;


import java.util.Collections;

public class ArrayWithSizeTag extends MultiDimensionalArrayWithSizeTag {
    
    
    
    public ArrayWithSizeTag(AbstractSyntaxNode expression, TypeEnvironment environment, ConstantDeterminer determiner) {
        super(1, Collections.singletonList(expression), environment, determiner);
        
    }
    
    public TypeAugmentedSemanticNode getExpression() {
        return getExpressions().get(0);
    }
    
    public boolean isConstant() {
        ICompilationSettings.debugLog.info("---------------IGNORE MOST ERRORS BELOW------------------");
        if(!determiner.isConstant(getExpression())) {
            ICompilationSettings.debugLog.info("---------------IGNORE MOST ERRORS ABOVE------------------");
            return false;
        }
        
        return true;
    }
}
