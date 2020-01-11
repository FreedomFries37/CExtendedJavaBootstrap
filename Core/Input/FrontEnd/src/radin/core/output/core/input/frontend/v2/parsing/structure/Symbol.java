package radin.core.output.core.input.frontend.v2.parsing.structure;

public class Symbol extends ParsableObject<String> {
    
    public Symbol(String symbolName) {
        super(symbolName);
    }
    
    @Override
    public String toString() {
        return "<" + getBackingObject() + ">";
    }
}
