package radin.core.output.midanalysis;


import radin.core.output.typeanalysis.VariableTypeTracker;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.utility.ICompilationSettings;

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
        VariableTypeTracker next;
        if(trackerStack.empty()) {
            next = trackerStack.push(new VariableTypeTracker(environment));
        } else {
            next = trackerStack.peek().createInnerTypeTracker();
        }
        trackerStack.push(next);
        ICompilationSettings.debugLog.finest("Scope Level: " + trackerStack.size() + " " + "#".repeat(trackerStack.size()));
    }
    
    public void typeTrackingClosure(CXClassType classType) {
        VariableTypeTracker next = trackerStack.peek().createInnerTypeTracker(classType);
        trackerStack.push(next);
        ICompilationSettings.debugLog.finest("Scope Level: " + trackerStack.size()+ " " + "#".repeat(trackerStack.size()) + "    Inheriting from " + classType + " scope");
    }
    
    public void typeTrackingClosureLoad(CXClassType cxClassType) {
        if(!VariableTypeTracker.trackerPresent(cxClassType)) typeTrackingClosure();
        VariableTypeTracker next = trackerStack.peek().createInnerTypeTrackerLoad(cxClassType);
        trackerStack.push(next);
        
        ICompilationSettings.debugLog.finest("Scope Level: " + trackerStack.size() + " " + "#".repeat(trackerStack.size())
        + "    Loading into " + cxClassType + " scope");
        for (String s : next.allMethodsAvailable()) {
            ICompilationSettings.debugLog.finest("Method Loaded: " + s);
        }
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
    
    public boolean isBaseTracker() {
        return trackerStack.size() == 1;
    }
    
    public void releaseTrackingClosure() {
        trackerStack.pop();
        getCurrentTracker().removeParentlessStructFields();
        ICompilationSettings.debugLog.finest("Scope Level: " + trackerStack.size() + " " + "#".repeat(trackerStack.size()));
    }
    
    public void reset() {
        trackerStack = new Stack<>();
    }
    
}