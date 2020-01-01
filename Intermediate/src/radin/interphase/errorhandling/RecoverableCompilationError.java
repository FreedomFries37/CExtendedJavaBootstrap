package radin.interphase.errorhandling;

public class RecoverableCompilationError extends Error {
    
    public RecoverableCompilationError() {
    }
    
    public RecoverableCompilationError(String message) {
        super(message);
    }
}
