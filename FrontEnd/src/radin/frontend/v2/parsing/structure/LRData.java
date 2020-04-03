package radin.frontend.v2.parsing.structure;

import java.util.*;
import java.util.stream.Collectors;

public class LRData<T> {
    
    protected List<Production> productionList;
    protected Set<Symbol> symbols;
    protected Symbol startingSymbol;
    protected HashMap<ParsableObject<?>, Set<Terminal<T>>> firstSet;
    protected T eof;
    private HashSet<Symbol> eps;
    
    public LRData(List<Production> productionList, Symbol startingSymbol, T eof) {
        this.productionList = productionList;
        this.symbols = new HashSet<>();
        this.startingSymbol = new Symbol(startingSymbol.getBackingObject() + "'");
        this.eof = eof;
    }
    
    
    
    public void generateFirstSets() {
        firstSet = new HashMap<>();
        eps = new HashSet<>();
        for (Symbol symbol : symbols) {
            firstSet.put(symbol, new HashSet<>());
        }
        
        HashMap<ParsableObject<?>, Set<Terminal<T>>> firstSet = this.firstSet;
        do {
            this.firstSet = firstSet;
            firstSet = new HashMap<>();
            for (Map.Entry<ParsableObject<?>, Set<Terminal<T>>> symbolSetEntry : this.firstSet.entrySet()) {
                firstSet.put(symbolSetEntry.getKey(), new HashSet<>(symbolSetEntry.getValue()));
            }
            
            OUTER:
            for (Production p : productionList) {
                Symbol s = p.getLhs();
                
                for (ParsableObject<?> parsableObject : p.getRhs()) {
                    if (parsableObject instanceof Symbol) {
                        firstSet.get(s).addAll(firstSet.get(parsableObject));
                        if(!eps.contains(parsableObject)) continue OUTER;
                    } else {
                        Terminal<T> tNonTerminal = (Terminal<T>) parsableObject;
                        firstSet.put(tNonTerminal, Collections.singleton(tNonTerminal));
                        firstSet.get(s).add(tNonTerminal);
                        continue OUTER;
                    }
                }
                
            }
        } while (!firstSet.equals(this.firstSet));
        
    }
    
    private boolean EPS(ParsableObject<?> o) {
        if(o instanceof Terminal<?>) return false;
        return eps.contains(o);
    }
    
    private Set<Terminal<T>> FIRST(ParsableObject<?> o){
        return firstSet.getOrDefault(o, new HashSet<>());
    }
    
    protected boolean stringEps(List<ParsableObject<?>> objects) {
        for (ParsableObject<?> object : objects) {
            if(!EPS(object)) return false;
        }
        return true;
    }
    
    public List<Production> getProductionsForSymbol(Symbol s) {
        return productionList.stream().filter(t -> t.getLhs().equals(s)).collect(Collectors.toList());
    }
    
    protected Set<Terminal<T>> stringFirst(List<ParsableObject<?>> objects) {
        Set<Terminal<T>> output = new HashSet<>();
        for (ParsableObject<?> object : objects) {
            if(object instanceof Symbol) {
                output.addAll(FIRST(object));
                if (!EPS(object)) return output;
            }else {
                output.add((Terminal<T>) object);
                return output;
            }
        }
        return output;
    }
    
    public List<Production> getProductionList() {
        return productionList;
    }
    
    public Set<Symbol> getSymbols() {
        return symbols;
    }
    
    public Symbol getStartingSymbol() {
        return startingSymbol;
    }
    
    public HashSet<Symbol> getEps() {
        return eps;
    }
    
    public HashMap<ParsableObject<?>, Set<Terminal<T>>> getFirstSet() {
        return firstSet;
    }
}
