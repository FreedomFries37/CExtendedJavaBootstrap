package radin.v2.parsing;

import java.util.List;

public class LALRData<T> {

    private List<Production> productionList;
    private Symbol startingSymbol;
    
    public LALRData(List<Production> productionList, Symbol startingSymbol) {
        this.productionList = productionList;
        this.startingSymbol = startingSymbol;
    }
}
