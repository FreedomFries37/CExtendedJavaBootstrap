package radin.compilation.tags;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.TypeAugmentedSemanticTree;
import radin.typeanalysis.analyzers.ExpressionTypeAnalyzer;

import java.util.LinkedList;
import java.util.List;

public class MultiDimensionalArrayWithSizeTag extends AbstractCompilationTag {
    
    /**
     * The dimensions of the array, where k > 0
     */
    private int k;
    private List<TypeAugmentedSemanticNode> expressions;
    
    public MultiDimensionalArrayWithSizeTag(int k, List<AbstractSyntaxNode> expressions, TypeEnvironment environment) {
        super("" + k + "-DIMENSIONAL ARRAY", ASTNodeType.declaration);
        this.expressions = new LinkedList<>();
        this.k = k;
        for (AbstractSyntaxNode expression : expressions) {
            this.expressions.add(new TypeAugmentedSemanticTree(expression, environment).getHead());
        }
    }
    
    public List<TypeAugmentedSemanticNode> getExpressions() {
        return expressions;
    }
    
    public int getK() {
        return k;
    }
    
    public boolean isConstant() {
        if(k > expressions.size()) return false;
        for (TypeAugmentedSemanticNode expression : expressions) {
            ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(expression);
            if(!typeAnalyzer.determineTypes() || typeAnalyzer.hasErrors()) return false;
        }
        return true;
    }
}
