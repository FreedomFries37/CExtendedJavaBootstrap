package radin.compilation.tags;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.util.List;

public class PriorConstructorTag extends AbstractCompilationTag {
    
    private CXConstructor priorConstructor;
    private TypeAugmentedSemanticNode sequence;
    
    public PriorConstructorTag(CXConstructor priorConstructor, TypeAugmentedSemanticNode sequence) {
        super("PRIOR CONSTRUCTOR CALL", ASTNodeType.constructor_definition);
        this.priorConstructor = priorConstructor;
        this.sequence = sequence;
    }
    
    public CXConstructor getPriorConstructor() {
        return priorConstructor;
    }
    
    public TypeAugmentedSemanticNode getSequence() {
        return sequence;
    }
}
