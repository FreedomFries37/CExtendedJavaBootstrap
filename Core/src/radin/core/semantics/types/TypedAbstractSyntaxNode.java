package radin.core.semantics.types;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;

import java.util.List;

public class TypedAbstractSyntaxNode extends AbstractSyntaxNode {

    public class NullCXTypeError extends AbstractCompilationError {
        public NullCXTypeError(Token t) {
            super(t, "No type found for this");
        }
    
        public NullCXTypeError() {
            super(null, "No type found for this");
        }
    }
    
    private CXType cxType;
    
    public TypedAbstractSyntaxNode(ASTNodeType type, CXType cxType, AbstractSyntaxNode... children) {
        super(type, children);
        if(cxType == null)
            throw new NullCXTypeError();
        this.cxType = cxType;
    }
    
    public TypedAbstractSyntaxNode(ASTNodeType type, CXType cxType, List<AbstractSyntaxNode> children) {
       this(type, cxType, children.toArray(new AbstractSyntaxNode[0]));
    }
    
    public TypedAbstractSyntaxNode(AbstractSyntaxNode other, CXType cxType, AbstractSyntaxNode add, AbstractSyntaxNode... additionalChildren) {
        super(other, add, additionalChildren);
        if(cxType == null)
            throw new NullCXTypeError();
        this.cxType = cxType;
    }
    
    public TypedAbstractSyntaxNode(AbstractSyntaxNode other, boolean addFirst, CXType cxType, AbstractSyntaxNode add, AbstractSyntaxNode... additionalChildren) {
        super(other, addFirst, add, additionalChildren);
        if(cxType == null)
            throw new NullCXTypeError();
        this.cxType = cxType;
    }
    
    public TypedAbstractSyntaxNode(AbstractSyntaxNode other, boolean addFirst, CXType cxType,
                                   List<AbstractSyntaxNode> additionalChildren) {
        super(other, addFirst, additionalChildren);
        if(cxType == null)
            throw new NullCXTypeError();
        this.cxType = cxType;
    }
    
    public TypedAbstractSyntaxNode(AbstractSyntaxNode other, CXType cxType,
                                   List<AbstractSyntaxNode> additionalChildren) {
        super(other, additionalChildren);
        if(cxType == null)
            throw new NullCXTypeError();
        this.cxType = cxType;
    }
    
    
    public TypedAbstractSyntaxNode(ASTNodeType type, Token token, CXType cxType) {
        super(type, token);
        if(cxType == null)
            throw new NullCXTypeError(token);
        this.cxType = cxType;
    }
    
    public CXType getCxType() {
        return cxType;
    }
    
    @Override
    public String getRepresentation() {
        return super.getRepresentation() + " [" + getCxType() + "]";
    }
    
    @Override
    public String toString() {
        return super.toString() + " [" + getCxType().ASTableDeclaration() + "]";
    }
}
