package radin.v2.parsing;



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
}
