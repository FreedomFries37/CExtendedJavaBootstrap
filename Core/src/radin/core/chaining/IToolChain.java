package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

/**
 * Reprents a chain of commands to transform some object to another object
 * @param <T> the input type
 * @param <R> the output type
 */
public interface IToolChain <T, R> extends ICompilationErrorCollector {
    
    R invoke(T input);
    
    void clearErrors();
}
