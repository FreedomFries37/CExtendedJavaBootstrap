package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

/**
 * Takes an object, performs some method on an object, then outputs whether the method was successful by either
 * outputting the input, or outputting null
 * @param <T> the type to be analyzed
 */
public interface IInPlaceCompilerAnalyzer <T> extends ICompilationErrorCollector  {
    
    /**
     * Sets the target of the analyzer.
     * @param object the input object
     */
    void setHead(T object);
    
    /**
     * Invokes the analyzer
     * @return if the invocation was successful
     */
    boolean invoke();
}
