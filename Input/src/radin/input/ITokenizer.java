package radin.input;

import radin.core.chaining.IToolChainHead;
import radin.core.errorhandling.ICompilationErrorCollector;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public interface ITokenizer<T> extends Iterator<T>, Iterable<T>, IToolChainHead<T> {
    
    
    int getTokenIndex();
    
    void setTokenIndex(int tokenIndex);
    
    String getInputString();
    
    int run();
    
    T getFirst();
    
    T getLast();
    
    T getPrevious();
    
    T getCurrent();
    
    T getNext();
    
    void reset();
    
    @Override
    default T invoke(Void input) {
        return invoke();
    }
    
    @Override
    default boolean hasNext() {
        return getNext() != null;
    }
    
    @Override
    default T next() {
        return getCurrent();
    }
    
    
    @Override
    default Iterator<T> iterator() {
        return this;
    }
    
    default List<T> getAll() {
        reset();
        List<T> output = new LinkedList<>();
        for (T t : this) {
            output.add(t);
        }
        return output;
    }
}
