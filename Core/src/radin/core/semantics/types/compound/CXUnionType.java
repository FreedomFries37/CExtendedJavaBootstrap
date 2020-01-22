package radin.core.semantics.types.compound;

import radin.core.lexical.Token;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.CXCompoundTypeNameIndirection;

import java.util.List;

public class CXUnionType extends CXBasicCompoundType {
    
    public CXUnionType(List<FieldDeclaration> fields) {
        super(fields);
    }
    
    public CXUnionType(FieldDeclaration f1, FieldDeclaration... fields) {
        super(f1, fields);
    }
    
    public CXUnionType(Token name, List<FieldDeclaration> fields) {
        super(name, fields);
    }
    
    public CXUnionType(Token name, FieldDeclaration f1, FieldDeclaration... fields) {
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
