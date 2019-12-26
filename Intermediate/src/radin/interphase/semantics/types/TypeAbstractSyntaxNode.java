package radin.interphase.semantics.types;

import radin.interphase.lexical.Token;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;

import java.util.List;

public class TypeAbstractSyntaxNode extends AbstractSyntaxNode {

    private CXType cxType;
    
    public TypeAbstractSyntaxNode(ASTNodeType type, CXType cxType, AbstractSyntaxNode... children) {
        super(type, children);
        this.cxType = cxType;
    }
    
    public TypeAbstractSyntaxNode(ASTNodeType type, CXType cxType, List<AbstractSyntaxNode> children) {
       this(type, cxType, children.toArray(new AbstractSyntaxNode[children.size()]));
    }
    
    public TypeAbstractSyntaxNode(AbstractSyntaxNode other, AbstractSyntaxNode add, CXType cxType, AbstractSyntaxNode... additionalChildren) {
        super(other, add, additionalChildren);
        this.cxType = cxType;
    }
    
    public TypeAbstractSyntaxNode(AbstractSyntaxNode other, boolean addFirst, AbstractSyntaxNode add, CXType cxType, AbstractSyntaxNode... additionalChildren) {
        super(other, addFirst, add, additionalChildren);
        this.cxType = cxType;
    }
    
    public TypeAbstractSyntaxNode(ASTNodeType type, Token token, CXType cxType) {
        super(type, token);
        this.cxType = cxType;
    }
    
    public CXType getCxType() {
        return cxType;
    }
    
    @Override
    public String getRepresentation() {
        return super.getRepresentation() + " [" + cxType + "]";
    }
    
    @Override
    public String toString() {
        return super.toString() + " [" + getCxType() + "]";
    }
}
