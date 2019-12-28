package radin.typeanalysis.analyzers;

import radin.interphase.semantics.types.CXType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.util.LinkedList;
import java.util.List;

public class SequenceTypeAnalyzer extends TypeAnalyzer {
    
    private List<CXType> sequenceTypes;
    
    public SequenceTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
        sequenceTypes = new LinkedList<>();
    }
    
    public List<CXType> getSequenceTypes() {
        return sequenceTypes;
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        
        
        
        
        return true;
    }
}
