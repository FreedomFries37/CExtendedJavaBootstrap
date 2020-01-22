package radin.core.output.midanalysis.typeanalysis.analyzers;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.primitives.PointerType;
import radin.core.semantics.types.TypeAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.core.output.typeanalysis.TypeAnalyzer;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.typeanalysis.errors.MissingReturnError;
import radin.core.utility.ICompilationSettings;

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
        ICompilationSettings.debugLog.finest("Compiling function " + node.getASTChild(ASTNodeType.id).getToken().getImage());
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
            ICompilationSettings.debugLog.finest("Adding " + type.generateCDeclaration(name) + " to parameters");
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
