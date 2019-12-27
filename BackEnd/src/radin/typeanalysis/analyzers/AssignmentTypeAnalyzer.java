package radin.typeanalysis.analyzers;

import radin.interphase.semantics.ASTNodeType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;

public class AssignmentTypeAnalyzer extends TypeAnalyzer {
    
    public AssignmentTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        assert node.getASTType() == ASTNodeType.assignment;
        
        TypeAugmentedSemanticNode lhs = node.getChild(0);
        TypeAugmentedSemanticNode rhs = node.getChild(1);
        
        
        
        
        
        
        return true;
    }
}
