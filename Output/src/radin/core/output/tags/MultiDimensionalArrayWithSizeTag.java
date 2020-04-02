package radin.core.output.tags;

import radin.core.ICompilationMapper;
import radin.core.output.midanalysis.TypeAugmentedSemanticTree;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.utility.ICompilationSettings;


import java.util.LinkedList;
import java.util.List;

public class MultiDimensionalArrayWithSizeTag extends AbstractCompilationTag {
    
    @FunctionalInterface
    public interface ConstantDeterminer {
        boolean isConstant(TypeAugmentedSemanticNode input);
    }
    
    /**
     * The dimensions of the array, where k > 0
     */
    private int k;
    private List<TypeAugmentedSemanticNode> expressions;
    protected ConstantDeterminer determiner;
    
    public MultiDimensionalArrayWithSizeTag(int k, List<AbstractSyntaxNode> expressions, TypeEnvironment environment, ConstantDeterminer determiner) {
        super("" + k + "-DIMENSIONAL ARRAY", ASTNodeType.declaration);
        this.determiner = determiner;
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
            /*
            ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(expression);
            if(!typeAnalyzer.determineTypes() || typeAnalyzer.hasErrors()) return false;
           
             */
            ICompilationSettings.debugLog.info("---------------IGNORE MOST ERRORS BELOW------------------");
            if(!determiner.isConstant(expression)) {
                ICompilationSettings.debugLog.info("---------------IGNORE MOST ERRORS ABOVE------------------");
                return false;
            }
            
        }
        return true;
    }
}
