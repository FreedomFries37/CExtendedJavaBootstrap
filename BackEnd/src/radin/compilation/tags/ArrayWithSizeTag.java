package radin.compilation.tags;

import radin.compilation.microcompilers.ExpressionCompiler;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.TypeEnvironment;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.TypeAugmentedSemanticTree;
import radin.typeanalysis.analyzers.ExpressionTypeAnalyzer;

import java.util.HashSet;

public class ArrayWithSizeTag extends AbstractCompilationTag {
    
    private TypeAugmentedSemanticNode expression;
    
    public ArrayWithSizeTag(AbstractSyntaxNode expression, TypeEnvironment environment) {
        super("ARRAY SIZE", ASTNodeType.declaration);
        this.expression = new TypeAugmentedSemanticTree(expression, environment).getHead();
    }
    
    public TypeAugmentedSemanticNode getExpression() {
        return expression;
    }
    
    public boolean isConstant() {
        ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(expression);
        if(!typeAnalyzer.determineTypes()) return false;
        return !typeAnalyzer.hasErrors();
    }
}
