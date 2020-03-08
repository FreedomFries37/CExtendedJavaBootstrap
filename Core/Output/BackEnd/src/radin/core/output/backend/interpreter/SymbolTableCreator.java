package radin.core.output.backend.interpreter;

import radin.core.SymbolTable;
import radin.core.chaining.IToolChain;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.CXIdentifier;

import java.util.LinkedList;
import java.util.List;

public class SymbolTableCreator implements IToolChain<TypeAugmentedSemanticNode, SymbolTable<CXIdentifier,
        TypeAugmentedSemanticNode>> {
    
    private List<AbstractCompilationError> errors = new LinkedList<>();
    private String file;
    
    @Override
    public <V> void setVariable(String variable, V value) {
        switch (variable) {
            case "file": {
                this.file = (String) value;
                break;
            }
        }
        
    }
    
    @Override
    public SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> invoke(TypeAugmentedSemanticNode input) {
        SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> output = new SymbolTable<>();
        List<TypeAugmentedSemanticNode> functionDefinitions = input.getAllChildren(ASTNodeType.function_definition);
        
        // Creates symbols for all functions
        for (TypeAugmentedSemanticNode functionDefinition : functionDefinitions) {
            output.put(
                    output.new Key(new CXIdentifier(functionDefinition.getASTChild(ASTNodeType.id).getToken(), false),
                            file, functionDefinition.findFirstToken()),
                    functionDefinition
            );
        }
        
        return output;
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return errors;
    }
}
