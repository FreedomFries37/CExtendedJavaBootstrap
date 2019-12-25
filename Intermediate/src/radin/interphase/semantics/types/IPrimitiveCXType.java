package radin.interphase.semantics.types;

public interface IPrimitiveCXType extends CXType {
    
    @Override
    default boolean isPrimitive() {
        return true;
    }
    
    @Override
    default String generateCDefinition(String identifier) {
        return generateCDefinition() + " " + identifier;
    }
}
