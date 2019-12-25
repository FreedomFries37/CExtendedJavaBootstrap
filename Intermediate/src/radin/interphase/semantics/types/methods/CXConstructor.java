package radin.interphase.semantics.types.methods;

import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.Visibility;
import radin.interphase.semantics.types.compound.CXClassType;

import java.util.List;
import java.util.stream.Collectors;

public class CXConstructor extends CXMethod {
    
    private CXConstructor priorConstructor;
    
    public CXConstructor(CXClassType parent, Visibility visibility, CXConstructor prior, List<CXParameter> parameters,
                         AbstractSyntaxNode methodBody) {
        super(parent, visibility, createConstructorName(parent, parameters), false, parent, parameters, methodBody);
        this.priorConstructor = prior;
    }
    
    private static String createConstructorName(CXClassType parent, List<CXParameter> parameters) {
        
        
        return "initialize_" + parent.getCTypeName() + "_" + getParameterMangle(parameters);
        
    }
    
    
    
}
