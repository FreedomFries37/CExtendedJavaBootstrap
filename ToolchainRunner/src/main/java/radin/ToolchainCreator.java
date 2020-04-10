package radin;

import radin.core.utility.Reference;

public class ToolchainCreator {
    
    static <T> void swap(Reference<T> a, Reference<T> b) {
        T temp = a.getValue();
        a.setValue(b.getValue());
        b.setValue(temp);
    }
    
    public static void main(String[] args) {
        System.out.println("What is the name of the toolchain you would like to define?");
        
        Reference<Integer> a = new Reference<>(0);
        Reference<Integer> b = new Reference<>(1);
        // ToolchainCreator.<Object>swap(a, b);
    }
}
