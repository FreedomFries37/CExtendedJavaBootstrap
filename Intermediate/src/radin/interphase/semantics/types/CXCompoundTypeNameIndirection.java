package radin.interphase.semantics.types;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.exceptions.TypeDoesNotExist;
import radin.interphase.semantics.types.compound.CXCompoundType;

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
    private String typename;
    
    public CXCompoundTypeNameIndirection(CompoundType compoundType, String typename) {
        this.compoundType = compoundType;
        this.typename = typename;
    }
    
    public CXCompoundTypeNameIndirection(CompoundType compoundType, CXCompoundType actual) {
        this.compoundType = compoundType;
        this.typename = actual.getTypeName();
    }
    
    @Override
    public String generateCDefinition() {
        return compoundType.cequiv + " " + typename;
    }
    
    @Override
    public String generateCDefinition(String identifier) {
        return compoundType.cequiv + " " + typename + " " + identifier;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(!(other instanceof ICXCompoundType || other instanceof CXCompoundTypeNameIndirection)) return false;
        CXCompoundType namedCompoundType = e.getNamedCompoundType(typename);
        if(other instanceof ICXCompoundType) {
            if(namedCompoundType == null){
                return false;
            }
            return namedCompoundType.is(other, e);
        } else {
            if(!e.namedCompoundTypeExists(((CXCompoundTypeNameIndirection) other).typename)) {
                if(this.compoundType == ((CXCompoundTypeNameIndirection) other).compoundType && this.typename == ((CXCompoundTypeNameIndirection) other).typename) {
                    return true;
                }
                throw new TypeDoesNotExist(((CXCompoundTypeNameIndirection) other).typename);
            }
            return namedCompoundType.is(e.getNamedCompoundType(((CXCompoundTypeNameIndirection) other).typename), e);
        }
    }
    
    public CompoundType getCompoundType() {
        return compoundType;
    }
    
    public String getTypename() {
        return typename;
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return e.namedCompoundTypeExists(typename);
    }
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return e.getNamedCompoundType(typename).getDataSize(e);
    }
    
    @Override
    public CXType getTypeRedirection(TypeEnvironment e) {
        return e.getNamedCompoundType(typename);
    }
    
    @Override
    public CXType getCTypeIndirection() {
        if(compoundType.equals(CompoundType._class)) return new CXCompoundTypeNameIndirection(CompoundType.struct,
            typename);
        return new CXCompoundTypeNameIndirection(compoundType, typename);
    }
}
