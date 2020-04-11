package radin;

import radin.core.utility.Reference;
import radin.frontend.v1.parsing.Parser;

public class ToolchainCreator {
    
    static <T> void swap(Reference<T> a, Reference<T> b) {
        T temp = a.getValue();
        a.setValue(b.getValue());
        b.setValue(temp);
    }
    
    static class Parent {
    
    }
    
    static class Child extends Parent {
    
    }
    
    public static void main(String[] args) {
        System.out.println("What is the name of the toolchain you would like to define?");
        
        Reference<Integer> a = new Reference<>(0);
        Reference<Integer> b = new Reference<>(1);
        // ToolchainCreator.<Object>swap(a, b);
        Reference<? extends Parent> p = new Reference<>(new Parent());
        Reference<? extends Child> c = new Reference<>(new Child());
        
       
        p = c;
    }
}
