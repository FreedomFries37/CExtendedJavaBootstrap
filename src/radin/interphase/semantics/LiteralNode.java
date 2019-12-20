package radin.interphase.semantics;

public class LiteralNode extends AbstractSyntaxNode {
    
    public LiteralNode(String image) {
        super(ASTNodeType.literal, image);
    }
}
