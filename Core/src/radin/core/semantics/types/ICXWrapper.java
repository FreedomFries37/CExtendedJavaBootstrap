package radin.core.semantics.types;

public interface ICXWrapper {
    
    CXType getWrappedType();
    
    default String infoDump() {
        return getWrappedType().infoDump();
    }
}
