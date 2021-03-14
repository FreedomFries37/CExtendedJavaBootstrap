package radin.core.semantics.types.wrapped;

import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.ICXWrapper;
import radin.core.semantics.types.primitives.AbstractCXPrimitiveType;
import radin.core.semantics.types.primitives.ArrayType;
import radin.core.semantics.types.primitives.PointerType;

public class ConstantType extends CXType /*implements ICXWrapper*/ {
    
    private CXType subtype;
    
    public ConstantType(CXType subtype) {
        this.subtype = subtype;
    }
    
    @Override
    public String generateCDefinition() {
        return "const " + subtype.generateCDefinition();
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        return "const " + subtype.generateCDeclaration(identifier);
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return subtype.isValid(e);
    }
    
    @Override
    public boolean isPrimitive() {
        return subtype.isPrimitive();
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return subtype.getDataSize(e);
    }
    
    public CXType getSubtype() {
        return subtype;
    }
    
    /*
    @Override
    public CXType getWrappedType() {
        return getSubtype();
    }
    */

    @Override
    public CXIdentifier getIdentifier() {
        return CXIdentifier.from(toString());
    }

    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(other instanceof ConstantType) {
            if(strictPrimitiveEquality)
                e.isStrict(subtype, ((ConstantType) other).getSubtype());
                else
            return e.is(subtype, ((ConstantType) other).getSubtype());
            // return subtype.is(((ConstantType) other).getSubtype(), e);
        }
        if(this.subtype instanceof AbstractCXPrimitiveType && !(this.subtype instanceof PointerType || this.subtype instanceof ArrayType)) {
            if(strictPrimitiveEquality)
                e.isStrict(subtype, other);
            else
                return e.is(subtype, other);
        }
        return false;
    }
}
