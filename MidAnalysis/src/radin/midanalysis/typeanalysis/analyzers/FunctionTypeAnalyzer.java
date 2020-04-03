package radin.midanalysis.typeanalysis.analyzers;

import radin.midanalysis.ScopedTypeTracker;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.output.tags.BasicCompilationTag;
import radin.output.typeanalysis.errors.IncorrectMainDefinition;
import radin.output.typeanalysis.errors.MultipleMainDefinitionsError;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.primitives.ArrayType;
import radin.core.semantics.types.primitives.PointerType;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.output.typeanalysis.TypeAnalyzer;
import radin.output.typeanalysis.errors.MissingReturnError;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;

import java.util.LinkedList;
import java.util.List;

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
        assert node.getASTNode() instanceof TypedAbstractSyntaxNode;
        
        CXType returnType = ((TypedAbstractSyntaxNode) node.getASTNode()).getCxType();
        
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
        List<CXType> parameterTypes = new LinkedList<>();
        for (TypeAugmentedSemanticNode parameter : parameters.getAllChildren(ASTNodeType.declaration)) {
            assert parameter.getASTNode() instanceof TypedAbstractSyntaxNode;
            CXType type = ((TypedAbstractSyntaxNode) parameter.getASTNode()).getCxType();
            parameterTypes.add(type);
            String name = parameter.getASTChild(ASTNodeType.id).getToken().getImage();
            ICompilationSettings.debugLog.finest("Adding " + type.generateCDeclaration(name) + " to parameters");
            getCurrentTracker().addVariable(name, type);
        }
        
        String functionName = node.getASTChild(ASTNodeType.id).getToken().getImage();
    
        
        
        if(UniversalCompilerSettings.getInstance().getSettings().isLookForMainFunction() && functionName.equals("main") && !hasOwnerType) {
            if(!ScopedTypeTracker.environment.is(returnType, CXPrimitiveType.INTEGER) ||
                    parameterTypes.size() != 2 ||
                    !ScopedTypeTracker.environment.is(parameterTypes.get(0), CXPrimitiveType.INTEGER) ||
                    !(parameterTypes.get(1) instanceof ArrayType) ||
                    !(((ArrayType) parameterTypes.get(1)).getBaseType() instanceof PointerType) ||
                    !(((PointerType) ((ArrayType) parameterTypes.get(1)).getBaseType()).getSubType() instanceof CXClassType) ||
                    !((CXClassType) ((PointerType) ((ArrayType) parameterTypes.get(1)).getBaseType()).getSubType()).getTypeName().equals("std::String")) {
                throw new IncorrectMainDefinition(node.getASTChild(ASTNodeType.id).getToken());
            }
        
            if(MultipleMainDefinitionsError.firstDefinition != null) {
                throw new MultipleMainDefinitionsError(node.getASTChild(ASTNodeType.id).getToken());
            }
            MultipleMainDefinitionsError.firstDefinition = node.getASTChild(ASTNodeType.id).getToken();
            ICompilationSettings.debugLog.info("Main Function Found");
            node.addCompilationTag(BasicCompilationTag.MAIN_FUNCTION);
        }
        
        TypeAugmentedSemanticNode compoundStatement = node.getASTChild(ASTNodeType.compound_statement);
        CompoundStatementTypeAnalyzer compoundStatementTypeAnalyzer =
                new CompoundStatementTypeAnalyzer(compoundStatement, returnType, false);
        if(!determineTypes(compoundStatementTypeAnalyzer)) return false;
        
        
        if(returnType != CXPrimitiveType.VOID) {
            if(!compoundStatementTypeAnalyzer.isReturns()) {
                setIsFailurePoint(node);
                throw new MissingReturnError(node.getASTChild(ASTNodeType.id).findFirstToken().getPrevious(),
                        returnType.toString());
            }
        }
        
        
        releaseTrackingClosure();
        return true;
    }
}
