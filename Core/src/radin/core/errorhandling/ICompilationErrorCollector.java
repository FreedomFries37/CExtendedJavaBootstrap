package radin.core.errorhandling;

import java.util.List;

public interface ICompilationErrorCollector {
    
    List<AbstractCompilationError> getErrors();
    
    default boolean hasErrors() {
        return !getErrors().isEmpty();
    }
}
