package radin.core.chaining;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.utility.ICompilationSettings;

import java.lang.reflect.TypeVariable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ToolChainFactory {
    
   
    
    public static abstract class ToolChainLink <T, R> implements IToolChain<T, R> {
        protected List<AbstractCompilationError> errors = new LinkedList<>();
        
        public <O> ToolChainLink<T, O> chain_to(ToolChainLink<? super R, O> next) {
            next.errors = this.errors;
            var troChainLink = new ChainLink<T, R, O>(this, next);
            troChainLink.errors = this.errors;
            return troChainLink;
        }
    
        public <I> ToolChainLink<I, R> chain_from(ToolChainLink<? super I, ? extends T> prev) {
            prev.errors = this.errors;
            return new ChainLink<>(prev, this);
        }
    
        @Override
        public List<AbstractCompilationError> getErrors() {
            return errors;
        }
        
        @Override
        public void clearErrors() {
            errors.clear();
        }
    
        @Override
        public String toString() {
            return getClass().getSimpleName();
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
        public void clearErrors() {
            head.clearErrors();
            chain.clearErrors();
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
            ICompilationSettings.debugLog.info("Running Identity link");
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
            ICompilationSettings.debugLog.info("Running function link");
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
            ICompilationSettings.debugLog.info("Running link from " + front + " to " + back);
            M invoke = front.invoke(input);
            return back.invoke(invoke);
        }
    
        @Override
        public void clearErrors() {
            front.clearErrors();
            back.clearErrors();
        }
    }
    
    private static class CompilerFunctionLink <T, R> extends ToolChainLink<T, R> {
        private ICompilerFunction<? super T, ? extends R> part;
    
        public CompilerFunctionLink(ICompilerFunction<? super T, ? extends R> part) {
            this.part = part;
        }
    
        @Override
        public R invoke(T input) {
            ICompilationSettings.debugLog.info("Running Compiler function on " + part.getClass().getSimpleName());
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
            ICompilationSettings.debugLog.info("Running Compiler producer " + part.getClass().getSimpleName());
            R invoke = part.invoke();
            errors.addAll(part.getErrors());
            return invoke;
        }
    
        
    }
    
    private static class InPlaceCompilerLink <T> extends ToolChainLink<T, T> {
        private IInPlaceCompilerAnalyzer<? super T> part;
    
        public InPlaceCompilerLink(IInPlaceCompilerAnalyzer<? super T> part) {
            this.part = part;
        }
    
        @Override
        public T invoke(T input) {
            ICompilationSettings.debugLog.info("Running Compiler analyzer " + part.getClass().getSimpleName());
            part.setHead(input);
            if(!part.invoke()) {
                errors.addAll(part.getErrors());
                return null;
            }
            errors.addAll(part.getErrors());
            return input;
        }
    
        @Override
        public void clearErrors() {
            super.clearErrors();
            part.getErrors().clear();
        }
    }
    
   
    
    public static <T> ToolChainLink<T, T> identity() {
        return new IdentityLink<>();
    }
    
    public static <T, R> ToolChainLink<T, R> function(Function<? super T, ? extends R> function) {
        return new FunctionLink<>(function);
    }
    
    public static <T> ToolChainLink<T, T> compilerAnalyzer(IInPlaceCompilerAnalyzer<T> analyzer) {
        return new InPlaceCompilerLink<>(analyzer);
    }
    
    public static <T, R> ToolChainLink<T, R> compilerFunction(ICompilerFunction<? super T, ? extends R> part) {
        return new CompilerFunctionLink<>(part);
    }
    
    public static <R> ToolChainHead<R> compilerProducer(ICompilerProducer<? extends R> part) {
        return new CompilerProducerLink<>(part);
    }
    
}


