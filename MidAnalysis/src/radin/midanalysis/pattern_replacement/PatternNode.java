package radin.midanalysis.pattern_replacement;

import radin.core.AbstractTree;
import radin.core.semantics.TypeEnvironment;
import radin.midanalysis.TypeAugmentedSemanticNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PatternNode extends AbstractTree<PatternNode> {
    
    private final PatternSingleton singleton;
    private final List<PatternNode> children;

    public PatternNode(PatternSingleton singleton) {
        this.singleton = singleton;
        this.children = new LinkedList<>();
    }

    public void addChild(PatternNode child) {
        this.children.add(child);
    }

    @Override
    public List<PatternNode> postfix() {
        List<PatternNode> output = new LinkedList<>();
        output.add(this);
        for(PatternNode child : children) {
            output.addAll(child.postfix());
        }
        return output;
    }
    
    @Override
    public List<PatternNode> getDirectChildren() {
        return children;
    }
    
    @Override
    public List<PatternNode> getMutableChildren() {
        return children;
    }

    public boolean isMatch(TypeAugmentedSemanticNode node, TypeEnvironment e) {
        if(!singleton.match(node, e)) {
            return false;
        }
        Iterator<PatternNode> patternNodeIterator = children.iterator();
        Iterator<TypeAugmentedSemanticNode> childIterator = node.getDirectChildren().iterator();

        while(patternNodeIterator.hasNext() && childIterator.hasNext()) {
            PatternNode pattern = patternNodeIterator.next();
            TypeAugmentedSemanticNode child = childIterator.next();

            if (!pattern.isMatch(child, e)) {
                return false;
            }
        }

        return patternNodeIterator.hasNext() == childIterator.hasNext();
    }

    public void transform(HashMap<String, TypeAugmentedSemanticNode> variables) {

    }
}
