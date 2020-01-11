package radin.core.semantics.types.methods;

import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.Visibility;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.primitives.PointerType;

import java.util.List;

public class CXConstructor extends CXMethod {
    
    private CXConstructor priorConstructor;
    private AbstractSyntaxNode correspondingASTNode;
    
    public CXConstructor(CXClassType parent, Visibility visibility, List<CXParameter> parameters,
                         AbstractSyntaxNode methodBody, AbstractSyntaxNode correspondingASTNode) {
        super(parent, visibility, createConstructorName(parent, parameters), false, new PointerType(parent), parameters,
                methodBody);
        this.correspondingASTNode = correspondingASTNode;
    }
    
    public CXConstructor getPriorConstructor() {
        return priorConstructor;
    }
    
    public void setPriorConstructor(CXConstructor priorConstructor) {
        this.priorConstructor = priorConstructor;
    }
    
    private static String createConstructorName(CXClassType parent, List<CXParameter> parameters) {
        
        
        return "construct_" + parent.getTypeNameIdentifier().generateCDefinitionNoHash() + Math.abs(parameters.hashCode()) + '_';
        
    }
    
    public AbstractSyntaxNode getCorrespondingASTNode() {
        return correspondingASTNode;
    }
    
    @Override
    public String toString() {
        return getCFunctionName() + "{" +
                "priorConstructor=" + priorConstructor +
                '}';
    }
}
