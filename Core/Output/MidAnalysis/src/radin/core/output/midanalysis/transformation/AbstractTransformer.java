package radin.core.output.midanalysis.transformation;

import radin.core.chaining.ICompilerFunction;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.output.midanalysis.ScopedTypeTracker;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.midanalysis.TypeAugmentedSemanticTree;
import radin.core.output.midanalysis.typeanalysis.analyzers.ExpressionTypeAnalyzer;
import radin.core.output.typeanalysis.TypeAnalyzer;
import radin.core.output.typeanalysis.VariableTypeTracker;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public abstract class AbstractTransformer extends ScopedTypeTracker implements
        ITransformer,
        ICompilerFunction<TypeAugmentedSemanticNode, TypeAugmentedSemanticNode> {
    
    private Stack<TypeAugmentedSemanticNode> headStack = new Stack<>();
    
    public AbstractTransformer(Stack<VariableTypeTracker> trackerStack) {
        super(trackerStack);
    }
    
    public AbstractTransformer(ScopedTypeTracker old) {
        super(old);
    }
    
    public AbstractTransformer() {
    }
    
    protected final TypeAugmentedSemanticNode getHead() {
        return headStack.peek();
    }
    
    @Override
    public final TypeAugmentedSemanticNode invoke(TypeAugmentedSemanticNode input) {
        headStack.push(input);
        TypeAugmentedSemanticNode output = transform();
        headStack.pop();
        return output;
    }
    
    protected abstract TypeAugmentedSemanticNode transform();
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return new LinkedList<>();
    }
    
    
    @Override
    public boolean insertFirst(TypeAugmentedSemanticNode node) {
        getRelevant().add(0, node);
        return true;
    }
    
    @Override
    public boolean insertFirst(List<? extends TypeAugmentedSemanticNode> nodes) {
        return getRelevant().addAll(0, nodes);
    }
    
    @Override
    public boolean insertLast(TypeAugmentedSemanticNode node) {
        getRelevant().add(node);
        return true;
    }
    
    @Override
    public boolean insertLast(List<? extends TypeAugmentedSemanticNode> nodes) {
        return getRelevant().addAll(nodes);
    }
    
    @Override
    public boolean insertAfter(TypeAugmentedSemanticNode target, TypeAugmentedSemanticNode node) {
        if(target == null) return insertLast(node);
        int index = indexOf(target);
        if(index == -1) throw new IndexOutOfBoundsException(index);
        getRelevant().add(index, node);
        return false;
    }
    
    @Override
    public boolean insertAfter(TypeAugmentedSemanticNode target, List<? extends TypeAugmentedSemanticNode> nodes) {
        if(target == null) return insertLast(nodes);
        int index = indexOf(target);
        if(index == -1) throw new IndexOutOfBoundsException(index);
        getRelevant().addAll(index + 1, nodes);
        return false;
    }
    
    @Override
    public boolean insertBefore(TypeAugmentedSemanticNode target, TypeAugmentedSemanticNode node) {
        if(target == null) return insertFirst(node);
        int index = indexOf(target);
        if(index == -1) throw new IndexOutOfBoundsException(index);
        getRelevant().add(index, node);
        return false;
    }
    
    @Override
    public boolean insertBefore(TypeAugmentedSemanticNode target, List<? extends TypeAugmentedSemanticNode> nodes) {
        if(target == null) return insertLast(nodes);
        int index = indexOf(target);
        if(index == -1) throw new IndexOutOfBoundsException(index);
        getRelevant().addAll(index, nodes);
        return false;
    }
    
    @Override
    public boolean delete(TypeAugmentedSemanticNode node) {
        return getRelevant().remove(node);
    }
    
    @Override
    public TypeAugmentedSemanticNode next(TypeAugmentedSemanticNode target) {
        int targetIndex = indexOf(target);
        if(targetIndex == getRelevant().size() - 1) return null;
        return getRelevant().get(targetIndex + 1);
    }
    
    @Override
    public TypeAugmentedSemanticNode previous(TypeAugmentedSemanticNode target) {
        int targetIndex = indexOf(target);
        if(targetIndex == 0) return null;
        return getRelevant().get(targetIndex - 1);
    }
    
    private int indexOf(TypeAugmentedSemanticNode node) {
        return getRelevant().indexOf(node);
    }
    
    @Override
    public List<TypeAugmentedSemanticNode> getRelevant() {
        return getHead().getChildren();
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
