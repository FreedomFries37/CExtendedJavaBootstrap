package radin.core.semantics.types.compound;

import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;

import java.util.List;
import java.util.Optional;

public interface ICXCompoundType{
    
    default boolean isPrimitive() {
        return false;
    }
    
    List<FieldDeclaration> getFields();
    
    String getTypeName();
    
    String getCTypeName();
    
    CXIdentifier getTypeNameIdentifier();
    
    default Optional<CXType> getFieldType(String name) {
        return getFields().stream().filter((f) -> f.name.equals(name)).map((f) -> f.type).findAny();
    }
    
    class FieldDeclaration {
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
            return getType().generateCDeclaration(name);
        }
    
        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }
}
