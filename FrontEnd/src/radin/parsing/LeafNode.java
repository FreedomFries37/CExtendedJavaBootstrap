package radin.parsing;

import radin.lexing.Token;

public class LeafNode extends ParseNode {
    
    private Token token;
    
    public LeafNode(Token data) {
        super(data.toString());
    }
    
    public Token getToken() {
        return token;
    }
    
    @Override
    public boolean hasChildren() {
        return false;
    }
}
