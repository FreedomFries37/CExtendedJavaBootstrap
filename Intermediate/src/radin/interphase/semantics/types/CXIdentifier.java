package radin.interphase.semantics.types;

import java.util.Objects;

public class CXIdentifier implements CXEquivalent {
    private CXIdentifier parentNamespace;
    private String identifier;
    private boolean useHashcode;
    
    public CXIdentifier(CXIdentifier parentNamespace, String identifier) {
        this.parentNamespace = parentNamespace;
        this.identifier = identifier;
        useHashcode = true;
    }
    
    public CXIdentifier(String identifier, boolean useHashcode) {
        this.identifier = identifier;
        this.useHashcode = false;
    }
    
    public CXIdentifier getParentNamespace() {
        return parentNamespace;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CXIdentifier that = (CXIdentifier) o;
        
        if (!Objects.equals(parentNamespace, that.parentNamespace))
            return false;
        return identifier.equals(that.identifier);
    }
    
    @Override
    public int hashCode() {
        int result = parentNamespace != null ? parentNamespace.hashCode() : 0;
        result = 31 * result + identifier.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        if(parentNamespace == null) return identifier;
        return parentNamespace.toString() + "." + identifier;
    }
    
    @Override
    public String generateCDefinition() {
        return toString().replace(".", "_") + hashCode();
    }
    
}
