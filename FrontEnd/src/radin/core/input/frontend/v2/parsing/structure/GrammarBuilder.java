package radin.core.input.frontend.v2.parsing.structure;

import java.util.*;

/**
 * GrammarBuilder for a LALR(1) parser
 * @param <T> backing type of the non-terminals
 */
public class GrammarBuilder <T>  {
    
    private class GrammarBuilderTerminal extends Terminal<T> {
    
        public GrammarBuilderTerminal(T backingObject) {
            super(backingObject);
        }
    }
    
    private List<Production> productions;
    private HashMap<String, Symbol> symbols;
    private Symbol startingSymbol;
    
    public GrammarBuilder() {
        this.productions = new LinkedList<>();
        symbols = new HashMap<>();
    }
    
    public GrammarBuilder(GrammarBuilder<? extends T> inherit) {
        this.productions = new LinkedList<>(inherit.productions);
        symbols = new HashMap<>(inherit.symbols);
    }
    
    public Symbol symbol(String s) {
        if(symbols.containsKey(s)) return symbols.get(s);
        Symbol symbol = new Symbol(s);
        symbols.put(s, symbol);
        return symbol;
    }
    
    public void inherit(GrammarBuilder<? extends T> other) {
        productions.addAll(other.productions);
        symbols.putAll(other.symbols);
    }
    
    public GrammarBuilderTerminal terminal(T o) {
        return new GrammarBuilderTerminal(o);
    }
    
    public void setStartingSymbol(Symbol startingSymbol) {
        this.startingSymbol = startingSymbol;
    }
    
    
    
    public void addProduction(String lhs, Object... rhs) {
        addProduction(symbol(lhs), rhs);
    }
    
    public void addProduction(Symbol lhs, Object... rhs) {
        ParsableObject<?>[] arr = new ParsableObject<?>[rhs.length];
        int index = 0;
        for (Object rh : rhs) {
            
            if(rh instanceof String && symbols.containsKey(rh)) {
                arr[index] = symbol((String) rh);
            } else if(rh instanceof Symbol) {
                arr[index] = (Symbol) rh;
            } else if(rh instanceof Terminal) {
                arr[index] = (Terminal<T>) rh;
            } else {
                arr[index] = terminal((T) rh);
            }
            
            index++;
        }
        
        addProduction(lhs, arr);
    }
    
    public LRData<T> toNormalData(T eof) {
        return new LRData<>(productions, startingSymbol, eof);
    }
    
    public void addProduction(Symbol lhs, ParsableObject<?>... rhs) {
        productions.add(new Production(lhs, Arrays.asList(rhs)));
    }
    
    public SLRData<T> toData(T eof) {
        return new SLRData<>(productions, startingSymbol, eof);
    }
    
    public boolean valid() {
        Set<Symbol> discovered = new HashSet<>();
        for (Production production : productions) {
            discovered.add(production.getLhs());
        }
        return symbols.values().containsAll(discovered) &&
                discovered.containsAll(symbols.values()) && startingSymbol != null;
    }
    
    public Set<Symbol> invalidSymbols() {
        Set<Symbol> discovered = new HashSet<>();
        for (Production production : productions) {
            discovered.add(production.getLhs());
        }
        // discovered.removeIf(symbol -> symbols.containsValue(symbol));
        Set<Symbol> values = new HashSet<>(symbols.values());
        values.removeIf(discovered::contains);
        return values;
    }
    
    public void print() {
        for (Production production : productions) {
            System.out.println(production);
        }
    }
}
