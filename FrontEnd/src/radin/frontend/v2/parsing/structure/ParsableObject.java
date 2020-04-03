package radin.frontend.v2.parsing.structure;



public abstract class ParsableObject<T> {
    private T backingObject;
    
    public ParsableObject(T backingObject) {
        this.backingObject = backingObject;
    }
    
    public T getBackingObject() {
        return backingObject;
    }
    
    @Override
    public String toString() {
        return backingObject.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ParsableObject<?> that = (ParsableObject<?>) o;
    
        return backingObject.equals(that.backingObject);
    }
    
    @Override
    public int hashCode() {
        return backingObject.hashCode();
    }
}
