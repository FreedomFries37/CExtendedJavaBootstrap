package radin.interphase.semantics.types.compound;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.ArrayType;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.ICXCompoundType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class CXCompoundType extends ICXCompoundType {

    public static class FieldDeclaration {
        private CXType type;
        private String name;
    
        public FieldDeclaration(CXType type, String name) {
            this.type = type;
            this.name = name;
        }
    
        public CXType getType() {
            return type;
        }
    
        public String getName() {
            return name;
        }
    
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
        
            FieldDeclaration that = (FieldDeclaration) o;
        
            if (!type.equals(that.type)) return false;
            return name.equals(that.name);
        }
    
        @Override
        public String toString() {
            return getType().generateCDefinition(name);
        }
    
        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }
    
    private List<FieldDeclaration> fields;
    private String typeName;
    private boolean anonymous;
    
    public CXCompoundType(List<FieldDeclaration> fields) {
        this.fields = fields;
        anonymous = true;
    }
    
    public CXCompoundType(FieldDeclaration f1, FieldDeclaration... fields) {
        this.fields = Arrays.asList(fields);
        this.fields.add(0, f1);
        anonymous = true;
    }
    
    public CXCompoundType(String name, List<FieldDeclaration> fields) {
        this.fields = fields;
        this.typeName = name;
        anonymous = false;
    }
    
    public CXCompoundType(String name, FieldDeclaration f1, FieldDeclaration... fields) {
        this.fields = Arrays.asList(fields);
        this.fields.add(0, f1);
        this.typeName = name;
        anonymous = false;
    }
    
    
    
    
    
    public List<FieldDeclaration> getFields() {
        return fields;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public boolean isAnonymous() {
        return anonymous;
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        for (FieldDeclaration field : fields) {
            if(!field.getType().isValid(e)) return false;
        }
        CXType lastType = fields.get(fields.size() - 1).type;
        if (lastType instanceof ArrayType) {
            return ((ArrayType) lastType).isConstSize();
        }
        return true;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        long sum = 0;
        for (FieldDeclaration field : fields) {
            sum += field.getType().getDataSize(e);
        }
        return sum;
    }
    
    @Override
    public String toString() {
        return super.toString().replaceAll("\\s+", " ");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CXCompoundType that = (CXCompoundType) o;
        
        if (anonymous != that.anonymous) return false;
        return Objects.equals(typeName, that.typeName);
    }
    
    @Override
    public int hashCode() {
        int result = typeName != null ? typeName.hashCode() : 0;
        result = 31 * result + (anonymous ? 1 : 0);
        return result;
    }
}
