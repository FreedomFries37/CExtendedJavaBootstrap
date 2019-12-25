package radin.interphase.semantics.types;

import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;
import radin.interphase.semantics.TypeEnvironment;

public interface CXType extends CXEquivalent{
    
    
    String generateCDefinition(String identifier);
    
    default Token getTokenEquivalent() {
        return new Token(TokenType.t_typename, generateCDefinition());
    }
    
    boolean isValid(TypeEnvironment e);
    
    boolean isPrimitive();
    
    long getDataSize(TypeEnvironment e);
}
