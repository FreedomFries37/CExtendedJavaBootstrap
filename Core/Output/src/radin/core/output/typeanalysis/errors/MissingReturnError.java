package radin.core.output.typeanalysis.errors;

public class MissingReturnError extends Error {
    
    public MissingReturnError() {
        super("function does not return in every branch");
    }
}
