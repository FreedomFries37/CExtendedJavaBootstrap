package radin.core.input.frontend.v2.parsing.structure;

public class Terminal <T> extends ParsableObject<T> {
    
    public Terminal(T backingObject) {
        super(backingObject);
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!(o instanceof Terminal<?>)) {
            return false;
        }
        
        return this.getBackingObject().equals(((Terminal<?>) o).getBackingObject());
    }
}
