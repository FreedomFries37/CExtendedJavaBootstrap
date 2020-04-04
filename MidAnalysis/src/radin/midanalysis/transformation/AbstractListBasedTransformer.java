package radin.midanalysis.transformation;

import radin.core.chaining.ICompilerFunction;
import radin.core.errorhandling.AbstractCompilationError;
import radin.midanalysis.GenericModule;
import radin.midanalysis.ScopedTypeTracker;
import radin.midanalysis.typeanalysis.VariableTypeTracker;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public abstract class AbstractListBasedTransformer <T> extends ScopedTypeTracker implements
        ITransformer<T, List<T>>,
        ICompilerFunction<T, T> {
    
    private Stack<T> headStack = new Stack<>();
    
    public AbstractListBasedTransformer(Stack<VariableTypeTracker> trackerStack, GenericModule module) {
        super(trackerStack, module);
    }
    
    public AbstractListBasedTransformer(ScopedTypeTracker old) {
        super(old);
    }
    
    public AbstractListBasedTransformer() {
    }
    
    protected final T getHead() {
        return headStack.peek();
    }
    
    /**
     * Changes the scope somehow to be relevant to the target, and returns the old scope target
     *
     * @param target the new target for the scope change. How this changes the relevant layer is implementation
     *               specific
     * @return the old target
     */
    @Override
    public T reScopeTo(T target) {
        T oldValue = getHead();
        headStack.push(target);
        return oldValue;
    }
    
    
    @Override
    public T unScopeTo(T to) {
        T pop = headStack.pop();
        if(!getHead().equals(to)) {
            headStack = new Stack<>();
            headStack.push(to);
        }
        return pop;
    }
    
    @Override
    public T unScopeTo() {
        return headStack.pop();
    }
    
    @Override
    public final T invoke(T input) {
        T old = reScopeTo(input);
        T output = transform();
        unScopeTo(old);
        return output;
    }
    
    protected abstract T transform();
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return new LinkedList<>();
    }
    
    
    @Override
    public boolean insertFirst(T node) {
        getRelevant().add(0, node);
        return true;
    }
    
    @Override
    public boolean insertFirst(List<? extends T> nodes) {
        return getRelevant().addAll(0, nodes);
    }
    
    @Override
    public boolean insertLast(T node) {
        getRelevant().add(node);
        return true;
    }
    
    @Override
    public boolean insertLast(List<? extends T> nodes) {
        return getRelevant().addAll(nodes);
    }
    
    @Override
    public boolean insertAfter(T target, T node) {
        if(target == null) return insertLast(node);
        int index = indexOf(target);
        if(index == -1) throw new IndexOutOfBoundsException(index);
        getRelevant().add(index, node);
        return false;
    }
    
    @Override
    public boolean insertAfter(T target, List<? extends T> nodes) {
        if(target == null) return insertLast(nodes);
        int index = indexOf(target);
        if(index == -1) throw new IndexOutOfBoundsException(index);
        getRelevant().addAll(index + 1, nodes);
        return false;
    }
    
    @Override
    public boolean insertBefore(T target, T node) {
        if(target == null) return insertFirst(node);
        int index = indexOf(target);
        if(index == -1) throw new IndexOutOfBoundsException(index);
        getRelevant().add(index, node);
        return false;
    }
    
    @Override
    public boolean insertBefore(T target, List<? extends T> nodes) {
        if(target == null) return insertLast(nodes);
        int index = indexOf(target);
        if(index == -1) throw new IndexOutOfBoundsException(index);
        getRelevant().addAll(index, nodes);
        return false;
    }
    
    @Override
    public boolean delete(T node) {
        return getRelevant().remove(node);
    }
    
    @Override
    public abstract T next(T target);
    
    @Override
    public abstract T previous(T target);
    
    protected int indexOf(T node) {
        return getRelevant().indexOf(node);
    }
    
    
}
