package radin.core.semantics.types;

public interface CXEquivalent {
    String generateCDefinition();
    default String generateCDeclaration() {
        return generateCDefinition();
    }
}
