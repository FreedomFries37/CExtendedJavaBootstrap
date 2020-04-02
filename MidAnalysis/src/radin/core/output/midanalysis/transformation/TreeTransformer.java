package radin.core.output.midanalysis.transformation;

import radin.core.AbstractTree;

import java.util.List;

public abstract class TreeTransformer<T extends AbstractTree<T>> extends AbstractListBasedTransformer<T> {
    
    @Override
    public List<T> getRelevant() {
        return getHead().getDirectChildren();
    }
    
    
    @Override
    public boolean encapsulate(T target, T child) {
        if(!replace(target, child)) return false;
        child.addChild(target);
        return true;
    }
    
    
    @Override
    public T next(T target) {
        int targetIndex = indexOf(target);
        if(targetIndex == getRelevant().size() - 1 || targetIndex == -1) return null;
        return getRelevant().get(targetIndex + 1);
    }
    
    @Override
    public T previous(T target) {
        int targetIndex = indexOf(target);
        if(targetIndex <= 0) return null;
        return getRelevant().get(targetIndex - 1);
    }
    
}
