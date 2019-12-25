package radin.interphase.semantics.types.compound;

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
    public String generateCDefinition() {
        return "struct" + super.generateCDefinition();
        
    }
}
