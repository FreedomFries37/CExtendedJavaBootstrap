package radin.core.output.typeanalysis;

import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.types.compound.CXClassType;

public interface ITypeAnalyzer {
    
    void typeTrackingClosure();
    void typeTrackingClosure(CXClassType classType);
    void typeTrackingClosureLoad(CXClassType cxClassType);
    
    VariableTypeTracker getCurrentTracker();
    
    void releaseTrackingClosure();
    
    /**
     *
     * @return if could successfully determine all types
     */
    boolean determineTypes();
    /**
     * @param node the node to check
     * @return if could successfully determine all types
     */
    boolean determineTypes(TypeAugmentedSemanticNode node);
}
