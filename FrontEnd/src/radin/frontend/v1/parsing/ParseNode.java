package radin.frontend.v1.parsing;

import radin.frontend.v1.semantics.SynthesizedMissingException;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.AbstractTree;
import radin.frontend.v1.InheritMissingError;

import java.util.LinkedList;
import java.util.List;

public abstract class ParseNode extends AbstractTree<ParseNode> {

    private String data;
    private AbstractSyntaxNode synthesized;
    private List<AbstractSyntaxNode> inherit;
    private AbstractSyntaxNode compilationTagList;
    
    
    public ParseNode(String data) {
        this.data = data;
        inherit = new LinkedList<>();
    }
    
    public String getData() {
        return data;
    }
    
    public AbstractSyntaxNode getSynthesized() throws SynthesizedMissingException {
        if(synthesized == null) throw new SynthesizedMissingException(this);
        return synthesized;
    }
    
    public void setSynthesized(AbstractSyntaxNode synthesized) {
        this.synthesized = synthesized;
    }
    
    public AbstractSyntaxNode getInherit() {
        if (inherit.isEmpty()) {
            throw new InheritMissingError(this);
        }
        return inherit.get(0);
    }

    public AbstractSyntaxNode getInheritOrEmpty() {
        if (inherit.isEmpty()) {
            return AbstractSyntaxNode.EMPTY;
        }
        return inherit.get(0);
    }

    public AbstractSyntaxNode getInherit(int index) {
        if(index >= inherit.size()) throw new InheritMissingError(this);
        return inherit.get(index);
    }
    
    public AbstractSyntaxNode getCompilationTagList() {
        return compilationTagList;
    }
    
    public void setCompilationTagList(AbstractSyntaxNode compilationTagList) {
        this.compilationTagList = compilationTagList;
    }
    
    public void setInherit(AbstractSyntaxNode inherit) {
        this.inherit.add(0, inherit);
    }
    public void setInherit(AbstractSyntaxNode inherit, int index) {
        if(index == this.inherit.size()) {
            this.inherit.add(inherit);
        } else {
            this.inherit.add(index, inherit);
        }
    }
    
    
    
    public abstract boolean hasChildren();
    
    public String toString() { return getData(); }
    
   
    protected String toTreeForm(int indent) {
        return "  ".repeat(indent) + toString();
    }
    
    public abstract List<ParseNode> postfix();
    
}
