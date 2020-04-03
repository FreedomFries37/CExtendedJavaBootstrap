package radin.interpreter;

import radin.core.SymbolTable;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.types.CXIdentifier;
import radin.core.utility.Pair;

public class ProgramSymbolTable extends SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> {
    
    private SymbolTable<Pair<CXIdentifier, Integer>, TypeAugmentedSemanticNode> methodTable;
    
    public ProgramSymbolTable() {
    
    }
    
    
}
