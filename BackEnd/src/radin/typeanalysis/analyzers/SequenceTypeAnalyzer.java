package radin.typeanalysis.analyzers;

import radin.interphase.semantics.types.CXType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.util.LinkedList;
import java.util.List;

public class SequenceTypeAnalyzer extends TypeAnalyzer {
    
    private List<CXType> collectedTypes;
    
    public SequenceTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
        collectedTypes = new LinkedList<>();
    }
    
    
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
    
        for (TypeAugmentedSemanticNode child : node.getChildren()) {
            
            ExpressionTypeAnalyzer analyzer = new ExpressionTypeAnalyzer(child);
            if(!determineTypes(analyzer)) return false;
            collectedTypes.add(child.getCXType());
        }
        
        return true;
    }
    
    public List<CXType> getCollectedTypes() {
        return collectedTypes;
    }
}
