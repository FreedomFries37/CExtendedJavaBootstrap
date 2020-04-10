package radin.core.semantics.generics;

public class GenericInstance<T> {
    public final String modifiedName;
    public final T type;
    
    
    public GenericInstance(String modifiedName, T type) {
        this.modifiedName = modifiedName;
        this.type = type;
    }
}
