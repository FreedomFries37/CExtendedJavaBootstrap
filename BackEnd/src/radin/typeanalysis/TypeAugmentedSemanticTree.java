package radin.typeanalysis;

import radin.interphase.AbstractTree;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.CompoundTypeReference;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TypeAugmentedSemanticTree extends AbstractTree<TypeAugmentedSemanticNode> {
    
   
    private TypeAugmentedSemanticNode head;
    
    public TypeAugmentedSemanticTree(AbstractSyntaxNode headAST, TypeEnvironment environment) {
        this.head = convertAST(headAST, environment);
    }
    
    protected static TypeAugmentedSemanticNode convertAST(AbstractSyntaxNode head, TypeEnvironment e) {
        List<TypeAugmentedSemanticNode> children = new LinkedList<>();
        for (AbstractSyntaxNode abstractSyntaxNode : head.getChildList()) {
            children.add(convertAST(abstractSyntaxNode, e));
        }
        if(head instanceof TypeAbstractSyntaxNode) {
            TypeAbstractSyntaxNode typeAbstractSyntaxNode = (TypeAbstractSyntaxNode) head;
            CXType type = typeAbstractSyntaxNode.getCxType();
            if(type instanceof CompoundTypeReference) {
                CXType newType = e.getNamedCompoundType(((CompoundTypeReference) type).getTypename());
                head = new TypeAbstractSyntaxNode(head.getType(), newType, head.getChildList());
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
