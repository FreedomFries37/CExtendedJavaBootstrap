package radin.core;

import radin.core.chaining.ICompilerProducer;
import radin.core.semantics.TypeEnvironment;

public interface IFrontEndUnit<S> extends ICompilerProducer<S> {
    
    
    TypeEnvironment getEnvironment();
    String getUsedString();
    void reset();
}
