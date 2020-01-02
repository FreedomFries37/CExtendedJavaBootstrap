package radin.interphase.semantics.types.wrapped;

import radin.interphase.lexical.Token;
import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.ICXWrapper;

public class CXNamespacedType extends CXType implements ICXWrapper {

    private CXType actual;
    
    public CXNamespacedType(CXType actual) {
        this.actual = actual;
    }
    
    public CXType getActual() {
        return actual;
    }
    
    @Override
    public String generateCDefinition(String identifier) {
        return actual.generateCDefinition(identifier);
    }
    
    @Override
    public Token getTokenEquivalent() {
        return actual.getTokenEquivalent();
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return actual.isValid(e);
    }
    
    @Override
    public boolean isPrimitive() {
        return actual.isPrimitive();
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return actual.getDataSize(e);
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e) {
        return actual.is(other, e);
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        return actual.is(other, e, strictPrimitiveEquality);
    }
    
    @Override
    public String toString() {
        return actual.toString();
    }
    
    @Override
    public CXType getTypeIndirection() {
        return actual.getTypeIndirection();
    }
    
    @Override
    public CXType getTypeRedirection(TypeEnvironment e) {
        return actual.getTypeRedirection(e);
    }
    
    @Override
    public CXType getCTypeIndirection() {
        return actual.getCTypeIndirection();
    }
    
    @Override
    public String generateCDefinition() {
        return actual.generateCDefinition();
    }
    
    @Override
    public String generateCDeclaration() {
        return actual.generateCDeclaration();
    }
    
    @Override
    public CXType getWrappedType() {
        return actual;
    }
}
