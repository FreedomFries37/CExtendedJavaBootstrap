package radin.core.semantics.types;

import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.primitives.PointerType;

public abstract class CXType implements CXEquivalent {
    
    
    abstract public String generateCDeclaration(String identifier);
    
    public Token getTokenEquivalent() {
        return new Token(TokenType.t_typename, generateCDefinition());
    }
    
    abstract public boolean isValid(TypeEnvironment e);
    
    abstract public boolean isPrimitive();
    
    abstract public long getDataSize(TypeEnvironment e);
    
    public boolean is(CXType other, TypeEnvironment e) {
        return is(other, e, false);
    }
    
    abstract public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality);
    
    @Override
    public String toString() {
        return generateCDefinition().replaceAll("\\s+", " ");
    }
    
    public CXType getTypeIndirection() {
        return this;
    }
    
    public CXType getTypeRedirection(TypeEnvironment e) {
        return this;
    }
    
    public CXType getCTypeIndirection() {
        return this;
    }
    
    final public PointerType toPointer() {
        return new PointerType(this);
    }
    
    public boolean isExact(CXType other, TypeEnvironment e) {
        boolean leftLTE = e.isStrict(this, other);
        boolean rightLTE = e.isStrict(other, this);
        return leftLTE && rightLTE;
    }
}
