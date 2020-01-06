package radin.core.frontend;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.errorhandling.ICompilationErrorCollector;
import radin.core.lexical.Token;

import java.util.Iterator;
import java.util.List;

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
