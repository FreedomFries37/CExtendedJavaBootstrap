package radin.core.output.midanalysis.typeanalysis.analyzers;

import radin.core.output.midanalysis.ScopedTypeTracker;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.typeanalysis.TypeAnalyzer;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.utility.ICompilationSettings;

public class GenericTypeAnalyzer extends TypeAnalyzer {
    
    public GenericTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        ICompilationSettings.debugLog.info(node.toTreeForm());
    
        TypeAugmentedSemanticNode functionDefinition = node.getASTChild(ASTNodeType.function_definition);
        FunctionTypeAnalyzer functionTypeAnalyzer = new FunctionTypeAnalyzer(functionDefinition);
        
        if(!determineTypes(functionTypeAnalyzer)) return false;
        
        return true;
    }
}
