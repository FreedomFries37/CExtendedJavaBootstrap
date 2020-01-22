package radin.core.input.frontend.v2.parsing.structure;

import radin.core.utility.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class SLRData <T> {
    
    private List<Production> productionList;
    private Set<Symbol> symbols;
    private Symbol startingSymbol;
    
    private HashSet<Symbol> eps;
    private HashMap<ParsableObject<?>, Set<NonTerminal<T>>> firstSet;
    private HashMap<ParsableObject<?>, Set<NonTerminal<T>>> followSet;
    
    private List<LRState<T>> states;
    private LRState<T> state0;
    
    private int backingNumber;
    private HashMap<LRState<T>, Integer> stateToNumber;
    private HashMap<Pair<ParsableObject<?>, LRState<T>>, LRActionRecord<LRState<T>>> parseTable;
    private T eof;
    
    
    public SLRData(List<Production> productionList, Symbol startingSymbol, T eof) {
        this.productionList = productionList;
        this.startingSymbol = new Symbol(startingSymbol.getBackingObject() + "'");
        this.productionList.add(new Production(this.startingSymbol, Arrays.asList(startingSymbol,
                new NonTerminal<>(eof))));
        this.symbols = new HashSet<>();
        for (Production production : productionList) {
            this.symbols.add(production.getLhs());
        }
        this.eof = eof;
    }
    
    public void generateFirstSets() {
        firstSet = new HashMap<>();
        eps = new HashSet<>();
        for (Symbol symbol : symbols) {
            firstSet.put(symbol, new HashSet<>());
        }
        
        HashMap<ParsableObject<?>, Set<NonTerminal<T>>> firstSet = this.firstSet;
        do {
            this.firstSet = firstSet;
            firstSet = new HashMap<>();
            for (Map.Entry<ParsableObject<?>, Set<NonTerminal<T>>> symbolSetEntry : this.firstSet.entrySet()) {
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
                        NonTerminal<T> tNonTerminal = (NonTerminal<T>) parsableObject;
                        firstSet.put(tNonTerminal, Collections.singleton(tNonTerminal));
                        firstSet.get(s).add(tNonTerminal);
                        continue OUTER;
                    }
                }
                
            }
        } while (!firstSet.equals(this.firstSet));
        
    }
    
    private boolean EPS(ParsableObject<?> o) {
        if(o instanceof NonTerminal<?>) return false;
        return eps.contains((Symbol) o);
    }
    
    private Set<NonTerminal<T>> FIRST(ParsableObject<?> o){
        return firstSet.getOrDefault(o, new HashSet<>());
    }
    
    private Set<NonTerminal<T>> FOLLOW(ParsableObject<?> o){
        return followSet.getOrDefault(o, new HashSet<>());
    }
    
    private boolean stringEps(List<ParsableObject<?>> objects) {
        for (ParsableObject<?> object : objects) {
            if(!EPS(object)) return false;
        }
        return true;
    }
    
    public List<Production> getProductionsForSymbol(Symbol s) {
        return productionList.stream().filter(t -> t.getLhs().equals(s)).collect(Collectors.toList());
    }
    
    private Set<NonTerminal<T>> stringFirst(List<ParsableObject<?>> objects) {
        Set<NonTerminal<T>> output = new HashSet<>();
        for (ParsableObject<?> object : objects) {
            if(object instanceof Symbol) {
                output.addAll(FIRST(object));
                if (!EPS(object)) return output;
            }else {
                output.add((NonTerminal<T>) object);
                return output;
            }
        }
        return output;
    }
    
    
    
    public void generateFollowSet() {
        followSet = new HashMap<>();
        for (ParsableObject<?> object : firstSet.keySet()) {
            followSet.put(object, new HashSet<>());
        }
        
        HashMap<ParsableObject<?>, Set<NonTerminal<T>>> followSet = this.followSet;
        do {
            this.followSet = followSet;
            followSet = new HashMap<>();
            for (Map.Entry<ParsableObject<?>, Set<NonTerminal<T>>> symbolSetEntry : this.followSet.entrySet()) {
                followSet.put(symbolSetEntry.getKey(), new HashSet<>(symbolSetEntry.getValue()));
            }
            
            for (Production production : productionList) {
                List<ParsableObject<?>> rhs = production.getRhs();
                for (int i = 0; i < rhs.size(); i++) {
                    ParsableObject<?> B = rhs.get(i);
                    if(!followSet.containsKey(B)) followSet.put(B, new HashSet<>());
                    if(i < rhs.size() - 1) {
                        List<ParsableObject<?>> beta = rhs.subList(i+1, rhs.size());
                        Set<NonTerminal<T>> stringFirst = stringFirst(beta);
                        followSet.get(B).addAll(stringFirst);
                        if(stringEps(beta)) {
                            followSet.get(B).addAll(followSet.get(production.getLhs()));
                        }
                    } else {
                        followSet.get(B).addAll(followSet.get(production.getLhs()));
                    }
                }
            }
            
            
        }while (!followSet.equals(this.followSet));
        
        states = new LinkedList<>();
        Set<Item<T>> set = new HashSet<>();
        Queue<Symbol> toVisit = new LinkedList<>();
        Set<Symbol> visited = new HashSet<>();
        visited.add(startingSymbol);
        for (Production production : getProductionsForSymbol(startingSymbol)) {
            if(production.getRhs().size() > 0 && production.getRhs().get(0) instanceof Symbol) {
                toVisit.offer(((Symbol) production.getRhs().get(0)));
            }
            Set<NonTerminal<T>> prodFollowSet = FOLLOW(production.getLhs());
            prodFollowSet.add(new NonTerminal<>(eof));
            set.add(new Item<>(production, prodFollowSet));
        }
        
        while (!toVisit.isEmpty()) {
            Symbol symbol = toVisit.poll();
            if(visited.contains(symbol)) continue;
            visited.add(symbol);
        
            for (Production production : getProductionsForSymbol(symbol)) {
                Item<T> item = new Item<>(production, getFollowSet().get(symbol));
                set.add(item);
                if(!item.isFinished() && item.getAtLocation() instanceof Symbol) {
                    toVisit.offer(((Symbol) item.getAtLocation()));
                }
            }
        }
        
        state0 = new LRState<>(set);
        states.add(state0);
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
    
    public HashMap<ParsableObject<?>, Set<NonTerminal<T>>> getFirstSet() {
        return firstSet;
    }
    
    public HashMap<ParsableObject<?>, Set<NonTerminal<T>>> getFollowSet() {
        return followSet;
    }
    
    public void generateParseTable() {
        parseTable = new HashMap<>();
        stateToNumber = new HashMap<>();
        
        Queue<LRState<T>> lrStates = new LinkedList<>();
        lrStates.offer(state0);
        while (!lrStates.isEmpty()) {
            LRState<T> current = lrStates.poll();
            if(!stateToNumber.containsKey(current)) {
                stateToNumber.put(current, backingNumber++);
            }
            if(!states.contains(current)) states.add(current);
            
            for (Pair<ParsableObject<?>, LRActionWithLRState<T>> action : current.getActions(this)) {
                
                Pair<ParsableObject<?>, LRState<T>> key = new Pair<>(action.getVal1(), current);
                parseTable.put(key, action.getVal2());
                if(action.getVal2().getNextState() != null && !states.contains(action.getVal2().getNextState()))
                    lrStates.offer(action.getVal2().getNextState());
            }
            
        }
    }
    
    public LRState<T> getState0() {
        return state0;
    }
    
    public HashMap<LRState<T>, Integer> getStateToNumber() {
        return stateToNumber;
    }
    
    public HashMap<Pair<ParsableObject<?>, LRState<T>>, LRActionRecord<LRState<T>>> getParseTable() {
        return parseTable;
    }
    
    public HashMap<Pair<ParsableObject<?>, Integer>, LRActionRecord<Integer>> transformParseTable() {
        HashMap<Pair<ParsableObject<?>, Integer>, LRActionRecord<Integer>> output = new HashMap<>();
        if(parseTable == null)return null;
        for (Map.Entry<Pair<ParsableObject<?>, LRState<T>>, LRActionRecord<LRState<T>>> pairLRActionRecordEntry : parseTable.entrySet()) {
            output.put(
                    new Pair<>(
                            pairLRActionRecordEntry.getKey().getVal1(),
                            stateToNumber.get(pairLRActionRecordEntry.getKey().getVal2())
                    ),
                    new LRCharacteristicAction(
                            pairLRActionRecordEntry.getValue().action,
                            stateToNumber.get(pairLRActionRecordEntry.getValue().getNextState()),
                            pairLRActionRecordEntry.getValue().production
                    )
            );
        }
        return output;
    }
    
    public void printParseTable() {
        Iterable<Map.Entry<Pair<ParsableObject<?>, Integer>, LRActionRecord<Integer>>> entries =
                transformParseTable().entrySet().stream().sorted(Comparator.comparing(o -> o.getKey().getVal2())).collect(Collectors.toList());
        for (Map.Entry<Pair<ParsableObject<?>, Integer>, LRActionRecord<Integer>> pairLRActionRecordEntry : entries) {
            
            Pair<ParsableObject<?>, ?> key = pairLRActionRecordEntry.getKey();
            System.out.printf("[%s, %s] -> ", key.getVal1(), key.getVal2());
            LRActionRecord<?> value = pairLRActionRecordEntry.getValue();
            System.out.printf("%s %s %s\n", value.action, value.getNextState(),
                    value.production);
        }
    }
}
