package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

public interface IInPlaceCompilerAnalyzer <T> extends ICompilationErrorCollector  {
    void setHead(T object);
    boolean invoke();
}
