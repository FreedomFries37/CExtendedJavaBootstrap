package radin.output.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class MultipleMainDefinitionsError extends AbstractCompilationError {
    
    public static Token firstDefinition;
    
    public MultipleMainDefinitionsError(Token newDefinition) {
        super("main(...) defined more than once", Arrays.asList(firstDefinition, newDefinition), "First defined here"
                , "but attempting to redefine");
    }
}
