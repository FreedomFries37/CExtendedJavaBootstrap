package radin.core.semantics.types.wrapped;

import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;

import java.util.List;
import java.util.stream.Collectors;

public class CXDeferredGenericClassDefinition extends CXDeferredClassDefinition{
    
    private final List<CXType> parameters;
    
    public CXDeferredGenericClassDefinition(Token corresponding, TypeEnvironment environment, CXIdentifier identifier, List<CXType> parameters) {
        super(corresponding, environment, identifier);
        this.parameters = parameters;
    }
    
    @Override
    public String toString() {
        return String.format("%s<%s>*", getIdentifier(), parameters.stream().map(CXType::toString).collect(Collectors.joining(",")));
    }
    
    @Override
    protected CXType getType() {
        return environment.getGenericType(getIdentifier(), parameters);
    }
}
