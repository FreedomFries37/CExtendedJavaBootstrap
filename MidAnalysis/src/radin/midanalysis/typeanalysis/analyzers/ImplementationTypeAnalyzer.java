package radin.midanalysis.typeanalysis.analyzers;

import radin.midanalysis.MethodTASNTracker;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.output.tags.ImplementMethodTag;
import radin.output.typeanalysis.TypeAnalyzer;
import radin.output.typeanalysis.errors.MethodDoesNotExistError;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.utility.ICompilationSettings;

import java.util.LinkedList;
import java.util.List;

public class ImplementationTypeAnalyzer extends TypeAnalyzer {

    private CXClassType parentType;
    
    public ImplementationTypeAnalyzer(TypeAugmentedSemanticNode tree, CXClassType parentType) {
        super(tree);
        this.parentType = parentType;
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        boolean output = true;
        node.setType(parentType);
        typeTrackingClosureLoad(parentType);
        for (TypeAugmentedSemanticNode child : node.getChildren()) {
            
            if(child.getASTType() == ASTNodeType.function_definition) {
                FunctionTypeAnalyzer functionTypeAnalyzer = new FunctionTypeAnalyzer(child, parentType);
                if(!determineTypes(functionTypeAnalyzer)) {
                    output = false;
                    setIsFailurePoint(child);
                }
                List<CXType> parameterTypes = new LinkedList<>();
                for (AbstractSyntaxNode abstractSyntaxNode : child.getASTNode().getChild(ASTNodeType.parameter_list)) {
                    assert  abstractSyntaxNode instanceof TypedAbstractSyntaxNode;
                    CXType paramType = ((TypedAbstractSyntaxNode) abstractSyntaxNode).getCxType().getTypeRedirection(getEnvironment());
                    parameterTypes.add(paramType);
                }
    
                CXMethod corresponding = parentType.getMethodStrict(
                        child.getASTChild(ASTNodeType.id).getToken().getImage(),
                        new ParameterTypeList(parameterTypes),
                        null
                        );
                if(corresponding == null) {
                    ICompilationSettings.debugLog.warning("Corresponding function declaration doesn't exist: " +child.getASTChild(ASTNodeType.id).getToken().getImage() );
                    child.setFailurePoint(true);
                    throw new MethodDoesNotExistError(child.getASTChild(ASTNodeType.id).getToken());
                }
                MethodTASNTracker.getInstance().add(corresponding, child);
                ICompilationSettings.debugLog.info("Implementation found for " + corresponding);
                ICompilationSettings.debugLog.finest(child.toTreeForm());
                child.addCompilationTag(new ImplementMethodTag(corresponding));
               
            } else if(child.getASTType() == ASTNodeType.constructor_definition) {
                ConstructorTypeAnalyzer analyzer = new ConstructorTypeAnalyzer(child, parentType);
                if(!determineTypes(analyzer)) {
                    output = false;
                    setIsFailurePoint(child);
                }
                List<CXType> parameterTypes = new LinkedList<>();
                for (AbstractSyntaxNode abstractSyntaxNode : child.getASTNode().getChild(ASTNodeType.parameter_list)) {
                    assert  abstractSyntaxNode instanceof TypedAbstractSyntaxNode;
                    CXType paramType = ((TypedAbstractSyntaxNode) abstractSyntaxNode).getCxType().getTypeRedirection(getEnvironment());
                    parameterTypes.add(paramType);
                }
    
                CXConstructor corresponding = parentType.getConstructor(parameterTypes, environment);
                if(corresponding == null) {
                    ICompilationSettings.debugLog.warning("Corresponding method declaration doesn't exist: " +child.getASTChild(ASTNodeType.id).getToken().getImage() );
                    child.setFailurePoint(true);
                    throw new MethodDoesNotExistError(child.getASTChild(ASTNodeType.id).getToken());
                }
                MethodTASNTracker.getInstance().add(corresponding, child);
                ICompilationSettings.debugLog.info("Implementation found for " + corresponding);
                ICompilationSettings.debugLog.finest(child.toTreeForm());
                child.addCompilationTag(new ImplementMethodTag(corresponding));
            }
            
        }
        releaseTrackingClosure();
        return output;
    }
}
