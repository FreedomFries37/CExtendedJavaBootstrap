package radin.core.chaining;

import radin.core.errorhandling.AbstractCompilationError;

import javax.naming.OperationNotSupportedException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ToolChainFactory {
    
   
    
    public static abstract class ToolChainLink <T, R> implements IToolChain<T, R> {
        protected List<AbstractCompilationError> errors = new LinkedList<>();
        
        public <O> ToolChainLink<T, O> chain_to(ToolChainLink<? super R, O> next) {
            next.errors = this.errors;
            return new ChainLink<>(this, next);
        }
    
        public <I> ToolChainLink<I, R> chain_from(ToolChainLink<? super I, ? extends T> prev) {
            this.errors = prev.errors;
            return new ChainLink<>(prev, this);
        }
    
        @Override
        public List<AbstractCompilationError> getErrors() {
            return errors;
        }
    }
    
    public static abstract class ToolChainHead<R> extends ToolChainLink<Void, R> {
    
        @Override
        public <I> ToolChainLink<I, R> chain_from(ToolChainLink<? super I, ? extends Void> prev) {
            throw new UnsupportedOperationException();
        }
        
        
    }
    
    private static class ToolChainHeadChain<T, R> extends ToolChainLink<Void, R> {
        private ToolChainHead<? extends T> head;
        private IToolChain<? super T, ? extends R> chain;
    
        public ToolChainHeadChain(ToolChainHead<? extends T> head, IToolChain<? super T, ? extends R> chain) {
            this.head = head;
            this.chain = chain;
        }
    
        @Override
        public <O> ToolChainLink<Void, O> chain_to(ToolChainLink<? super R, O> next) {
            return new ToolChainHeadChain<>(head, new ChainLink<>(chain, next));
        }
    
        @Override
        public <I> ToolChainLink<I, R> chain_from(ToolChainLink<? super I, ? extends Void> prev) {
            throw new UnsupportedOperationException();
        }
    
        @Override
        public R invoke(Void input) {
            return invoke();
        }
        
        public R invoke() {
            T headOutput = head.invoke(null);
            return chain.invoke(headOutput);
        }
    }

    private static class IdentityLink<T> extends ToolChainLink<T, T> {
    
        @Override
        public T invoke(T input) {
            return input;
        }
    
        
    }
    
    private static class FunctionLink<T, R> extends ToolChainLink<T, R> {
        
        private Function<? super T, ? extends R> function;
    
        public FunctionLink(Function<? super T, ? extends R> function) {
            this.function = function;
        }
    
        @Override
        public R invoke(T input) {
            return function.apply(input);
        }
    }
    
    private static class ChainLink <T, M, R> extends ToolChainLink<T, R> {
        private IToolChain<? super T, ? extends M> front;
        private IToolChain<? super M, ? extends R> back;
    
        public ChainLink(IToolChain<? super T, ? extends M> front, IToolChain<? super M, ? extends R> back) {
            this.front = front;
            this.back = back;
        }
    
        @Override
        public R invoke(T input) {
            M invoke = front.invoke(input);
            return back.invoke(invoke);
        }
    }
    
    private static class CompilerFunctionLink <T, R> extends ToolChainLink<T, R> {
        private ICompilerFunction<? super T, ? extends R> part;
    
        public CompilerFunctionLink(ICompilerFunction<? super T, ? extends R> part) {
            this.part = part;
        }
    
        @Override
        public R invoke(T input) {
            R invoke = part.invoke(input);
            errors.addAll(part.getErrors());
            return invoke;
        }
    }
    
    private static class CompilerProducerLink <R> extends ToolChainHead<R>  {
        private ICompilerProducer<? extends R> part;
    
        public CompilerProducerLink(ICompilerProducer<? extends R> part) {
            this.part = part;
        }
    
        @Override
        public R invoke(Void input) {
            return invoke();
        }
    
        
        public R invoke() {
            R invoke = part.invoke();
            errors.addAll(part.getErrors());
            return invoke;
        }
    }
    
   
    
    public static <T> ToolChainLink<T, T> identity() {
        return new IdentityLink<>();
    }
    
    public static <T, R> ToolChainLink<T, R> function(Function<? super T, ? extends R> function) {
        return new FunctionLink<>(function);
    }
    
    public static <T, R> ToolChainLink<T, R> compilerFunction(ICompilerFunction<? super T, ? extends R> part) {
        return new CompilerFunctionLink<>(part);
    }
    
    public static <R> ToolChainHead<R> compilerProducer(ICompilerProducer<? extends R> part) {
        return new CompilerProducerLink<>(part);
    }
    
}


