package radin.parsing;

import radin.interphase.lexical.Token;

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
    public List<ParseNode> postfix() {
        LinkedList<ParseNode> parseNodes = new LinkedList<>();
        parseNodes.add(this);
        return parseNodes;
    }
}
