package radin.interphase.semantics.types.compound;

import java.util.List;

public abstract class CXBasicCompoundType extends CXCompoundType {
    
    public CXBasicCompoundType(List<FieldDeclaration> fields) {
        super(fields);
    }
    
    public CXBasicCompoundType(FieldDeclaration f1, FieldDeclaration... fields) {
        super(f1, fields);
    }
    
    public CXBasicCompoundType(String name, List<FieldDeclaration> fields) {
        super(name, fields);
    }
    
    public CXBasicCompoundType(String name, FieldDeclaration f1, FieldDeclaration... fields) {
        super(name, f1, fields);
    }
    
    @Override
    public String generateCDefinition(String identifier) {
        return generateCDefinition() + " " + identifier;
    }
    
    @Override
    public String generateCDefinition() {
        StringBuilder builder = new StringBuilder();
        if(!isAnonymous()) {
            builder.append(" ");
            builder.append(getTypeName());
        }
        builder.append('{');
        for (FieldDeclaration field : getFields()) {
            builder.append("\t");
            builder.append(field.getType().generateCDefinition(field.getName()));
            builder.append(";\n");
        }
        builder.append("};");
        return builder.toString();
    }
}
