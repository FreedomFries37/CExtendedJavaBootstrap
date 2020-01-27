package radin.core.chaining;

import radin.core.errorhandling.ICompilationErrorCollector;

import javax.naming.OperationNotSupportedException;
import java.security.InvalidKeyException;
import java.security.KeyException;

/**
 * Reprents a chain of commands to transform some object to another object
 * @param <T> the input type
 * @param <R> the output type
 */
public interface IToolChain <T, R> extends ICompilationErrorCollector {
    
    R invoke(T input);
    
    default void clearErrors() {
        getErrors().clear();
    }
    
    default <V> void setVariable(String variable, V value) {
    }
    default <V> V getVariable(String variable) {
        return null;
    }
}
