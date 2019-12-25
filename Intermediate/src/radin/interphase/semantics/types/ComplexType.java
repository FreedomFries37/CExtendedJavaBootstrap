package radin.interphase.semantics.types;

public interface ComplexType extends CXType{
    
    @Override
    default boolean isPrimitive() {
        return false;
    }
}
