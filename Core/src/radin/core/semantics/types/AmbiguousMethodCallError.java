package radin.core.semantics.types;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.methods.CXMethod;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AmbiguousMethodCallError extends AbstractCompilationError {
    public AmbiguousMethodCallError(Token name, List<CXMethod>methods, List<Token> parameters) {
        super("Ambiguous Identifier",
                join(name, parameters),
               "Could be " +
                methods.stream().map(CXMethod::toString).collect(Collectors.joining(" or ")));
    }
    
    private static List<Token> join(Token name, List<Token> identifiers) {
        return Stream.concat(Collections.singletonList(name).stream(), identifiers.stream()).collect(Collectors.toList());
    }
}
