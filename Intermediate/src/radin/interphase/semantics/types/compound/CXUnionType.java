package radin.interphase.semantics.types.compound;

import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.CXCompoundTypeNameIndirection;

import java.util.List;

public class CXUnionType extends CXBasicCompoundType {
    
    public CXUnionType(List<FieldDeclaration> fields) {
        super(fields);
    }
    
    public CXUnionType(FieldDeclaration f1, FieldDeclaration... fields) {
        super(f1, fields);
    }
    
    public CXUnionType(String name, List<FieldDeclaration> fields) {
        super(name, fields);
    }
    
    public CXUnionType(String name, FieldDeclaration f1, FieldDeclaration... fields) {
        super(name, f1, fields);
    }
    
    @Override
    protected String getSpecifier() {
        return "union";
    }
    
    @Override
    public CXType getTypeIndirection() {
        return new CXCompoundTypeNameIndirection(CXCompoundTypeNameIndirection.CompoundType.union, this);
    }
}
