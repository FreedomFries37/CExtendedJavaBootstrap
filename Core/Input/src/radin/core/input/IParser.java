package radin.core.input;

import radin.core.errorhandling.ICompilationErrorCollector;
import radin.core.AbstractTree;

public interface IParser<T, P extends AbstractTree<? extends P>> extends ICompilationErrorCollector {
    void setTokenizer(ITokenizer<? extends T> t);
    P parse();
    
    ITokenizer<? extends T> getTokenizer();
    
    default T getCurrent() {
        return getTokenizer().getCurrent();
    }
    
    default T getNext() {
        return getTokenizer().getNext();
    }
    
    default T getFirst() {
        return getTokenizer().getFirst();
    }
    
    default T getLast() {
        return getTokenizer().getLast();
    }
    
    default T getPrevious() {
        return getTokenizer().getPrevious();
    }
    
    void reset();
}
