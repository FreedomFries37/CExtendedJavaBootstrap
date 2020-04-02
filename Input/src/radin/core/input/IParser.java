package radin.core.input;

import radin.core.chaining.IToolChain;
import radin.core.chaining.IToolChainHead;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.errorhandling.ICompilationErrorCollector;
import radin.core.AbstractTree;

import java.util.ArrayList;
import java.util.List;

public interface IParser<T, P extends AbstractTree<? extends P>> extends IToolChainHead<P> {
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
    
    class ClosureTokenizer<T> implements ITokenizer<T>{
        private List<T> tokens;
        private int index;
    
        public ClosureTokenizer(List<T> tokens) {
            this.tokens = tokens;
        }
    
        @Override
        public int getTokenIndex() {
            return index;
        }
    
        @Override
        public void setTokenIndex(int tokenIndex) {
            index = tokenIndex;
        }
    
        @Override
        public String getInputString() {
            return null;
        }
    
        @Override
        public int run() {
            return tokens.size();
        }
    
        @Override
        public T getFirst() {
            return tokens.get(0);
        }
    
        @Override
        public T getLast() {
            return tokens.get(tokens.size() - 1);
        }
    
        @Override
        public T getPrevious() {
            if(index == 0) return null;
            return tokens.get(index - 1);
        }
    
        @Override
        public T getCurrent() {
            return tokens.get(index);
        }
    
        @Override
        public T getNext() {
            return tokens.get(++index);
        }
    
        @Override
        public void reset() {
            index = 0;
        }
    
        @Override
        public T invoke() {
            return getNext();
        }
    
        @Override
        public List<AbstractCompilationError> getErrors() {
            return null;
        }
    }
    
    /**
     * Alters the parser so it only looks at the next n tokens, where the nth + 1 token is the the token given by the
     * parameter
     * <p>
     *     Must be restored to the previous tokenizer by using the {@link IParser#setTokenizer(ITokenizer)} function
     * </p>
     * @param exclusiveEnd the ending token
     * @return the old tokenizer
     */
    default ITokenizer<? extends T> createClosure(T exclusiveEnd) {
        ITokenizer<? extends T> output = getTokenizer();
        ArrayList<T> list = new ArrayList<>();
        while (getCurrent() != null && !getCurrent().equals(exclusiveEnd)) {
            list.add(getCurrent());
            invoke();
        }
        ClosureTokenizer<T> closureTokenizer = new ClosureTokenizer<>(list);
        setTokenizer(closureTokenizer);
        return output;
    }
    
    void reset();
}
