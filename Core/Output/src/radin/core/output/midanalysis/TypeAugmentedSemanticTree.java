package radin.core.output.midanalysis;

import radin.core.output.tags.TypeDefHelperTag;
import radin.core.AbstractTree;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.CXCompoundTypeNameIndirection;
import radin.core.semantics.types.TypeAbstractSyntaxNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TypeAugmentedSemanticTree extends AbstractTree<TypeAugmentedSemanticNode> {
    
   
    private TypeAugmentedSemanticNode head;
    
    public TypeAugmentedSemanticTree(AbstractSyntaxNode headAST, TypeEnvironment environment) {
        this.head = convertAST(headAST, environment);
    }
    
    public static TypeAugmentedSemanticNode convertAST(AbstractSyntaxNode head, TypeEnvironment e) {
        List<TypeAugmentedSemanticNode> children = new LinkedList<>();
        for (AbstractSyntaxNode abstractSyntaxNode : head.getChildList()) {
            children.add(convertAST(abstractSyntaxNode, e));
        }
        if(head instanceof TypeAbstractSyntaxNode) {
            TypeAbstractSyntaxNode typeAbstractSyntaxNode = (TypeAbstractSyntaxNode) head;
            CXType type = typeAbstractSyntaxNode.getCxType();
            if(type instanceof CXCompoundTypeNameIndirection) {
                CXType newType = e.getNamedCompoundType(((CXCompoundTypeNameIndirection) type).getTypename());
                if(newType != null) {
                    head = new TypeAbstractSyntaxNode(head.getType(), newType, head.getChildList());
                    TypeAugmentedSemanticNode output = new TypeAugmentedSemanticNode(head, children);
                    output.addCompilationTag(new TypeDefHelperTag(type));
                    return output;
                }
            }
        }
        return new TypeAugmentedSemanticNode(head, children);
    }
    
    @Override
    public List<TypeAugmentedSemanticNode> postfix() {
        return head.postfix();
    }
    
    @Override
    public List<? extends AbstractTree<TypeAugmentedSemanticNode>> getDirectChildren() {
        return Collections.singletonList(head);
    }
    
    public TypeAugmentedSemanticNode getHead() {
        return head;
    }
}
