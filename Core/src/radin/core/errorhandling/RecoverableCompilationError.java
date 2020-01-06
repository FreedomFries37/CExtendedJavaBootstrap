package radin.core.errorhandling;

public class RecoverableCompilationError extends Error {
    
    public RecoverableCompilationError() {
    }
    
    public RecoverableCompilationError(String message) {
        super(message);
    }
}
