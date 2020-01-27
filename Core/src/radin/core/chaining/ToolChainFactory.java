package radin.core.chaining;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.utility.ICompilationSettings;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ToolChainFactory {
    
    
    /**
     * A tool chain builder is a toolchain that can be expanded upon. Using this class ensures that all parts of a
     * toolchain have the same error collector, and that the objects that are passed through the chain are passed
     * safely.
     * @param <T> The input type of the chain
     * @param <R> The output type that the chain
     */
    public static abstract class ToolChainBuilder <T, R> implements IToolChain<T, R> {
        protected List<AbstractCompilationError> errors = new LinkedList<>();
    
        /**
         * Returns a new chain that takes the current chain and feeds into the other chain
         * @param next the other chain thats a builder type to ensure proper error holding. The input type of the
         *             next chain must fulfil the requirements of {@code <? super R>}
         * @param <O> the output type of the next chain.
         * @return a new {@link ToolChainBuilder} object
         */
        public <O> ToolChainBuilder<T, O> chain_to(ToolChainBuilder<? super R, O> next) {
            next.errors = this.errors;
            var troChainLink = new ChainBuilder<T, R, O>(this, next);
            troChainLink.errors = this.errors;
            return troChainLink;
        }
    
        /**
         * Returns a new chain that takes the prev chain and feeds it into this chain
         * @param prev the other chain builder. It's output must fulfil the requirements of {@code <? extends T>}
         * @param <I> the input type of the previous chain
         * @return a new {@link ToolChainBuilder} object
         */
        public <I> ToolChainBuilder<I, R> chain_from(ToolChainBuilder<I, ? extends T> prev) {
            return prev.chain_to(this);
        }
    
        /**
         *
         * @return the errors that toolchain encountered
         */
        @Override
        public List<AbstractCompilationError> getErrors() {
            return errors;
        }
    
        /**
         * Clears the errors
         */
        @Override
        public void clearErrors() {
            errors.clear();
        }
    
        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    
        /**
         * Makes the chain uneditable
         * @return the builder as an uneditable chain
         */
        public IToolChain<T, R> toChain() {
            return this;
        }
    
        @Override
        public <V> void setVariable(String variable, V value) {
        
        }
    
        @Override
        public <V> V getVariable(String variable) {
            return null;
        }
    }
    
    public static abstract class ToolChainHead<R> extends ToolChainBuilder<Void, R> {
    
        /**
         * @throws UnsupportedOperationException Can't be used on an object of this type
         */
        @Override
        public <I> ToolChainBuilder<I, R> chain_from(ToolChainBuilder<I, ? extends Void> prev) {
            throw new UnsupportedOperationException();
        }
        
        
    }
    
    private static class ToolChainHeadChain<T, R> extends ToolChainBuilder<Void, R> {
        private ToolChainHead<? extends T> head;
        private IToolChain<? super T, ? extends R> chain;
    
        public ToolChainHeadChain(ToolChainHead<? extends T> head, IToolChain<? super T, ? extends R> chain) {
            this.head = head;
            this.chain = chain;
        }
    
        @Override
        public <O> ToolChainBuilder<Void, O> chain_to(ToolChainBuilder<? super R, O> next) {
            return new ToolChainHeadChain<>(head, new ChainBuilder<>(chain, next));
        }
    
        /**
         * @throws UnsupportedOperationException Can't be used on an object of this type
         */
        @Override
        public <I> ToolChainBuilder<I, R> chain_from(ToolChainBuilder<I, ? extends Void> prev) {
            throw new UnsupportedOperationException();
        }
    
        @Override
        public void clearErrors() {
            head.clearErrors();
            chain.clearErrors();
        }
    
        @Override
        public <V> void setVariable(String variable, V value) {
            head.setVariable(variable, value);
            chain.setVariable(variable, value);
        }
    
        @Override
        public <V> V getVariable(String variable) {
            var variable1 = head.<V>getVariable(variable);
            if(variable1 != null) return variable1;
            return chain.getVariable(variable);
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

    private static class IdentityBuilder <T> extends ToolChainBuilder<T, T> {
    
        @Override
        public T invoke(T input) {
            ICompilationSettings.debugLog.info("Running Identity link");
            return input;
        }
        
    }
    
    private static class FunctionBuilder <T, R> extends ToolChainBuilder<T, R> {
        
        private Function<? super T, ? extends R> function;
    
        public FunctionBuilder(Function<? super T, ? extends R> function) {
            this.function = function;
        }
    
        @Override
        public R invoke(T input) {
            ICompilationSettings.debugLog.info("Running function link");
            return function.apply(input);
        }
    }
    
    private static class ChainBuilder <T, M, R> extends ToolChainBuilder<T, R> {
        private IToolChain<? super T, ? extends M> front;
        private IToolChain<? super M, ? extends R> back;
    
        public ChainBuilder(IToolChain<? super T, ? extends M> front, IToolChain<? super M, ? extends R> back) {
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
    
        @Override
        public <V> void setVariable(String variable, V value) {
            front.setVariable(variable, value);
            back.setVariable(variable, value);
        }
    
        @Override
        public <V> V getVariable(String variable) {
            var variable1 = front.<V>getVariable(variable);
            if(variable1 != null) return variable1;
            return back.getVariable(variable);
        }
    }
    
    private static class CompilerFunctionBuilder <T, R> extends ToolChainBuilder<T, R> {
        private ICompilerFunction<? super T, ? extends R> part;
    
        public CompilerFunctionBuilder(ICompilerFunction<? super T, ? extends R> part) {
            this.part = part;
        }
    
        @Override
        public R invoke(T input) {
            ICompilationSettings.debugLog.info("Running Compiler function on " + part.getClass().getSimpleName());
            R invoke = part.invoke(input);
            
            errors.addAll(part.getErrors());
            return invoke;
        }
    
        @Override
        public <V> void setVariable(String variable, V value) {
            part.setVariable(variable, value);
        }
    
        @Override
        public <V> V getVariable(String variable) {
            return part.getVariable(variable);
        }
    }
    
    private static class CompilerProducerBuilder <R> extends ToolChainHead<R>  {
        private ICompilerProducer<? extends R> part;
    
        public CompilerProducerBuilder(ICompilerProducer<? extends R> part) {
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
    
        @Override
        public <V> void setVariable(String variable, V value) {
            part.setVariable(variable, value);
        }
    
        @Override
        public <V> V getVariable(String variable) {
            return part.getVariable(variable);
        }
    }
    
    private static class InPlaceCompilerBuilder <T> extends ToolChainBuilder<T, T> {
        private IInPlaceCompilerAnalyzer<? super T> part;
    
        public InPlaceCompilerBuilder(IInPlaceCompilerAnalyzer<? super T> part) {
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
    
        @Override
        public <V> void setVariable(String variable, V value) {
            part.setVariable(variable, value);
        }
    
        @Override
        public <V> V getVariable(String variable) {
            return part.getVariable(variable);
        }
    }
    
    
    /**
     * Creates a toolchain builder that does nothing, but can be used to enforce an input type of a the next link in
     * a chain
     * <p>
     *     For example a chain of type {@code Chain<T, R>} <i>a</i> can feed into any chain of type {@code Chain<?
     *     super R, O>}.
     *     If you were to chain <i>a</i> into an identity of type {@code C} such that {@code C super R}, now the new
     *     chain can only feed into another chain of type {@code Chain<? super C, O>}. This reduces the set of
     *     possible chains to feed into, and can aid in clarity.
     * </p>
     *
     * @param <T> the type to output as
     * @return a new identity chain
     */
    public static <T> ToolChainBuilder<T, T> identity() {
        return new IdentityBuilder<>();
    }
    
    /**
     * Creates a toolchain builder that run a function on an object of at most type T and outputs an object that is at
     * least type R.
     * <p>
     * <i>The errors of this part are reported to the builder as a whole.</i>
     * @param function the function
     * @param <T> the input type
     * @param <R> the output type
     * @return a tool chain builder
     */
    public static <T, R> ToolChainBuilder<T, R> function(Function<? super T, ? extends R> function) {
        return new FunctionBuilder<>(function);
    }
    
    /**
     * Creates a toolchain builder that runs an object of type {@link IInPlaceCompilerAnalyzer} on type {@code T}
     * <p>
     * <i>The errors of this part are reported to the chain as a whole.</i>
     * @param analyzer the compiler part that both has input types of T and outputs of
     * @param <T> the input and output types of the chain
     * @return a tool chain builder
     */
    public static <T> ToolChainBuilder<T, T> compilerAnalyzer(IInPlaceCompilerAnalyzer<? super T> analyzer) {
        return new InPlaceCompilerBuilder<>(analyzer);
    }
    
    /**
     * Creates a toolchain builder that runs an object of type {@link ICompilerFunction} that takes in an object of
     * at most type T and outputs an object that is at least type R
     *
     * <p>
     * <i>The errors of this part are reported to the chain as a whole.</i>
     * @param part the compiler part that takes in at most an object of type T as input and at least an object of
     *             type R as output
     * @param <T> the input type of the chain
     * @param <R> the output type of the chain
     * @return
     */
    public static <T, R> ToolChainBuilder<T, R> compilerFunction(ICompilerFunction<? super T, ? extends R> part) {
        return new CompilerFunctionBuilder<>(part);
    }
    
    /**
     * Creates a toolchain builder that runs an object of type {@link ICompilerProducer} outputs an object that is at least
     * type R
     *
     * <p>
     * <i>The errors of this part are reported to the chain as a whole.</i>
     * @param part the compiler part that outputs an object of at least type R
     * @param <R>
     * @return
     */
    public static <R> ToolChainHead<R> compilerProducer(ICompilerProducer<? extends R> part) {
        return new CompilerProducerBuilder<>(part);
    }
    
}


