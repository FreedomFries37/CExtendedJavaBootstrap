package radin.core.utility;

import java.util.function.Consumer;
import java.util.function.Function;

public class Option<T> {

    public static class NoneUnwrapped extends Error {
        public NoneUnwrapped() {
            super("None value unwrapped");
        }

        public NoneUnwrapped(String msg) {
            super(msg);
        }
    }
    
    private T data;
    
    private Option(T data) {
        this.data = data;
    }
    
    /**
     * Checks if the option has data
     * @return there is some data
     */
    public boolean isSome() {
        return data != null;
    }
    
    
    /**
     * Checks if the option is empty
     * @return there is no data
     */
    public boolean isNone() {
        return data == null;
    }
    
    /**
     * Gets the data in the option
     * @return the data
     * @throws NoneUnwrapped if the data is null
     */
    public T unwrap() {
        if(isNone()) {
            throw new NoneUnwrapped();
        }
        return data;
    }
    
    /**
     * Gets the data in the option
     * @return the data
     * @throws NoneUnwrapped if the data is null, with a user set message
     */
    public T expect(String message) {
        if(isNone()) {
            throw new NoneUnwrapped(message);
        }
        return data;
    }
    
    /**
     * Puts the data contained in this option into the reference if it exists.
     * @param ref A non-null reference object
     * @return if this was an option with some value
     */
    public boolean match(Reference<T> ref) {
        if(ref == null) throw new UnsupportedOperationException("Reference must not be null");
        ref.setValue(data);
        return isSome();
    }
    
    /**
     * Applies a function to the data in the option, if it exists
     * @param function the function to apply
     * @param <R> the return type
     * @return The option wrapped result, where its Some if the original data existed, or None if no data existed originally
     */
    public <R> Option<R> onSome(Function<? super T, ? extends R> function) {
        Reference<T> ref = new Reference<>();
        if(match(ref)) {
            T value = ref.getValue();
            return Some(function.apply(value));
        } else {
            return None();
        }
    }
    
    /**
     * Applies a function to the data in the option, if it exists
     * @param function the function to apply
     * @param <R> the return type
     * @return The option wrapped result, where its Some if the original data existed, or None if no data existed originally
     */
    public <R> R onSome(Function<? super T, ? extends R> function, R noneResult) {
        Reference<T> ref = new Reference<>();
        if(match(ref)) {
            T value = ref.getValue();
            return function.apply(value);
        } else {
            return noneResult;
        }
    }
    
    /**
     * Applies a function to the data in the option, if it exists
     * @param function the function to apply
     */
    public void onSome(Consumer<? super T> function) {
        Reference<T> ref = new Reference<>();
        if(match(ref)) {
            T value = ref.getValue();
            function.accept(value);
        }
    }
    
    public T thisOrElse(T or) {
        Reference<T> ref = new Reference<>();
        if(match(ref)) {
            return ref.getValue();
        } else {
            return or;
        }
    }
    
    
    public static <T> Option<T> Some(T data) {
        return new Option<>(data);
    }
    
    public static <T> Option<T> None() {
        return new Option<>(null);
    }
}
