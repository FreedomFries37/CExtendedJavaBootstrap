package radin.core;

import radin.core.chaining.ICompilerProducer;
import radin.core.chaining.IToolChainHead;
import radin.core.semantics.TypeEnvironment;

public interface IFrontEndUnit<S> extends ICompilerProducer<S>, IToolChainHead<S> {
    
    TypeEnvironment getEnvironment();
    String getUsedString();
    
    @Override
    default S invoke(Void input) {
        return invoke();
    }
}
