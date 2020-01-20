package radin.core.output.midanalysis.typeanalysis.analyzers;

import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.tags.ImplementMethodTag;
import radin.core.output.typeanalysis.TypeAnalyzer;
import radin.core.output.typeanalysis.errors.MethodDoesNotExistError;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.AmbiguousMethodCallError;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypeAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
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
                    assert  abstractSyntaxNode instanceof TypeAbstractSyntaxNode;
                    CXType paramType = ((TypeAbstractSyntaxNode) abstractSyntaxNode).getCxType().getTypeRedirection(getEnvironment());
                    parameterTypes.add(paramType);
                }
    
                CXMethod corresponding = parentType.getMethod(
                        child.getASTChild(ASTNodeType.id).getToken(),
                        new ParameterTypeList(parameterTypes),
                        null
                        );
                if(corresponding == null) {
                    throw new MethodDoesNotExistError(child.getASTChild(ASTNodeType.id).getToken());
                }
                ICompilationSettings.debugLog.info("Implementation found for " + corresponding);
                child.addCompilationTag(new ImplementMethodTag(corresponding));
               
            } else if(child.getASTType() == ASTNodeType.constructor_definition) {
                ConstructorTypeAnalyzer analyzer = new ConstructorTypeAnalyzer(child, parentType);
                if(!determineTypes(analyzer)) {
                    output = false;
                    setIsFailurePoint(child);
                }
            }
            
        }
        releaseTrackingClosure();
        return output;
    }
}
