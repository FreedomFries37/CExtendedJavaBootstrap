package radin.output.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

public class IllegalLValueError extends AbstractCompilationError {
    
    public IllegalLValueError(Token node) {
        super(node, "Illegal L Value");
    }
}
