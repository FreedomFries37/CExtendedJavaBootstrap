package radin.core.semantics.exceptions;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

public class TypeDoesNotExist extends Error {
    
    public TypeDoesNotExist(String type) {
        super(type + " does not exist");
    }
}
