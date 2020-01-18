package radin.core.semantics.types.wrapped;

import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;

public class CXDelayedTypeDefinition extends CXMappedType {
    
    private CXIdentifier identifier;
    
    public CXDelayedTypeDefinition(CXIdentifier identifier, Token corresponding, TypeEnvironment environment) {
        super(corresponding, environment);
        this.identifier = identifier;
    }
    
    public CXIdentifier getIdentifier() {
        return identifier;
    }
    
    @Override
    protected CXType getType() {
        return environment.getType(identifier, corresponding);
    }
    
    @Override
    public String toString() {
        if(getWrappedType() == null) return "[DELAYED] " + identifier;
        return getWrappedType().toString();
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        if(!update()) throw new BadDelayedTypeAccessError();
        return getWrappedType().generateCDeclaration(identifier);
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        if(!update()) throw new BadDelayedTypeAccessError();
        return getWrappedType().isValid(e);
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(!update()) throw new BadDelayedTypeAccessError();
        return getWrappedType().is(other, e, strictPrimitiveEquality);
    }
    
    @Override
    public String generateCDefinition() {
        if(!update()) throw new BadDelayedTypeAccessError();
        return getWrappedType().generateCDefinition();
    }
    
}
