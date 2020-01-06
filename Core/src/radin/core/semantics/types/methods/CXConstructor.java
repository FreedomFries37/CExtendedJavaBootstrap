package radin.core.semantics.types.methods;

import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.Visibility;
import radin.core.semantics.types.compound.CXClassType;

import java.util.List;

public class CXConstructor extends CXMethod {
    
    private CXConstructor priorConstructor;
    
    public CXConstructor(CXClassType parent, Visibility visibility, List<CXParameter> parameters,
                         AbstractSyntaxNode methodBody) {
        super(parent, visibility, createConstructorName(parent, parameters), false, parent, parameters, methodBody);
    }
    
    public CXConstructor getPriorConstructor() {
        return priorConstructor;
    }
    
    public void setPriorConstructor(CXConstructor priorConstructor) {
        this.priorConstructor = priorConstructor;
    }
    
    private static String createConstructorName(CXClassType parent, List<CXParameter> parameters) {
        
        
        return "initialize_" + parent.getCTypeName() + "_" + getParameterMangle(parameters);
        
    }
    
    
    
}
