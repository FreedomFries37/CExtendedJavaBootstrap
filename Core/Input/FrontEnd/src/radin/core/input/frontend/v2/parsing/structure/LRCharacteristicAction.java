package radin.core.input.frontend.v2.parsing.structure;

public class LRCharacteristicAction extends LRActionRecord<Integer> {
    
    public LRCharacteristicAction(Action action, Integer nextState, Production production) {
        super(action, nextState, production);
    }
}
