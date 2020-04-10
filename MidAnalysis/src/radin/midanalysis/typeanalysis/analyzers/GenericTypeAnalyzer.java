package radin.midanalysis.typeanalysis.analyzers;

import radin.core.lexical.Token;
import radin.core.semantics.generics.CXParameterizedType;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.typeanalysis.TypeAnalyzer;
import radin.core.semantics.ASTNodeType;
import radin.core.utility.ICompilationSettings;
import radin.output.tags.GenericLocationTag;

import java.util.LinkedList;
import java.util.List;

public class GenericTypeAnalyzer extends TypeAnalyzer {
    
    public GenericTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        ICompilationSettings.debugLog.info("Generic Declaration: " + node.toTreeForm());
    
        List<CXParameterizedType> parameterTypes = new LinkedList<>();
        for (TypeAugmentedSemanticNode child : node.getASTChild(ASTNodeType.parameterized_types).getChildren()) {
            parameterTypes.add(((CXParameterizedType) child.getCXType()));
        }
    
        TypeAugmentedSemanticNode genericItem = node.getChild(1);
        var tag = new GenericLocationTag();
        node.addCompilationTag(tag);
        switch (genericItem.getASTType()) {
            case function_definition: {
                TypeAugmentedSemanticNode functionDefinition = node.getASTChild(ASTNodeType.function_definition);
                FunctionTypeAnalyzer functionTypeAnalyzer = new FunctionTypeAnalyzer(functionDefinition);
    
                if(!determineTypes(functionTypeAnalyzer)) return false;
                CXType returnType = functionDefinition.getCXType();
                
                List<CXType> argTypes = new LinkedList<>();
    
                for (TypeAugmentedSemanticNode dec : genericItem.getASTChild(ASTNodeType.parameter_list).getChildren()) {
                    argTypes.add(dec.getCXType());
                }
    
    
                Token id = functionDefinition.getASTChild(ASTNodeType.id).getToken();
                getGenericModule().declareGenericFunction(
                        new CXIdentifier(id, false),
                        returnType,
                        parameterTypes,
                        argTypes,
                        genericItem.getASTNode(),
                        environment,
                        id,
                        tag
                );
                
                
            }
            break;
            case class_type_definition: {
                
                
                break;
            }
            default: {
                return false;
            }
        }
        
        
        return true;
    }
}
