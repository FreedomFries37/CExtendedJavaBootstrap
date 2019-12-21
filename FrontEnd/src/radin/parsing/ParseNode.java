package radin.parsing;

import radin.interphase.semantics.AbstractSyntaxNode;

public abstract class ParseNode {

    private String data;
    private AbstractSyntaxNode synthesized;
    private AbstractSyntaxNode inherit;
    
    
    public ParseNode(String data) {
        this.data = data;
    }
    
    public String getData() {
        return data;
    }
    
    public AbstractSyntaxNode getSynthesized() {
        return synthesized;
    }
    
    public void setSynthesized(AbstractSyntaxNode synthesized) {
        this.synthesized = synthesized;
    }
    
    public AbstractSyntaxNode getInherit() {
        return inherit;
    }
    
    public void setInherit(AbstractSyntaxNode inherit) {
        this.inherit = inherit;
    }
    
    public abstract boolean hasChildren();
    
    public String toString() { return getData(); }
    
    public String toTreeForm() {
        return toTreeForm(0);
    }
    
    protected String toTreeForm(int indent) {
        return "  ".repeat(indent) + toString();
    }
    
    public void printTreeForm() {
        System.out.println(toTreeForm());
    }
    
}
