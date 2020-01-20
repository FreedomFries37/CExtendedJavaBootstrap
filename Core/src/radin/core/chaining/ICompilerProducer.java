package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

public interface ICompilerProducer<T> extends ICompilationErrorCollector {
    T invoke();
    
    
}
