package radin.v2.parsing;

import java.util.*;

/**
 * GrammarBuilder for a LALR(1) parser
 * @param <T> backing type of the non-terminals
 */
public class GrammarBuilder <T>  {
    public class NonTerminal extends ParsableObject<T> {
        
        public NonTerminal(T backingObject) {
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
    
    public NonTerminal nonTerminal(T o) {
        return new NonTerminal(o);
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
            } else if(NonTerminal.class.isInstance(rh)) {
                arr[index] = (NonTerminal) rh;
            } else {
                arr[index] = nonTerminal((T) rh);
            }
            
            index++;
        }
        
        addProduction(lhs, arr);
    }
    
    public void addProduction(Symbol lhs, ParsableObject<?>... rhs) {
        productions.add(new Production(lhs, Arrays.asList(rhs)));
    }
    
    public LALRData<T> toData() {
        return new LALRData<>(productions, startingSymbol);
    }
    
}
