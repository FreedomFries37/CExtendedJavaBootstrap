package radin.core.output.tags;


import radin.core.output.midanalysis.TypeAugmentedSemanticNode;

public interface ICompilationTag {
    
    boolean canAttachTo(TypeAugmentedSemanticNode node);
    boolean isAttachToAny();
}
