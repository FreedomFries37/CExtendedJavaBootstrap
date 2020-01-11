package radin.core.input;

import radin.core.errorhandling.ICompilationErrorCollector;

import java.util.Iterator;

public interface ITokenizer<T> extends Iterator<T>, Iterable<T>, ICompilationErrorCollector {
    
    
    int getTokenIndex();
    
    void setTokenIndex(int tokenIndex);
    
    String getInputString();
    
    void run();
    
    T getFirst();
    
    T getLast();
    
    T getPrevious();
    
    T getCurrent();
    
    T getNext();
    
    void reset();
    
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
}
