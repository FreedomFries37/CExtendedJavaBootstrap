package radin.core.semantics.types;

import radin.core.lexical.Token;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;

import java.util.List;

public class TypedAbstractSyntaxNode extends AbstractSyntaxNode {

    private CXType cxType;
    
    public TypedAbstractSyntaxNode(ASTNodeType type, CXType cxType, AbstractSyntaxNode... children) {
        super(type, children);
        this.cxType = cxType;
    }
    
    public TypedAbstractSyntaxNode(ASTNodeType type, CXType cxType, List<AbstractSyntaxNode> children) {
       this(type, cxType, children.toArray(new AbstractSyntaxNode[children.size()]));
    }
    
    public TypedAbstractSyntaxNode(AbstractSyntaxNode other, CXType cxType, AbstractSyntaxNode add, AbstractSyntaxNode... additionalChildren) {
        super(other, add, additionalChildren);
        this.cxType = cxType;
    }
    
    public TypedAbstractSyntaxNode(AbstractSyntaxNode other, boolean addFirst, CXType cxType, AbstractSyntaxNode add, AbstractSyntaxNode... additionalChildren) {
        super(other, addFirst, add, additionalChildren);
        this.cxType = cxType;
    }
    
    public TypedAbstractSyntaxNode(ASTNodeType type, Token token, CXType cxType) {
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
