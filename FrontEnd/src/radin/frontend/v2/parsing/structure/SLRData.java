package radin.frontend.v2.parsing.structure;

import radin.core.utility.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class SLRData <T> extends LRData<T> {
    
    private HashMap<ParsableObject<?>, Set<Terminal<T>>> followSet;
    
    
    
    private List<LRState<T>> states;
    private LRState<T> state0;
    
    private int backingNumber;
    private HashMap<LRState<T>, Integer> stateToNumber;
    private HashMap<Pair<ParsableObject<?>, LRState<T>>, LRActionRecord<LRState<T>>> parseTable;
    
    
    public SLRData(List<Production> productionList, Symbol startingSymbol, T eof) {
        super(productionList, startingSymbol, eof);
        this.getProductionList().add(new Production(this.getStartingSymbol(), Arrays.asList(startingSymbol,
                new Terminal<>(eof))));
        for (Production production : productionList) {
            this.getSymbols().add(production.getLhs());
        }
    }
    
    private Set<Terminal<T>> FOLLOW(ParsableObject<?> o){
        return followSet.getOrDefault(o, new HashSet<>());
    }
    
    
    public void generateFollowSet() {
        followSet = new HashMap<>();
        for (ParsableObject<?> object : firstSet.keySet()) {
            followSet.put(object, new HashSet<>());
        }
        
        HashMap<ParsableObject<?>, Set<Terminal<T>>> followSet = this.followSet;
        do {
            this.followSet = followSet;
            followSet = new HashMap<>();
            for (Map.Entry<ParsableObject<?>, Set<Terminal<T>>> symbolSetEntry : this.followSet.entrySet()) {
                followSet.put(symbolSetEntry.getKey(), new HashSet<>(symbolSetEntry.getValue()));
            }
            
            for (Production production : getProductionList()) {
                List<ParsableObject<?>> rhs = production.getRhs();
                for (int i = 0; i < rhs.size(); i++) {
                    ParsableObject<?> B = rhs.get(i);
                    if(!followSet.containsKey(B)) followSet.put(B, new HashSet<>());
                    if(i < rhs.size() - 1) {
                        List<ParsableObject<?>> beta = rhs.subList(i+1, rhs.size());
                        Set<Terminal<T>> stringFirst = stringFirst(beta);
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
        visited.add(getStartingSymbol());
        for (Production production : getProductionsForSymbol(getStartingSymbol())) {
            if(production.getRhs().size() > 0 && production.getRhs().get(0) instanceof Symbol) {
                toVisit.offer(((Symbol) production.getRhs().get(0)));
            }
            Set<Terminal<T>> prodFollowSet = FOLLOW(production.getLhs());
            prodFollowSet.add(new Terminal<>(eof));
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
    
    public HashMap<ParsableObject<?>, Set<Terminal<T>>> getFollowSet() {
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
