package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

interface IToolChain <T, R> extends ICompilationErrorCollector {
    
    R invoke(T input);
}
