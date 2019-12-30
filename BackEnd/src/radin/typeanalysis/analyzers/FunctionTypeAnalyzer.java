package radin.typeanalysis.analyzers;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.PointerType;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.compound.CXStructType;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.errors.MissingReturnError;

public class FunctionTypeAnalyzer extends TypeAnalyzer {
    
    private boolean hasOwnerType;
    private CXType owner;
    
    public FunctionTypeAnalyzer(TypeAugmentedSemanticNode tree, CXType owner) {
        super(tree);
        this.owner = owner;
        hasOwnerType = true;
    }
    
    public FunctionTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
        hasOwnerType = false;
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        assert node.getASTType() == ASTNodeType.function_definition;
        assert node.getASTNode() instanceof TypeAbstractSyntaxNode;
        
        CXType returnType = ((TypeAbstractSyntaxNode) node.getASTNode()).getCxType();
        
        typeTrackingClosure();
        if(hasOwnerType) {
            getCurrentTracker().addVariable("this", new PointerType(owner));
            if(owner instanceof CXClassType) {
                CXClassType cxClassType = (CXClassType) owner;
                if(cxClassType.getParent() != null) {
                    
                    
                    getCurrentTracker().addVariable("super", new PointerType(owner));
                }
            }
        }
        TypeAugmentedSemanticNode parameters = node.getASTChild(ASTNodeType.parameter_list);
        for (TypeAugmentedSemanticNode parameter : parameters.getAllChildren(ASTNodeType.declaration)) {
            assert parameter.getASTNode() instanceof TypeAbstractSyntaxNode;
            CXType type = ((TypeAbstractSyntaxNode) parameter.getASTNode()).getCxType();
            String name = parameter.getASTChild(ASTNodeType.id).getToken().getImage();
            
            getCurrentTracker().addVariable(name, type);
        }
    
        TypeAugmentedSemanticNode compoundStatement = node.getASTChild(ASTNodeType.compound_statement);
        CompoundStatementTypeAnalyzer compoundStatementTypeAnalyzer =
                new CompoundStatementTypeAnalyzer(compoundStatement, returnType, false);
        if(!determineTypes(compoundStatementTypeAnalyzer)) return false;
        
        
        if(returnType != CXPrimitiveType.VOID) {
            if(!compoundStatementTypeAnalyzer.isReturns()) throw new MissingReturnError();
        }
        
        
        releaseTrackingClosure();
        return true;
    }
}
