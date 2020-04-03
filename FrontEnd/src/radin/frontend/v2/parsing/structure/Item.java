package radin.frontend.v2.parsing.structure;

import java.util.List;
import java.util.Set;

public class Item<T> {
    
    private int location;
    private Production backingProduction;
    private Symbol lhs;
    private List<ParsableObject<?>> rhs;
    private Set<Terminal<T>> lookaheadSet;
    
    private Item(int location, Symbol lhs, List<ParsableObject<?>> rhs,
                 Set<Terminal<T>> lookaheadSet) {
        this(location, lhs, rhs, lookaheadSet, null);
    }
    
    private Item(int location, Symbol lhs, List<ParsableObject<?>> rhs,
                 Set<Terminal<T>> lookaheadSet, Production backingProduction) {
        this.location = location;
        this.lhs = lhs;
        this.rhs = rhs;
        this.lookaheadSet = lookaheadSet;
        this.backingProduction = backingProduction;
    }
    
    public Item(Production production, Set<Terminal<T>> lookaheadSet) {
        this(0, production.getLhs(), production.getRhs(), lookaheadSet, production);
    }
    
    public Production getBackingProduction() {
        return backingProduction;
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
    
    public Set<Terminal<T>> getLookaheadSet() {
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
        return new Item<T>(location + 1, lhs, rhs, lookaheadSet, backingProduction);
    }
    
    public List<ParsableObject<?>> getParsed() {
        return rhs.subList(0, location);
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Item<?> item = (Item<?>) o;
        
        if (location != item.location) return false;
        if (!lhs.equals(item.lhs)) return false;
        if (!rhs.equals(item.rhs)) return false;
        return lookaheadSet.equals(item.lookaheadSet);
    }
    
    @Override
    public int hashCode() {
        int result = location;
        result = 31 * result + lhs.hashCode();
        result = 31 * result + rhs.hashCode();
        result = 31 * result + lookaheadSet.hashCode();
        return result;
    }
}
