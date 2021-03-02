package radin.midanalysis.pattern_replacement;

import radin.core.semantics.TypeEnvironment;
import radin.midanalysis.TypeAugmentedSemanticNode;

import java.util.HashMap;

public class Pattern {
    
    private PatternNode inputHead;
    private PatternNode outputHead;
    private HashMap<String, TypeAugmentedSemanticNode> variables;

    /**
     * Applies a pattern exactly once to a TAST tree
     * @param node The input node
     * @return if the pattern was applied
     */
    private boolean applyPattern(TypeAugmentedSemanticNode node, TypeEnvironment e) {
        return false;
    }

    /**
     * Searches through a tree and finds the first
     * @param fullTree the entire tree
     * @return the root node of a matching tree, or null if none is found
     */
    private TypeAugmentedSemanticNode findMatchingRootNode(TypeAugmentedSemanticNode fullTree, TypeEnvironment e) {
        if(inputHead.isMatch(fullTree, e)) {
            return fullTree;
        }
        for (TypeAugmentedSemanticNode child : fullTree.getDirectChildren()) {
            TypeAugmentedSemanticNode ret = findMatchingRootNode(child, e);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    /**
     * Apply the pattern to all matching patterns within the tree
     * @param fullTree the full tree to apply the pattern to
     */
    public void apply(TypeAugmentedSemanticNode fullTree, TypeEnvironment e) {
        TypeAugmentedSemanticNode toFix = findMatchingRootNode(fullTree, e);
        while (toFix != null) {
            if(!applyPattern(fullTree, e)) {
                throw new Error("Didn't apply pattern but should have been");
            }
            toFix = findMatchingRootNode(fullTree, e);
        }
    }
    
}
