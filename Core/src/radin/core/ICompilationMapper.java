package radin.core;

import radin.core.errorhandling.ICompilationErrorCollector;

public interface ICompilationMapper<T, R> extends ICompilationErrorCollector {
    R transform(T input);
}
