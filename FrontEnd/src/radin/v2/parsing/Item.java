package radin.v2.parsing;

import java.util.List;
import java.util.Set;

public class Item<T> {
    
    private int location;
    private Symbol lhs;
    private List<ParsableObject<?>> rhs;
    private Set<GrammarBuilder<T>.NonTerminal> lookaheadSet;
    
    public Item(int location, Symbol lhs, List<ParsableObject<?>> rhs,
                Set<GrammarBuilder<T>.NonTerminal> lookaheadSet) {
        this.location = location;
        this.lhs = lhs;
        this.rhs = rhs;
        this.lookaheadSet = lookaheadSet;
    }
    
    public Item(Production production, Set<GrammarBuilder<T>.NonTerminal> lookaheadSet) {
        this(0, production.getLhs(), production.getRhs(), lookaheadSet);
    }
    
    public int getLocation() {
        return location;
    }
    
    public Symbol getLhs() {
        return lhs;
    }
    
    public List<ParsableObject<?>> getRhs() {
        return rhs;
    }
    
    public Set<GrammarBuilder<T>.NonTerminal> getLookaheadSet() {
        return lookaheadSet;
    }
    
    public boolean isFinished() {
        return location == rhs.size();
    }
    
    public ParsableObject<?> getAtLocation() {
        return rhs.get(location);
    }
    
    public Item<T> getNextItem() {
        if(location == rhs.size()) return null;
        return new Item<T>(location + 1, lhs, rhs, lookaheadSet);
    }
    
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("[" + lhs + " -> ");
        for (int i = 0; i < rhs.size(); i++) {
            if(i == location) output.append("*");
            if(i > 0) output.append(" ");
            output.append(rhs.get(i).toString());
        }
        output.append(", ").append(lookaheadSet).append("]");
        return output.toString();
    }
}
