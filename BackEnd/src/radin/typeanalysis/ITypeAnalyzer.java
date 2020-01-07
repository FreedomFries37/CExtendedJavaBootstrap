package radin.typeanalysis;

import radin.core.semantics.types.compound.CXClassType;

public interface ITypeAnalyzer {
    
    void typeTrackingClosure();
    void typeTrackingClosure(CXClassType classType);
    void typeTrackingClosureLoad(CXClassType cxClassType);
    
    TypeTracker getCurrentTracker();
    
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
