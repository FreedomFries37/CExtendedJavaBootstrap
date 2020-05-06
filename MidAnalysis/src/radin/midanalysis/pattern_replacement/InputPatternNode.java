package radin.midanalysis.pattern_replacement;

import radin.core.AbstractTree;

import java.util.List;

public class InputPatternNode extends AbstractTree<InputPatternNode> {
    
    private InputPatternSingleton singleton;
    private List<InputPatternNode> children;
    
    
    @Override
    public List<InputPatternNode> postfix() {
        return null;
    }
    
    @Override
    public List<InputPatternNode> getDirectChildren() {
        return children;
    }
    
    @Override
    public List<InputPatternNode> getMutableChildren() {
        return children;
    }
}
