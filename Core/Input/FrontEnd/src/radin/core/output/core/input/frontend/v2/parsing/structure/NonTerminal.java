package radin.core.output.core.input.frontend.v2.parsing.structure;

public class NonTerminal<T> extends ParsableObject<T> {
    
    public NonTerminal(T backingObject) {
        super(backingObject);
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!(o instanceof NonTerminal<?>)) {
            return false;
        }
        
        return this.getBackingObject().equals(((NonTerminal<?>) o).getBackingObject());
    }
}
