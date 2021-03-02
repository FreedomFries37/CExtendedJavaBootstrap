package radin.midanalysis.pattern_replacement;

import radin.core.chaining.IToolChain;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.semantics.TypeEnvironment;
import radin.midanalysis.TypeAugmentedSemanticNode;

import java.util.LinkedList;
import java.util.List;

public class PatternManager implements IToolChain<TypeAugmentedSemanticNode, TypeAugmentedSemanticNode> {

    private List<Pattern> patterns;
    private List<AbstractCompilationError> errors;
    private TypeEnvironment environment;

    public PatternManager(TypeEnvironment environment) {
        this.environment = environment;
        this.patterns = new LinkedList<>();
        this.errors = new LinkedList<>();
    }

    /**
     * Update the manager
     * @param pattern the pattern to add the manager
     */
    public void addPattern(Pattern pattern) {
        patterns.add(pattern);
    }

    /**
     * Applies all of the patterns within the manager on the {@link TypeAugmentedSemanticNode} inputted
     * @param input the node to input
     * @return the input tree with the patterns replaced
     */
    @Override
    public TypeAugmentedSemanticNode invoke(TypeAugmentedSemanticNode input) {
        for (Pattern pattern : patterns) {
            pattern.apply(input, environment);
        }
        return input;
    }

    @Override
    public List<AbstractCompilationError> getErrors() {
        return errors;
    }


}
