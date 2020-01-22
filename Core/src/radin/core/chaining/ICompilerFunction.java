package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

/**
 * An object that takes in an object of type T and outputs an object of type R
 * @param <T> input type
 * @param <R> output type
 */
public interface ICompilerFunction <T, R> extends ICompilationErrorCollector {
    
    R invoke(T input);
}
