package radin.v1.parsing;

import radin.core.AbstractTree;
import radin.core.lexical.TokenType;
import radin.v1.MissingCategoryNodeError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CategoryNode extends ParseNode {
    
    private List<ParseNode> allChildren;
    private List<LeafNode> leafChildren;
    private List<CategoryNode> categoryChildren;
    
    public CategoryNode(String data) {
        super(data);
        allChildren = new ArrayList<>();
        leafChildren = new ArrayList<>();
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
        if(found > 0)
            throw new MissingCategoryNodeError(this, category, count);
        else throw new MissingCategoryNodeError(this, category);
    }
    
    public LeafNode getLeafNode(TokenType type) {
        return getLeafNode(type, 1);
    }
    
    public boolean firstIs(TokenType type) {
        if(categoryChildren.size() > 0 && allChildren.get(0).equals(categoryChildren.get(0))) return false;
        return leafChildren.get(0).getToken().getType().equals(type);
    }
    
    public boolean firstIs(TokenType type1, TokenType type2, TokenType... rest) {
        if(categoryChildren.size() > 0 && allChildren.get(0).equals(categoryChildren.get(0))) return false;
        List<TokenType> all = new ArrayList<>(2 + rest.length);
        all.add(type1);
        all.add(type2);
        all.addAll(Arrays.asList(rest));
        for (TokenType tokenType : all) {
            if(leafChildren.get(0).getToken().getType().equals(tokenType)) return true;
        }
        return false;
    }
    
    public boolean firstIs(String category) {
        if(leafChildren.size() > 0 && allChildren.get(0).equals(leafChildren.get(0))) return false;
        return categoryChildren.get(0).getCategory().equals(category);
    }
    
    public boolean hasChildCategory(String cat) {
        try {
            if(this.getCategory().equals(cat)) {
                getCategoryNode(cat, 2);
            } else getCategoryNode(cat);
            return true;
        } catch (MissingCategoryNodeError e){
            return false;
        }
    }
    
    public boolean hasChildToken(TokenType t) {
        return getLeafNode(t) != null;
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
    
    @Override
    public String toString() {
        return "<" + super.toString() + ">";
    }
    
    public List<ParseNode> getAllChildren() {
        return allChildren;
    }
    
    @Override
    public List<? extends AbstractTree<ParseNode>> getDirectChildren() {
        return getAllChildren();
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
    
    public List<ParseNode> postfix() {
       
        List<ParseNode> output = new LinkedList<>();
        for (ParseNode child : allChildren) {
            output.addAll(child.postfix());
        }
        output.add(this);
        return output;
    }
}
