package radin.interphase.semantics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AbstractSyntaxNode implements Iterable<AbstractSyntaxNode>{
    
    private ASTNodeType type;
    private String image;
    private List<AbstractSyntaxNode> childList;
    
    public AbstractSyntaxNode(ASTNodeType type) {
        this.type = type;
        childList = new ArrayList<>();
    }
    
    public AbstractSyntaxNode(ASTNodeType type, String image) {
        this(type);
        this.image = image;
    }
    
    public List<AbstractSyntaxNode> getChildList() {
        return new ArrayList<>(childList);
    }
    
    
    
    public ASTNodeType getType() {
        return type;
    }
    
    @Override
    public Iterator<AbstractSyntaxNode> iterator() {
        return childList.iterator();
    }
}
