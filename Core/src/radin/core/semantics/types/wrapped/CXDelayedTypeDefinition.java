package radin.core.semantics.types.wrapped;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.ICXWrapper;

import java.util.List;

public class CXDelayedTypeDefinition extends CXType implements ICXWrapper {
    
    private CXIdentifier identifier;
    private TypeEnvironment environment;
    
    private CXType actual;
    
    public class BadDelayedTypeAccessError extends AbstractCompilationError {
    
        public BadDelayedTypeAccessError() {
            super(identifier.getCorresponding(), "This type never created");
        }
    }
    
    public CXDelayedTypeDefinition(CXIdentifier identifier, TypeEnvironment environment) {
        this.identifier = identifier;
        this.environment = environment;
    }
    
    public boolean update() {
        try {
            CXType type = environment.getType(identifier);
            if(type == this) return false;
            if(type == null) return false;
            actual = type;
            return true;
        }catch (TypeNotPresentException e) {
            return false;
        }
        
    }
    
    @Override
    public String toString() {
        if(actual == null) return "[DELAYED] " + identifier;
        return actual.toString();
    }
    
    @Override
    public String generateCDefinition(String identifier) {
        if(!update()) throw new BadDelayedTypeAccessError();
        return actual.generateCDefinition(identifier);
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        if(!update()) throw new BadDelayedTypeAccessError();
        return actual.isValid(e);
    }
    
    @Override
    public boolean isPrimitive() {
        if(!update()) throw new BadDelayedTypeAccessError();
        return actual.isPrimitive();
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        if(!update()) throw new BadDelayedTypeAccessError();
        return actual.getDataSize(e);
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(!update()) throw new BadDelayedTypeAccessError();
        return actual.is(other, e, strictPrimitiveEquality);
    }
    
    @Override
    public String generateCDefinition() {
        if(!update()) throw new BadDelayedTypeAccessError();
        return actual.generateCDefinition();
    }
    
    @Override
    public CXType getWrappedType() {
        return actual;
    }
}
