package radin.interphase.semantics.types.methods;

import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.types.Visibility;
import radin.interphase.semantics.types.compound.CXClassType;

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
