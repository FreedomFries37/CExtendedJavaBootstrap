package radin.typeanalysis.errors;

public class RecoverableError extends Error {
    
    public RecoverableError() {
    }
    
    public RecoverableError(String message) {
        super(message);
    }
}
