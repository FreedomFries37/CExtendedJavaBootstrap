package radin.compilation.tags;

import radin.typeanalysis.TypeAugmentedSemanticNode;

public interface ICompilationTag {
    
    boolean canAttachTo(TypeAugmentedSemanticNode node);
    boolean isAttachToAny();
}
