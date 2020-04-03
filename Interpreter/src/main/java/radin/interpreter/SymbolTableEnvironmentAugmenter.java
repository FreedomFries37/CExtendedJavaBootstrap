package radin.interpreter;

import radin.core.SymbolTable;
import radin.core.chaining.IToolChain;
import radin.core.errorhandling.AbstractCompilationError;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;

import java.util.LinkedList;
import java.util.List;

public class SymbolTableEnvironmentAugmenter implements IToolChain<SymbolTable<CXIdentifier,
        TypeAugmentedSemanticNode>, SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>> {
    
    private TypeEnvironment environment;
    
    public SymbolTableEnvironmentAugmenter(TypeEnvironment environment) {
        this.environment = environment;
    }
    
    @Override
    public SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> invoke(SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> input) {
        
        
        return input;
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return new LinkedList<>();
    }
}
