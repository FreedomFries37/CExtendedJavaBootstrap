package radin.interphase.semantics.types.wrapped;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.ICXWrapper;
import radin.interphase.semantics.types.primitives.AbstractCXPrimitiveType;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;

public class PointerType extends AbstractCXPrimitiveType implements ICXWrapper {
    
    private CXType subType;
    
    public PointerType(CXType subType) {
        this.subType = subType;
    }
    
    public CXType getSubType() {
        return subType;
    }
    
    @Override
    public String generateCDefinition() {
        return subType.getTypeIndirection().generateCDefinition() + "*";
    }
    
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return true;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return e.getPointerSize();
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(other instanceof AbstractCXPrimitiveType && !(other instanceof PointerType || other instanceof ArrayType)) {
            AbstractCXPrimitiveType abstractCXPrimitiveType = (AbstractCXPrimitiveType) other;
            return abstractCXPrimitiveType.isIntegral();
        }
        if(!(other instanceof ArrayType || other instanceof PointerType)) {
            return false;
        }
        if(this.subType == CXPrimitiveType.VOID){
                return true;
        }
       
        CXType subType;
        if(other instanceof ArrayType) {
            subType = ((ArrayType) other).getBaseType();
        }else {
            subType = ((PointerType) other).getSubType();
        }
        if(subType == CXPrimitiveType.VOID){
            return true;
        }
        
        return e.is(this.subType, subType);
    }
    
    @Override
    public CXType getWrappedType() {
        return getSubType();
    }
    
    @Override
    public boolean isIntegral() {
        return false;
    }
    
    @Override
    public String toString() {
        return subType.toString() + "*";
    }
    
    @Override
    public CXType getTypeRedirection(TypeEnvironment e) {
        return new PointerType(subType.getTypeRedirection(e));
    }
    
    @Override
    public CXType getCTypeIndirection() {
        return new PointerType(subType.getCTypeIndirection());
    }
}
