package radin.parsing;

import radin.lexing.TokenType;

import java.util.ArrayList;
import java.util.List;

public class CategoryNode extends ParseNode {
    
    private List<ParseNode> allChildren;
    private List<LeafNode> leafChildren;
    private List<CategoryNode> categoryChildren;
    
    public CategoryNode(String data) {
        super(data);
        allChildren = new ArrayList<>();
        categoryChildren = new ArrayList<>();
    }
    
    public void addChild(LeafNode leaf) {
        allChildren.add(leaf);
        leafChildren.add(leaf);
    }
    
    public void addChild(CategoryNode categoryNode) {
        allChildren.add(categoryNode);
        categoryChildren.add(categoryNode);
    }
    
    public ParseNode getChild(int index) {
        return allChildren.get(index);
    }
    
    public CategoryNode getCategoryNode(String category) {
        return getCategoryNode(category, 1);
    }
    
    public CategoryNode getCategoryNode(String category, int count) {
        int found = 0;
        if(this.getData().equals(category)) {
            if(++found == count) return this;
        }
        for (CategoryNode categoryChild : categoryChildren) {
            if(categoryChild.getCategory().equals(category)) {
                if(++found == count) return categoryChild;
            }
        }
        return null;
    }
    
    public LeafNode getLeafNode(TokenType type) {
        return getLeafNode(type, 1);
    }
    
    public LeafNode getLeafNode(TokenType type, int count) {
        int found = 0;
        for (LeafNode leafChild : leafChildren) {
            if(leafChild.getToken().getType().equals(type)) {
                if(++found == count) return leafChild;
            }
        }
        
        return null;
    }
    
    
    
    
    public String getCategory() {
        return getData();
    }
    
    @Override
    public boolean hasChildren() {
        return allChildren.size() > 0;
    }
    
    @Override
    protected String toTreeForm(int indent) {
        StringBuilder output = new StringBuilder(super.toTreeForm(indent));
        for (ParseNode child : allChildren) {
            output.append("\n");
            output.append(child.toTreeForm(indent + 1));
        }
        return output.toString();
    }
}
