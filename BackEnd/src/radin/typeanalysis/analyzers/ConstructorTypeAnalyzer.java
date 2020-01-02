package radin.typeanalysis.analyzers;

import radin.compilation.tags.PriorConstructorTag;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.wrapped.PointerType;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.methods.CXConstructor;
import radin.interphase.semantics.types.methods.ParameterTypeList;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.errors.IllegalReturnInConstructorError;
import radin.typeanalysis.errors.NoConstructorError;
import radin.typeanalysis.errors.TypeNotDefinedError;

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
            assert parameter.getASTNode() instanceof TypeAbstractSyntaxNode;
            CXType type = ((TypeAbstractSyntaxNode) parameter.getASTNode()).getCxType();
            parametersTypes.add(type);
            String name = parameter.getASTChild(ASTNodeType.id).getToken().getImage();
        
            getCurrentTracker().addVariable(name, type);
        }
        
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
            CXConstructor currentConstructor = owner.getConstructor(new ParameterTypeList(parametersTypes));
            
            currentConstructor.setPriorConstructor(priorConstructor);
            node.addCompilationTag(new PriorConstructorTag(priorConstructor));
        }
    
        TypeAugmentedSemanticNode compoundStatement = node.getASTChild(ASTNodeType.compound_statement);
        CompoundStatementTypeAnalyzer compoundStatementTypeAnalyzer =
                new CompoundStatementTypeAnalyzer(compoundStatement, CXPrimitiveType.VOID, false);
        if(!determineTypes(compoundStatementTypeAnalyzer)) return false;
        
        if(compoundStatementTypeAnalyzer.isReturns()) {
            throw new IllegalReturnInConstructorError();
        }
        
        releaseTrackingClosure();
        return true;
    }
}
