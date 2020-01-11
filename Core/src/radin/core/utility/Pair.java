package radin.core.utility;

import java.util.Objects;

public class Pair<T, R> {
    private T val1;
    private R val2;
    
    public Pair(T val1, R val2) {
        this.val1 = val1;
        this.val2 = val2;
    }
    
    public T getVal1() {
        return val1;
    }
    
    public void setVal1(T val1) {
        this.val1 = val1;
    }
    
    public R getVal2() {
        return val2;
    }
    
    public void setVal2(R val2) {
        this.val2 = val2;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Pair<?, ?> pair = (Pair<?, ?>) o;
        
        if (!Objects.equals(val1, pair.val1)) return false;
        return Objects.equals(val2, pair.val2);
    }
    
    @Override
    public int hashCode() {
        int result = val1 != null ? val1.hashCode() : 0;
        result = 31 * result + (val2 != null ? val2.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "{" +
                val1 +
                ", " +
                val2 +
                '}';
    }
    
    public static <T, R> Pair<T, R> to(T o1, R o2) {
        return new Pair<>(o1, o2);
    }
}
