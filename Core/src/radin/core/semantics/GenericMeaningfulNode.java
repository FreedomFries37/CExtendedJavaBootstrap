package radin.core.semantics;

import java.util.LinkedList;
import java.util.List;

public class GenericMeaningfulNode <T extends Enum<T>, R extends AbstractGenericMeaningfulNode<T, R>> extends AbstractGenericMeaningfulNode<T, R> {
    private T type;
    private LinkedList<R> children;
    
    public GenericMeaningfulNode(T type) {
        this.type = type;
        children = new LinkedList<>();
    }
    
    public GenericMeaningfulNode(T type, List<R> children) {
        this(type);
        this.children.addAll(children);
    }
    
    @Override
    public T getTreeType() {
        return type;
    }
    
    @Override
    public List<R> getDirectChildren() {
        return children;
    }
    
    @Override
    public List<R> getMutableChildren() {
        return children;
    }
}
