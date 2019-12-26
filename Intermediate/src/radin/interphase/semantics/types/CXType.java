package radin.interphase.semantics.types;

import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;
import radin.interphase.semantics.TypeEnvironment;

public abstract class CXType implements CXEquivalent {
    
    
    abstract public String generateCDefinition(String identifier);
    
    public Token getTokenEquivalent() {
        return new Token(TokenType.t_typename, generateCDefinition());
    }
    
    abstract public boolean isValid(TypeEnvironment e);
    
    abstract public boolean isPrimitive();
    
    abstract public long getDataSize(TypeEnvironment e);
    
    abstract public boolean is(CXType other, TypeEnvironment e);
    
    @Override
    public String toString() {
        return generateCDefinition().replaceAll("\\s+", " ");
    }
    
    public CXType getTypeIndirection() {
        return this;
    }
    
    public CXType getCTypeIndirection() {
        return this;
    }
}
