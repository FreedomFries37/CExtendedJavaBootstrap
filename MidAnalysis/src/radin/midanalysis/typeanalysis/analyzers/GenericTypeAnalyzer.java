package radin.midanalysis.typeanalysis.analyzers;

import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.typeanalysis.TypeAnalyzer;
import radin.core.semantics.ASTNodeType;
import radin.core.utility.ICompilationSettings;

public class GenericTypeAnalyzer extends TypeAnalyzer {
    
    public GenericTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        ICompilationSettings.debugLog.info("Generic Declaration: " + node.toTreeForm());
    
        switch (node.getChild(1).getASTType()) {
            case function_definition: {
                TypeAugmentedSemanticNode functionDefinition = node.getASTChild(ASTNodeType.function_definition);
                FunctionTypeAnalyzer functionTypeAnalyzer = new FunctionTypeAnalyzer(functionDefinition);
    
                if(!determineTypes(functionTypeAnalyzer)) return false;
                
                /*
                getGenericModule().declareGenericFunction(
                        new CXIdentifier(functionDefinition.getASTChild(ASTNodeType.id).getToken(), false),
                        functionDefinition.getCXType(),
                        
                );
                
                 */
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
