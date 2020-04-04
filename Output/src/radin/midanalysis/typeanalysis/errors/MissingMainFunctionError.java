package radin.midanalysis.typeanalysis.errors;

public class MissingMainFunctionError extends Error {
    
    public MissingMainFunctionError() {
        super("No main function defined!");
    }
}
