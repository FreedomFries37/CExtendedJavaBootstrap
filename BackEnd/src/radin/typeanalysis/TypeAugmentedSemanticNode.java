package radin.typeanalysis;

import radin.interphase.AbstractTree;
import radin.interphase.lexical.Token;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.interphase.semantics.types.compound.CXClassType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TypeAugmentedSemanticNode extends AbstractTree<TypeAugmentedSemanticNode> {
    
    private AbstractSyntaxNode astNode;
   
    private TypeAugmentedSemanticNode parent;
    private List<TypeAugmentedSemanticNode> children;
    
    private boolean isTypedExpression = false;
    private CXType type;
    
    private boolean isLValue = false;
   
    
    public TypeAugmentedSemanticNode(AbstractSyntaxNode base) {
        this.astNode = base;
        parent = null;
        children = new LinkedList<>();
        
    }
    
    public boolean isLValue() {
        return isLValue;
    }
    
    public void setLValue(boolean LValue) {
        isLValue = LValue;
    }
    
    public TypeAugmentedSemanticNode(AbstractSyntaxNode astNode, TypeAugmentedSemanticNode parent, List<TypeAugmentedSemanticNode> children) {
        this.astNode = astNode;
        setParent(parent);
        addAllChildren(children);
    }
    
    public TypeAugmentedSemanticNode(AbstractSyntaxNode astNode, List<TypeAugmentedSemanticNode> children) {
        this.astNode = astNode;
        parent = null;
        this.children = new LinkedList<>();
        /*
        for (TypeAugmentedSemanticNode child : children) {
            this.children.add(child);
            child.parent = this;
        }
       
         */
        addAllChildren(children);
    }
    
    public TypeAugmentedSemanticNode(AbstractSyntaxNode astNode, TypeAugmentedSemanticNode parent) {
        this.astNode = astNode;
        setParent(parent);
        children = new LinkedList<>();
    }
    
    public TypeAugmentedSemanticNode getParent() {
        return parent;
    }
    
    public List<TypeAugmentedSemanticNode> getChildren() {
        return children;
    }
    
    public void setParent(TypeAugmentedSemanticNode parent) {
        if(this.parent != null) {
            parent.getChildren().remove(this);
        }
        this.parent = parent;
        parent.getChildren().add(this);
    }
    
    public void addChild(TypeAugmentedSemanticNode child) {
        child.setParent(this);
    }
    
    public void addAllChildren(List<TypeAugmentedSemanticNode> children) {
        children.forEach(this::addChild);
    }
    
    public boolean isTypedExpression() {
        return isTypedExpression;
    }
    
    public AbstractSyntaxNode getSyntaxNode() {
        return astNode;
    }
    
    public ASTNodeType getASTType() {
        return astNode.getType();
    }
    
    public CXType getCXType() {
        return type;
    }
    
    public Token getToken() {
        return astNode.getToken();
    }
    
    public TypeAugmentedSemanticNode getChild(int index) {
        return children.get(index);
    }
    
    public boolean hasASTChild(ASTNodeType type) {
        return astNode.hasChild(type);
    }
    
    public List<TypeAugmentedSemanticNode> getAllChildren(ASTNodeType type) {
        List<TypeAugmentedSemanticNode> output = new LinkedList<>();
        if(this.astNode.getType() == type) output.add(this);
        for (TypeAugmentedSemanticNode child : children) {
            output.addAll(child.getAllChildren(type));
        }
        return output;
    }
    
    public TypeAugmentedSemanticNode getASTChild(ASTNodeType type) {
        AbstractSyntaxNode child = astNode.getChild(type);
        for (TypeAugmentedSemanticNode typeAugmentedSemanticNode : children) {
            if(typeAugmentedSemanticNode.astNode == child) return typeAugmentedSemanticNode;
        }
        return null;
    }
    
    public TypeAugmentedSemanticNode getASTChild(ASTNodeType type, int count) {
        AbstractSyntaxNode child = astNode.getChild(type);
        int found = 0;
        for (TypeAugmentedSemanticNode typeAugmentedSemanticNode : children) {
            if(typeAugmentedSemanticNode.astNode == child) {
                if(++found == count)
                    return typeAugmentedSemanticNode;
            }
        }
        return null;
    }
    
    public AbstractSyntaxNode getASTNode() {
        return astNode;
    }
    
    public void setType(CXType type) {
        if(type == null) clearType();
        else {
            isTypedExpression = true;
            this.type = type;
        }
    }
    
    public void clearType() {
        isTypedExpression = false;
        type = null;
    }
    
    @Override
    public String toString() {
        if(!isTypedExpression()) return astNode.toString();
        if(isLValue()) return astNode.toString() + " -> " + type.toString() + " [L]";
        return astNode.toString() + " -> " + type.toString() + " [R]";
    }
    
    @Override
    public List<? extends AbstractTree<TypeAugmentedSemanticNode>> getDirectChildren() {
        return getChildren();
    }
    
    @Override
    public List<TypeAugmentedSemanticNode> postfix() {
        List<TypeAugmentedSemanticNode> output = new LinkedList<>();
        for (TypeAugmentedSemanticNode child : children) {
            output.addAll(child.postfix());
        }
        output.add(this);
        return output;
    }
}
