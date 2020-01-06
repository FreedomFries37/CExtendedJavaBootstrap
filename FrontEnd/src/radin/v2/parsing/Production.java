package radin.v2.parsing;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Production {
    private Symbol lhs;
    private List<ParsableObject<?>> rhs;
    
    public Production(Symbol lhs, List<ParsableObject<?>> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public Symbol getLhs() {
        return lhs;
    }
    
    public List<ParsableObject<?>> getRhs() {
        return rhs;
    }
    
    @Override
    public String toString() {
        return lhs.toString() + " -> " + rhs.stream().map(ParsableObject::toString).collect(Collectors.joining(" "));
    }
}
