package radin.core.semantics.types.primitives;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.generics.CXParameterizedType;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.CXClassType;

public class PointerType extends AbstractCXPrimitiveType {
    
    private CXType subType;
    
    public PointerType(CXType subType) {
        this.subType = subType;
    }
    
    public CXType getSubType() {
        return subType;
    }
    
    @Override
    public String generateCDefinition() {
        return subType.generateCDefinition() + "*";
    }
    
    @Override
    public String generateCDeclaration() {
        return subType.generateCDeclaration() + "*";
    }
    
    @Override
    public String ASTableDeclaration() {
        if(subType instanceof CXClassType) return subType.ASTableDeclaration();
        return subType.ASTableDeclaration() + "*";
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
    public String infoDump() {
        return "(" + subType.infoDump() + ")*";
    }
    
    @Override
    public boolean isIntegral() {
        return false;
    }
    
    @Override
    public String toString() {
        return subType.toString() + "*";
    }
    
    /**
     * Creates a modified version of the C Declaration that matches the pattern {@code \W+}
     *
     * @return Such a string
     */
    @Override
    public String getSafeTypeString() {
        return subType.getSafeTypeString() + "_p";
    }
    
    @Override
    public CXType getTypeRedirection(TypeEnvironment e) {
        return new PointerType(subType.getTypeRedirection(e));
    }
    
    @Override
    public CXType getCTypeIndirection() {
        return new PointerType(subType.getCTypeIndirection());
    }
    
    @Override
    public CXType propagateGenericReplacement(CXParameterizedType original, CXType replacement) {
        return new PointerType(subType.propagateGenericReplacement(original, replacement));
    }
}
