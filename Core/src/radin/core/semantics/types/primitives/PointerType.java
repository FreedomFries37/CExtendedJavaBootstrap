package radin.core.semantics.types.primitives;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.generics.CXParameterizedType;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.AbstractCXClassType;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.wrapped.CXMappedType;

public class PointerType extends ArrayType {
    
    
    
    public PointerType(CXType subType) {
        super(subType);
    }
    
    public CXType getSubType() {
        return getDereferenceType();
    }
    
    
    @Override
    public String generateCDefinition() {
        return getSubType().generateCDefinition() + "*";
    }
    
    @Override
    public String generateCDeclaration() {
        return getSubType().generateCDeclaration() + "*";
    }
    
    @Override
    public String ASTableDeclaration() {
        if(getSubType() instanceof CXClassType) return getSubType().ASTableDeclaration();
        return getSubType().ASTableDeclaration() + "*";
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
            return false;
            // return abstractCXPrimitiveType.isIntegral();
        }
        if(other instanceof CXMappedType) {
            ((CXMappedType) other).update();
            return is(((CXMappedType) other).getWrappedType(), e, strictPrimitiveEquality);
        }
        if(!(other instanceof ArrayType || other instanceof PointerType)) {
            return false;
        }
        if(this.getSubType() == CXPrimitiveType.VOID){
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
        
        if(subType instanceof PointerType && this.getSubType() instanceof PointerType) {
            return subType.isExact(this.getSubType(), e);
        }
        
        return e.is(this.getSubType(), subType);
    }
    
    @Override
    public String infoDump() {
        return "(" + getSubType().infoDump() + ")*";
    }
    
    @Override
    public boolean isIntegral() {
        return false;
    }
    
    @Override
    public String toString() {
        return getSubType().toString() + "*";
    }
    
    /**
     * Creates a modified version of the C Declaration that matches the pattern {@code \W+}
     *
     * @return Such a string
     */
    @Override
    public String getSafeTypeString() {
        return getSubType().getSafeTypeString() + "_p";
    }
    
    public CXType innerMostType() {
        if(getSubType() instanceof PointerType) {
            return ((PointerType) getSubType()).innerMostType();
        }
        return getSubType();
    }
    
    @Override
    public CXType getTypeRedirection(TypeEnvironment e) {
        return new PointerType(getSubType().getTypeRedirection(e));
    }
    
    @Override
    public CXType getCTypeIndirection() {
        return new PointerType(getSubType().getCTypeIndirection());
    }
    
    @Override
    public CXType propagateGenericReplacement(CXParameterizedType original, CXType replacement) {
        return new PointerType(getSubType().propagateGenericReplacement(original, replacement));
    }
    
    @Override
    public boolean isClassPointer() {
        return getSubType() instanceof AbstractCXClassType;
    }
    
    @Override
    public boolean isEventuallyClassPointer() {
        if (getSubType() instanceof PointerType) {
            return getSubType().isEventuallyClassPointer();
        } else {
            return isClassPointer();
        }
    }
}
