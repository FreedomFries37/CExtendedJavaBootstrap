package radin.parsing;

import radin.interphase.semantics.AbstractSyntaxNode;

import java.util.List;

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
}
