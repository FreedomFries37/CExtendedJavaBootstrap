package radin.output.typeanalysis.errors;

public class RedeclarationError extends Error {
    
    public RedeclarationError(String name) {
        super("Can't redeclare " + name + " here");
    }
}
