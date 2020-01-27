package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

/**
 * An object that creates an object of type T
 * @param <T> the output type
 */
public interface ICompilerProducer<T> extends ICompilationErrorCollector, IToolChainHead<T> {
    T invoke();
    
    
}
