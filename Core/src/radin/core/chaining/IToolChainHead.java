package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

/**
 * Represents a part of the toolchain that generates an object without any input from another part of the chain.
 * Must be the first part in a chain
 * @param <T> the output type
 */
public interface IToolChainHead<T> extends IToolChain<Void, T> {
    T invoke();
    
    @Override
    default T invoke(Void input) {
        return invoke();
    }
    
    /**
     * Because output doesn't depend on input, then must be able to reset if necessary
     */
    void reset();
}
