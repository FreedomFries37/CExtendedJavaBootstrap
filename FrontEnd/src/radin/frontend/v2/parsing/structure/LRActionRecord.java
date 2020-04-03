package radin.frontend.v2.parsing.structure;

public abstract class LRActionRecord<T> {
    
    protected Action action;
    private T nextState;
    protected Production production;
    
    public LRActionRecord(Action action, T nextState, Production production) {
        this.action = action;
        this.nextState = nextState;
        this.production = production;
    }
    
    public Action getAction() {
        return action;
    }
    
    public T getNextState() {
        return nextState;
    }
    
    public Production getProduction() {
        return production;
    }
    
    @Override
    public String toString() {
        return "LRActionRecord{" +
                "action=" + action +
                ", nextState=" + nextState +
                ", production=" + production +
                '}';
    }
}
