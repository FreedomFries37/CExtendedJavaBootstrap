package radin.interphase.semantics.types;

public interface CXEquivalent {
    String generateCDefinition();
    default String generateCDeclaration() {
        return generateCDefinition();
    }
}
