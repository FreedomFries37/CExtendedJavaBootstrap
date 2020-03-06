package radin.core.semantics;

import radin.core.AbstractTree;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractGenericMeaningfulNode <T extends Enum<T>, R extends AbstractGenericMeaningfulNode<T, R>> extends AbstractTree<R> {
    
    abstract public T getTreeType();
    
    public R getChildWithType(T type) {
        return getChildWithType(type, 0);
    }
    
    public R getChildWithType(T type, int count) {
        int found = -1;
        for (R directChild : getImmutableChildren()) {
            if(directChild.getTreeType().equals(type)) {
                if(++found == count) {
                    return directChild;
                }
            }
        }
        return null;
    }
    
    public R getChildAtIndex(int index) {
        return getDirectChildren().get(index);
    }
    
    @Override
    public List<R> postfix() {
        List<R> output = new LinkedList<>();
        output.add((R) this);
        for (R directChild : getImmutableChildren()) {
            output.addAll(directChild.postfix());
        }
    
        return output;
    }
}
