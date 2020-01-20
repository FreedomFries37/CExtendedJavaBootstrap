package radin.core.semantics.types.wrapped;

import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.ICXWrapper;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.primitives.PointerType;

import java.util.LinkedList;
import java.util.Objects;

public class CXDeferredClassDefinition extends CXMappedType {
    
    private CXClassType temp;
    private CXIdentifier identifier;
    
    public CXDeferredClassDefinition(Token corresponding, TypeEnvironment environment, CXIdentifier identifier) {
        super(corresponding, environment);
        this.identifier = identifier;
        temp = new CXClassType(identifier, new LinkedList<CXClassType.ClassFieldDeclaration>(),
                new LinkedList<CXMethod>(), new LinkedList<CXConstructor>(), null);
    }
    
    @Override
    protected CXType getType() {
        return environment.getType(identifier, corresponding);
    }
    
    public CXIdentifier getIdentifier() {
        return identifier;
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        update();
        if(actual != null) return actual.generateCDeclaration(identifier);
        return temp.generateCDeclaration(identifier);
    }
    
    @Override
    public String generateCDeclaration() {
        update();
        if(actual != null) return actual.generateCDeclaration();
        return temp.generateCDeclaration();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CXDeferredClassDefinition that = (CXDeferredClassDefinition) o;
        return identifier.equals(that.identifier);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        update();
        if(actual != null) return actual.isValid(e);
        return false;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        
        if(other instanceof ICXWrapper) {
            return is(((ICXWrapper) other).getWrappedType(), e, strictPrimitiveEquality);
        }
        
        if(other instanceof PointerType && ((PointerType) other).getSubType() instanceof CXClassType) {
            CXClassType subType = (CXClassType) ((PointerType) other).getSubType();
            return temp.getTypeNameIdentifier().equals(subType.getTypeNameIdentifier());
        }
        
        update();
        if(actual != null) {
            if(strictPrimitiveEquality) {
                return e.isStrict(actual, other);
            } else {
                return e.is(actual, other);
            }
        }
        return equals(other);
    }
    
    @Override
    public String toString() {
        return getWrappedType().toString();
    }
    
    @Override
    public CXType getTypeRedirection(TypeEnvironment e) {
        update();
        if(actual == null) return super.getTypeRedirection(e);
        return getWrappedType().getTypeRedirection(e);
    }
    
    @Override
    public CXType getWrappedType() {
        if(actual == null) return temp;
        return actual;
    }
    
    @Override
    public String generateCDefinition() {
        if(actual != null) return actual.generateCDefinition();
        throw new UnsupportedOperationException();
    }
}
