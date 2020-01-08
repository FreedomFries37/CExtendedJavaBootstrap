package radin.compilation.tags;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.TypeAugmentedSemanticTree;
import radin.typeanalysis.analyzers.ExpressionTypeAnalyzer;

import java.util.Collections;

public class ArrayWithSizeTag extends MultiDimensionalArrayWithSizeTag {
    
    
    
    public ArrayWithSizeTag(AbstractSyntaxNode expression, TypeEnvironment environment) {
        super(1, Collections.singletonList(expression), environment);
        
    }
    
    public TypeAugmentedSemanticNode getExpression() {
        return getExpressions().get(0);
    }
    
    public boolean isConstant() {
        ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(getExpression());
        if(!typeAnalyzer.determineTypes()) return false;
        return !typeAnalyzer.hasErrors();
    }
}
