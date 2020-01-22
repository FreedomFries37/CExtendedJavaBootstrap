package radin.core.semantics.types;

import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.exceptions.TypeDoesNotExist;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.ICXCompoundType;

public class CXCompoundTypeNameIndirection extends CXType {
    
    public enum CompoundType {
        struct("struct"),
        union("union"),
        _class("struct")
        ;
        String cequiv;
        
        CompoundType(String cequiv) {
            this.cequiv = cequiv;
        }
    }
    private CompoundType compoundType;
    private CXIdentifier typename;
    
    public CXCompoundTypeNameIndirection(CompoundType compoundType, Token typename) {
        this.compoundType = compoundType;
        this.typename = new CXIdentifier(typename, false);
    }
    
    public CXCompoundTypeNameIndirection(CompoundType compoundType, CXIdentifier typename) {
        this.compoundType = compoundType;
        this.typename = typename;
    }
    
    public CXCompoundTypeNameIndirection(CompoundType compoundType, CXCompoundType actual) {
        this.compoundType = compoundType;
        this.typename = actual.getTypeNameIdentifier();
    }
    
    @Override
    public String generateCDefinition() {
        if(compoundType == CompoundType._class) return getCTypeIndirection().generateCDefinition();
        return compoundType.cequiv + " " + typename;
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        return generateCDefinition() + " " + identifier;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(!(other instanceof ICXCompoundType || other instanceof CXCompoundTypeNameIndirection)) return false;
        CXCompoundType namedCompoundType = e.getNamedCompoundType(typename.getIdentifierString());
        if(other instanceof ICXCompoundType) {
            if(namedCompoundType == null){
                return false;
            }
            return e.is(namedCompoundType, other);
        } else {
            if(!e.namedCompoundTypeExists(((CXCompoundTypeNameIndirection) other).typename.getIdentifierString())) {
                if(this.compoundType == ((CXCompoundTypeNameIndirection) other).compoundType && this.typename.equals(((CXCompoundTypeNameIndirection) other).typename)) {
                    return true;
                }
                throw new TypeDoesNotExist(((CXCompoundTypeNameIndirection) other).typename.getIdentifierString());
            }
            return e.is(namedCompoundType,
                    e.getNamedCompoundType(((CXCompoundTypeNameIndirection) other).typename.getIdentifierString()));
            //return namedCompoundType.is(e.getNamedCompoundType(((CXCompoundTypeNameIndirection) other).typename), e);
        }
    }
    
    public CompoundType getCompoundType() {
        return compoundType;
    }
    
    public String getTypename() {
        return typename.toString();
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return e.namedCompoundTypeExists(typename.getIdentifierString());
    }
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return e.getNamedCompoundType(typename.getIdentifierString()).getDataSize(e);
    }
    
    @Override
    public CXType getTypeRedirection(TypeEnvironment e) {
        return e.getNamedCompoundType(typename.getIdentifierString());
    }
    
    @Override
    public CXType getCTypeIndirection() {
        if(compoundType.equals(CompoundType._class)) return new CXCompoundTypeNameIndirection(CompoundType.struct,
            new Token(TokenType.t_id, "class_" + typename.getIdentifierString()));
        return new CXCompoundTypeNameIndirection(compoundType, typename);
    }
}
