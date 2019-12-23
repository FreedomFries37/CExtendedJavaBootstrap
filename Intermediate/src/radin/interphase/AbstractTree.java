package radin.interphase;

import java.util.List;

public abstract class AbstractTree<T extends AbstractTree<T>> {
    
    final public String toTreeForm() {
        return toTreeForm(0);
    }
    
    protected String toTreeForm(int indent) {
        return getIndent(indent) + toString();
    }
    
    protected String getIndent(int indent) {
        return "  ".repeat(indent);
    }
    
    final public void printTreeForm() {
        System.out.println(toTreeForm());
    }
    
    public abstract List<T> postfix();
}
