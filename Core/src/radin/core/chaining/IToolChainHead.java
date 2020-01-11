package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

public interface IToolChainHead<T> extends IToolChain<Void, T> {
    T invoke();
}
