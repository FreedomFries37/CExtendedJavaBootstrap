package radin.core.semantics.types.methods;

import radin.core.semantics.types.CXType;

public class CXParameter {
    private CXType type;
    private String name;

    public CXParameter(CXType type, String name) {
        this.type = type;
        this.name = name;
    }

    public CXType getType() {
        return type;
    }

    public void setType(CXType type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return type.generateCDefinition(name);
    }
    
    public String getName() {
        return name;
    }
}
