package radin.core.output.backend.interpreter;

import radin.core.SymbolTable;
import radin.core.chaining.IToolChain;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.types.CXIdentifier;

import java.util.List;

public class SymbolTableCreator implements IToolChain<TypeAugmentedSemanticNode, SymbolTable<CXIdentifier,
        TypeAugmentedSemanticNode>> {
    
    
    @Override
    public SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> invoke(TypeAugmentedSemanticNode input) {
        return null;
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return null;
    }
}
