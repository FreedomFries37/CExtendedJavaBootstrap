package radin.core.output.typeanalysis.errors;

public class RedeclarationError extends Error {
    
    public RedeclarationError(String name) {
        super("Can't redeclare " + name + " here");
    }
}
