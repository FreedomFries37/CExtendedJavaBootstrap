package radin.midanalysis.transformation;

import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.TypeAugmentedSemanticTree;
import radin.midanalysis.typeanalysis.analyzers.ExpressionTypeAnalyzer;
import radin.midanalysis.typeanalysis.TypeAnalyzer;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;

import java.util.LinkedList;
import java.util.List;

public class TASTTransformer extends TreeTransformer<TypeAugmentedSemanticNode> {
    
    @Override
    protected TypeAugmentedSemanticNode transform() {
        return getHead();
    }
    
    protected boolean determineTypes(TypeAnalyzer other) {
        other.setTrackerStack(trackerStack);
        return other.determineTypes();
    }
    
    public TypeAugmentedSemanticNode varNode(String varName) {
        AbstractSyntaxNode ast = new AbstractSyntaxNode(ASTNodeType.id, new Token(TokenType.t_id, varName));
        TypeAugmentedSemanticNode tree = TypeAugmentedSemanticTree.convertAST(ast, getEnvironment());
        ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(tree);
        if(!determineTypes(typeAnalyzer)) return null;
        return tree;
    }
    
    public boolean callFunctionOn(String functionName, List<TypeAugmentedSemanticNode> sequence) {
        AbstractSyntaxNode sequenceAST = new AbstractSyntaxNode(ASTNodeType.sequence);
        TypeAugmentedSemanticNode tree = TypeAugmentedSemanticTree.convertAST(sequenceAST, getEnvironment());
        
        try {
            if(!encapsulate(sequence, tree)) return false;
        } catch (TargetsNotConsecutive targetsNotConsecutive) {
            return false;
        }
        
        TypeAugmentedSemanticNode name = varNode(functionName);
        insertBefore(tree, name);
        TypeAugmentedSemanticNode call =
                TypeAugmentedSemanticTree.convertAST(new AbstractSyntaxNode(ASTNodeType.function_call),
                        getEnvironment());
        try {
            if(!encapsulate(call, name, tree)) return false;
        } catch (TargetsNotConsecutive targetsNotConsecutive) {
            return false;
        }
        ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(call);
        return determineTypes(typeAnalyzer);
    }
    
    
    
    public boolean callMethodOn(TypeAugmentedSemanticNode object, String functionName,
                                List<TypeAugmentedSemanticNode> sequence) {
        AbstractSyntaxNode sequenceAST = new AbstractSyntaxNode(ASTNodeType.sequence);
        TypeAugmentedSemanticNode sequenceTree = TypeAugmentedSemanticTree.convertAST(sequenceAST, getEnvironment());
        
        try {
            if(!encapsulate(sequence, sequenceTree)) return false;
        } catch (TargetsNotConsecutive targetsNotConsecutive) {
            return false;
        }
        
        TypeAugmentedSemanticNode name = varNode(functionName);
        insertAfter(object, name);
        insertAfter(name, sequenceTree);
        TypeAugmentedSemanticNode call =
                TypeAugmentedSemanticTree.convertAST(new AbstractSyntaxNode(ASTNodeType.function_call),
                        getEnvironment());
        try {
            if(!encapsulate(call, object, name, sequenceTree)) return false;
        } catch (TargetsNotConsecutive targetsNotConsecutive) {
            return false;
        }
        ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(call);
        return determineTypes(typeAnalyzer);
    }
    
    public boolean callMethodOn(TypeAugmentedSemanticNode object, String functionName) {
        return callMethodOn(object, functionName, new LinkedList<>());
    }
}
