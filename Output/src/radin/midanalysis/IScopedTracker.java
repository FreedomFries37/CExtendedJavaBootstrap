package radin.midanalysis;

import radin.output.typeanalysis.VariableTypeTracker;
import radin.core.semantics.types.compound.CXClassType;

import java.util.Stack;

public interface IScopedTracker<T> {
    
    void typeTrackingClosure();
    
    void typeTrackingClosure(CXClassType classType);
    
    void typeTrackingClosureLoad(CXClassType cxClassType);
    
    void setTrackerStack(Stack<T> trackerStack);
    
    VariableTypeTracker getCurrentTracker();
    
    boolean isBaseTracker();
    
    void releaseTrackingClosure();
    
    void reset();
}
