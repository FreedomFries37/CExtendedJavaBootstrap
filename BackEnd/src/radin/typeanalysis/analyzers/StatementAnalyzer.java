package radin.typeanalysis.analyzers;

import radin.interphase.semantics.types.CXType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;

public class StatementAnalyzer extends TypeAnalyzer {
    
    private CXType returnType;
    
    public StatementAnalyzer(TypeAugmentedSemanticNode tree, CXType returnType) {
        super(tree);
        this.returnType = returnType;
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        
        
        return true;
    }
}
