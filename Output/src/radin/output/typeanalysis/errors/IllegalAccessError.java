package radin.output.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.semantics.types.CXType;

import java.util.Arrays;

public class IllegalAccessError extends AbstractCompilationError {
    
    public IllegalAccessError(Token tok) {
        super(tok, "Illegal Access");
    }
    
    public IllegalAccessError(CXType type, String access, Token objectToken) {
        super("Can't access " + access + " for type " + type, objectToken, type.toString());
    }
    
    public IllegalAccessError(CXType type, String access, ParameterTypeList list, Token objectToken, Token accessToken) {
        super("Can't access " + access + " for type " + type + " on " + list, Arrays.asList(objectToken, accessToken),
                type.toString(), access + list.toString());
    }
}
