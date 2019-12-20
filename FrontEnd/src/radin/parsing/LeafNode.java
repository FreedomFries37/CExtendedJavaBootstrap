package radin.parsing;

public class LeafNode extends ParseNode {
    
    public LeafNode(String data) {
        super(data);
    }
    
    @Override
    public boolean hasChildren() {
        return false;
    }
}
