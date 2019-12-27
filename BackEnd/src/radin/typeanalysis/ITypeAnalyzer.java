package radin.typeanalysis;

import radin.interphase.semantics.types.compound.CXClassType;

public interface ITypeAnalyzer {
    
    void typeTrackingClosure();
    void typeTrackingClosure(CXClassType classType);
    
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
