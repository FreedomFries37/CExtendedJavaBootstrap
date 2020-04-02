package radin.core.output.midanalysis.typeanalysis.analyzers;

import radin.core.output.midanalysis.MethodTASNTracker;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.output.tags.PriorConstructorTag;
import radin.core.output.typeanalysis.errors.TypeNotDefinedError;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.primitives.PointerType;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.core.output.typeanalysis.TypeAnalyzer;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.typeanalysis.errors.IllegalReturnInConstructorError;
import radin.core.output.typeanalysis.errors.NoConstructorError;

import java.util.LinkedList;
import java.util.List;

public class ConstructorTypeAnalyzer extends TypeAnalyzer {
    
    private CXClassType owner;
    private CXConstructor priorConstructor;
    
    public ConstructorTypeAnalyzer(TypeAugmentedSemanticNode tree, CXClassType owner) {
        super(tree);
        this.owner = owner;
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
    
        typeTrackingClosure();
        
        getCurrentTracker().addVariable("this", new PointerType(owner));
        TypeAugmentedSemanticNode parameters = node.getASTChild(ASTNodeType.parameter_list);
        List<CXType> parametersTypes = new LinkedList<>();
        for (TypeAugmentedSemanticNode parameter : parameters.getAllChildren(ASTNodeType.declaration)) {
            assert parameter.getASTNode() instanceof TypedAbstractSyntaxNode;
            CXType type = ((TypedAbstractSyntaxNode) parameter.getASTNode()).getCxType();
            parametersTypes.add(type);
            String name = parameter.getASTChild(ASTNodeType.id).getToken().getImage();
        
            getCurrentTracker().addVariable(name, type);
        }
    
        CXConstructor currentConstructor = owner.getConstructor(new ParameterTypeList(parametersTypes));
        
        
        if(node.hasASTChild(ASTNodeType.sequence)) {
            CXClassType priorType;
            if (node.hasASTChild(ASTNodeType.id)) {
                // has a this token
                priorType = owner;
            } else if(node.hasASTChild(ASTNodeType._super)) {
                if(owner.getParent() == null) {
                    throw new TypeNotDefinedError(node.getASTChild(ASTNodeType._super).findFirstToken());
                }
                priorType = owner.getParent();
            } else {
                return false;
            }
            
            SequenceTypeAnalyzer sequenceTypeAnalyzer =
                    new SequenceTypeAnalyzer(node.getASTChild(ASTNodeType.sequence));
            
            if(!determineTypes(sequenceTypeAnalyzer)) return false;
    
            ParameterTypeList parameterTypeList = new ParameterTypeList(sequenceTypeAnalyzer.getCollectedTypes());
            
            if(!getCurrentTracker().constructorVisible(priorType, parameterTypeList)) {
                throw new NoConstructorError(priorType, parameterTypeList);
            }
    
            CXConstructor priorConstructor = priorType.getConstructor(parameterTypeList);
            
            currentConstructor.setPriorConstructor(priorConstructor);
            node.addCompilationTag(new PriorConstructorTag(priorConstructor, node.getASTChild(ASTNodeType.sequence)));
        }
    
        TypeAugmentedSemanticNode compoundStatement = node.getASTChild(ASTNodeType.compound_statement);
        if(compoundStatement != null) {
            MethodTASNTracker.getInstance().add(currentConstructor, node);
            CompoundStatementTypeAnalyzer compoundStatementTypeAnalyzer =
                    new CompoundStatementTypeAnalyzer(compoundStatement, CXPrimitiveType.VOID, false);
            if (!determineTypes(compoundStatementTypeAnalyzer)) return false;
    
            if (compoundStatementTypeAnalyzer.isReturns()) {
                throw new IllegalReturnInConstructorError();
            }
        }
        releaseTrackingClosure();
        return true;
    }
}
