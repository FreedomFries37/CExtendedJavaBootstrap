package radin.midanalysis;


import radin.output.typeanalysis.VariableTypeTracker;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.utility.ICompilationSettings;

import java.util.Stack;

public abstract class ScopedTypeTracker implements IScopedTracker<VariableTypeTracker>{
    
    protected static TypeEnvironment environment;
    protected Stack<VariableTypeTracker> trackerStack;
    protected GenericModule genericModule;
    
    public ScopedTypeTracker(Stack<VariableTypeTracker> trackerStack, GenericModule genericModule) {
        this.trackerStack = trackerStack;
        this.genericModule = genericModule;
    }
    
    public ScopedTypeTracker(ScopedTypeTracker old) {
        trackerStack = old.trackerStack;
        genericModule = old.genericModule;
    }
    
    public ScopedTypeTracker() {
        trackerStack = new Stack<>();
        genericModule = new GenericModule();
    }
    
    public GenericModule getGenericModule() {
        return genericModule;
    }
    
    @Override
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
    
    @Override
    public void typeTrackingClosure(CXClassType classType) {
        VariableTypeTracker next = trackerStack.peek().createInnerTypeTracker(classType);
        trackerStack.push(next);
        ICompilationSettings.debugLog.finest("Scope Level: " + trackerStack.size()+ " " + "#".repeat(trackerStack.size()) + "    Inheriting from " + classType + " scope");
    }
    
    @Override
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
    
    
    public void typeTrackingClosureSuperLoad(CXClassType cxClassType) {
        VariableTypeTracker next = getCurrentTracker().createInnerTypeTracker();
        CXClassType ptr = cxClassType;
        while(ptr != null) {
            next = next.createInnerTypeTrackerLoad(ptr);
            ptr = ptr.getParent();
        }
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
    
    @Override
    public void setTrackerStack(Stack<VariableTypeTracker> trackerStack) {
        this.trackerStack = trackerStack;
    }
    
    public static void setEnvironment(TypeEnvironment environment) {
        ScopedTypeTracker.environment = environment;
        
    }
    
    @Override
    public VariableTypeTracker getCurrentTracker() {
        return trackerStack.peek();
    }
    
    @Override
    public boolean isBaseTracker() {
        return trackerStack.size() == 1;
    }
    
    @Override
    public void releaseTrackingClosure() {
        trackerStack.pop();
        getCurrentTracker().removeParentlessStructFields();
        ICompilationSettings.debugLog.finest("Scope Level: " + trackerStack.size() + " " + "#".repeat(trackerStack.size()));
    }
    
    @Override
    public void reset() {
        trackerStack = new Stack<>();
    }
    
}