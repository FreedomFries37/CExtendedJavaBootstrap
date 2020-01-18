package radin.core.output.midanalysis;

import radin.core.output.typeanalysis.VariableTypeTracker;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.compound.CXClassType;

import java.util.Stack;

public abstract class ScopedTypeTracker {
    
    protected static TypeEnvironment environment;
    protected Stack<VariableTypeTracker> trackerStack;
    
    public ScopedTypeTracker(Stack<VariableTypeTracker> trackerStack) {
        this.trackerStack = trackerStack;
    }
    
    public ScopedTypeTracker(ScopedTypeTracker old) {
        trackerStack = old.trackerStack;
    }
    
    public ScopedTypeTracker() {
        trackerStack = new Stack<>();
    }
    
    public void typeTrackingClosure() {
        VariableTypeTracker next = trackerStack.peek().createInnerTypeTracker();
        trackerStack.push(next);
    }
    
    public void typeTrackingClosure(CXClassType classType) {
        VariableTypeTracker next = trackerStack.peek().createInnerTypeTracker(classType);
        trackerStack.push(next);
    }
    
    public void typeTrackingClosureLoad(CXClassType cxClassType) {
        if(!VariableTypeTracker.trackerPresent(cxClassType)) typeTrackingClosure();
        VariableTypeTracker next = trackerStack.peek().createInnerTypeTrackerLoad(cxClassType);
        trackerStack.push(next);
    }
    
    public static TypeEnvironment getEnvironment() {
        return environment;
    }
    
    public void setTrackerStack(Stack<VariableTypeTracker> trackerStack) {
        this.trackerStack = trackerStack;
    }
    
    public static void setEnvironment(TypeEnvironment environment) {
        ScopedTypeTracker.environment = environment;
    }
    
    public VariableTypeTracker getCurrentTracker() {
        return trackerStack.peek();
    }
    
    public void releaseTrackingClosure() {
        trackerStack.pop();
        getCurrentTracker().removeParentlessStructFields();
    }
    
    
    
}
