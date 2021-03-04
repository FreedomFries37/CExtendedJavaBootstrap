package radin.output.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

public class IncorrectNumberOfArgumentsError extends AbstractCompilationError  {
    
    private static String errorMessage(int expected_args, int found_args) {
        String error;
        if (expected_args < found_args) {
            error = "Too many arguments in function call. Expected " + expected_args + ",  found " + found_args;
        } else {
            error = "Too few arguments in function call. Expected " + expected_args + ",  found " + found_args;
        }
        return error;
    }
    
    public IncorrectNumberOfArgumentsError(Token sequence, int expected_args, int found_args) {
        super(sequence, errorMessage(expected_args, found_args));
    }
}
