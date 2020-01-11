package radin.core.semantics.types.compound;

import radin.core.semantics.types.CXCompoundTypeNameIndirection;
import radin.core.semantics.types.CXType;

import java.util.List;

public class CXStructType extends CXBasicCompoundType {
    
    public CXStructType(List<FieldDeclaration> fields) {
        super(fields);
    }
    
    public CXStructType(FieldDeclaration f1, FieldDeclaration... fields) {
        super(f1, fields);
    }
    
    public CXStructType(String name, List<FieldDeclaration> fields) {
        super(name, fields);
    }
    
    public CXStructType(String name, FieldDeclaration f1, FieldDeclaration... fields) {
        super(name, f1, fields);
    }
    
    @Override
    protected String getSpecifier() {
        return "struct";
    }
    
    
    
    @Override
    public CXType getTypeIndirection() {
        return new CXCompoundTypeNameIndirection(CXCompoundTypeNameIndirection.CompoundType.struct, this);
    }
}
