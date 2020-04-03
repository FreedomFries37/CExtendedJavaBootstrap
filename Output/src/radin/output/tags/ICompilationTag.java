package radin.output.tags;


import radin.midanalysis.TypeAugmentedSemanticNode;

public interface ICompilationTag {
    
    boolean canAttachTo(TypeAugmentedSemanticNode node);
    boolean isAttachToAny();
}
