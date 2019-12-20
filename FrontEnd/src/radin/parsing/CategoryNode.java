package radin.parsing;

import java.util.ArrayList;
import java.util.List;

public class CategoryNode extends ParseNode {
    
    private List<ParseNode> allChildren;
    private List<CategoryNode> categoryChildren;
    
    public CategoryNode(String data) {
        super(data);
        allChildren = new ArrayList<>();
        categoryChildren = new ArrayList<>();
    }
    
    public void addChild(LeafNode leaf) {
        allChildren.add(leaf);
    }
    
    public void addChild(CategoryNode categoryNode) {
        allChildren.add(categoryNode);
        categoryChildren.add(categoryNode);
    }
    
    public ParseNode getChild(int index) {
        return allChildren.get(index);
    }
    
    public CategoryNode getChildCategory(String category) {
        return getChildCategory(category, 1);
    }
    
    public CategoryNode getChildCategory(String category, int count) {
        int found = 0;
        for (CategoryNode categoryChild : categoryChildren) {
            if(categoryChild.getCategory().equals(category)) {
                if(++found == count) return categoryChild;
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
}
