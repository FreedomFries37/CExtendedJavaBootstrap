package radin.frontend.v2.parsing.structure;

public class LRActionWithLRState <T> extends LRActionRecord<LRState<T>> {
    
    
    public LRActionWithLRState(Action action, LRState<T> nextState, Production production) {
        super(action, nextState, production);
    }
}
