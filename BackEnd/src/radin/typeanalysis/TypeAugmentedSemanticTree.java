package radin.typeanalysis;

import radin.compilation.tags.TypeDefHelperTag;
import radin.interphase.AbstractTree;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.CXCompoundTypeNameIndirection;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;

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
