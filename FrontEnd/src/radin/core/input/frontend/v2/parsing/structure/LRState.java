package radin.core.input.frontend.v2.parsing.structure;

import radin.core.utility.Pair;

import java.util.*;

public class LRState<T> {
    
    private Set<Item<T>> items;
    
    public LRState(Set<Item<T>> items) {
        this.items = items;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        LRState<?> lrState = (LRState<?>) o;
        
        return items.equals(lrState.items);
    }
    
    @Override
    public int hashCode() {
        return items.hashCode();
    }
    
    public List<ParsableObject<?>> getAllAtLocation() {
        List<ParsableObject<?>> out = new LinkedList<>();
        
        for (Item<T> item : items) {
            out.add(item.getAtLocation());
        }
        
        return out;
    }
    
    public LRState<T> getNextState(ParsableObject<?> object, SLRData<T> slrData) {
        Set<Item<T>> nextItems = new HashSet<>();
        Set<Symbol> toAdd = new HashSet<>();
        for (Item<T> item : items) {
            if (!item.isFinished() && item.getAtLocation().equals(object)) {
                Item<T> nextItem = item.getNextItem();
                nextItems.add(nextItem);
                if (!nextItem.isFinished() && nextItem.getAtLocation() instanceof Symbol) {
                    Symbol nextItemAtLocation = (Symbol) nextItem.getAtLocation();
                    /*
                    for (Production production : slrData.getProductionsForSymbol(nextItemAtLocation)) {
                        nextItems.add(new Item<>(production, slrData.getFirstSet().get(nextItemAtLocation)));
                    }
                    
                     */
                    toAdd.add(nextItemAtLocation);
                }
            }
        }
        
        Queue<Symbol> toVisit = new LinkedList<>(toAdd);
        Set<Symbol> visited = new HashSet<>();
        while (!toVisit.isEmpty()) {
            Symbol symbol = toVisit.poll();
            if(visited.contains(symbol)) continue;
            visited.add(symbol);
            
            for (Production production : slrData.getProductionsForSymbol(symbol)) {
                Item<T> item = new Item<>(production, slrData.getFollowSet().get(symbol));
                
                if(!item.isFinished() && item.getAtLocation() instanceof Symbol) {
                    toVisit.offer(((Symbol) item.getAtLocation()));
                    nextItems.add(item);
                } /*else if(!item.isFinished() && item.getAtLocation().equals(object)) {
                    nextItems.add(item.getNextItem());
                } */else {
                    nextItems.add(item);
                }
            }
        }
        
        return new LRState<>(nextItems);
    }
    
    public List<Pair<ParsableObject<?>, LRActionWithLRState<T>>> getActions(SLRData<T> slrData) {
        List<Pair<ParsableObject<?>, LRActionWithLRState<T>>> output = new LinkedList<>();
        Set<List<ParsableObject<?>>> reducedSet = new HashSet<>();
        for (Item<T> item : items) {
            if (item.isFinished()) {
                LRActionWithLRState<T> objectLRActionWithLRState = new LRActionWithLRState<>(Action.REDUCE, null,
                        item.getBackingProduction());
                for (Terminal<T> tNonTerminal : item.getLookaheadSet()) {
                    output.add(new Pair<>(tNonTerminal, objectLRActionWithLRState));
                    List<ParsableObject<?>> reduced = new LinkedList<>(item.getRhs());
                    reduced.add(tNonTerminal);
                    reducedSet.add(reduced);
                }
            }
        }
        
        for (Item<T> item : items) {
            if(!item.isFinished()) {
                ParsableObject<?> atLocation = item.getAtLocation();
                List<ParsableObject<?>> parsed = new LinkedList<>(item.getParsed());
                parsed.add(atLocation);
                
                /*
                if(reducedSet.contains(parsed) && parsed.size() != item.getRhs().size()) throw new
                IllegalStateException(
                
                        "Shift" +
                        "-Reduce Conflict: " + parsed +
                        " out of " + item);

               
                */
                LRState<T> nextState = getNextState(atLocation, slrData);
                
                LRActionWithLRState<T> action = new LRActionWithLRState<>(Action.SHIFT, nextState,
                        item.getBackingProduction());
                
                if(output.stream().noneMatch(o -> o.getVal1().equals(atLocation)))
                    output.add(new Pair<>(atLocation, action));
                
                
            }
        }
        
        return output;
    }
}
