package radin.core.chaining;

/**
 * An object that takes in an object of type T and outputs an object of type R
 * @param <T> input type
 * @param <R> output type
 */
public interface ICompilerFunction <T, R> extends IToolChain<T, R> {
    
    R invoke(T input);
}
