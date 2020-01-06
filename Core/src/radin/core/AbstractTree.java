package radin.core;

import java.util.List;

public abstract class AbstractTree<T extends AbstractTree<T>> {
    
    final public String toTreeForm() {
        return toTreeForm(0);
    }
    
    protected String toTreeForm(int indent) {
        StringBuilder output = new StringBuilder();
        output.append(indentString(indent));
        for (AbstractTree<T> directChild : getDirectChildren()) {
            output.append("\n");
            output.append(directChild.toTreeForm(indent + 1));
        }
        
        return output.toString();
    }
    
    final protected String indentString(int indent) {
        return getIndent(indent) + toString();
    }
    
    protected String getIndent(int indent) {
        return "  ".repeat(indent);
    }
    
    final public void printTreeForm() {
        System.out.println(toTreeForm());
    }
    
    public abstract List<T> postfix();
    public abstract List<? extends AbstractTree<T>> getDirectChildren();
}
