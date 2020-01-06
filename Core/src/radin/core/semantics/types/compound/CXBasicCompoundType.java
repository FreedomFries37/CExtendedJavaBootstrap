package radin.core.semantics.types.compound;

import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;

import java.util.List;

public abstract class CXBasicCompoundType extends CXCompoundType {
    
    public CXBasicCompoundType(List<FieldDeclaration> fields) {
        super(fields);
    }
    
    public CXBasicCompoundType(FieldDeclaration f1, FieldDeclaration... fields) {
        super(f1, fields);
    }
    
    public CXBasicCompoundType(String name, List<FieldDeclaration> fields) {
        super(new CXIdentifier(name, false), fields);
    }
    
    public CXBasicCompoundType(String name, FieldDeclaration f1, FieldDeclaration... fields) {
        super(new CXIdentifier(name, false), f1, fields);
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(!(other instanceof CXCompoundType)) return false;
        if(this.isAnonymous() || ((CXCompoundType) other).isAnonymous()) return false;
        
        return this.getTypeName().equals(((CXCompoundType) other).getTypeName());
    }
    
    abstract protected String getSpecifier();
    
    @Override
    public String generateCDefinition(String identifier) {
        
        StringBuilder builder = new StringBuilder();
        getBaseStruct(builder);
        builder.append(" ");
        builder.append(identifier);
        return builder.toString();
    }
    
    protected void getBaseStruct(StringBuilder builder) {
        builder.append(getSpecifier());
        if(!isAnonymous()) {
            builder.append(" ");
            builder.append(getTypeName());
        }
        builder.append(" {\n");
        for (FieldDeclaration field : getFields()) {
            builder.append("\t");
            builder.append(field.getType().generateCDefinition(field.getName()));
            builder.append(";\n");
        }
        builder.append("}");
    }
    
    @Override
    public String generateCDefinition() {
        StringBuilder builder = new StringBuilder();
        getBaseStruct(builder);
        return builder.toString();
    }
}
