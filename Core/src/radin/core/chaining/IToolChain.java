package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

public interface IToolChain <T, R> extends ICompilationErrorCollector {
    
    R invoke(T input);
}
