package radin.interphase.semantics;

public class IdentifierNode extends AbstractSyntaxNode {
    
    public IdentifierNode(String id) {
        super(ASTNodeType.id, id);
    }
}
