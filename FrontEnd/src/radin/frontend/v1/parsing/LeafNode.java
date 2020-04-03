package radin.frontend.v1.parsing;

import radin.core.lexical.Token;

import java.util.LinkedList;
import java.util.List;

public class LeafNode extends ParseNode {
    
    private Token token;
    
    public LeafNode(Token data) {
        super(data.toString());
        this.token = data;
    }
    
    public Token getToken() {
        return token;
    }
    
    @Override
    public boolean hasChildren() {
        return false;
    }
    
    @Override
    public List<ParseNode> getDirectChildren() {
        return new LinkedList<>();
    }
    
    @Override
    public List<ParseNode> postfix() {
        LinkedList<ParseNode> parseNodes = new LinkedList<>();
        parseNodes.add(this);
        return parseNodes;
    }
    
    @Override
    public List<ParseNode> getMutableChildren() {
        return getDirectChildren();
    }
}
