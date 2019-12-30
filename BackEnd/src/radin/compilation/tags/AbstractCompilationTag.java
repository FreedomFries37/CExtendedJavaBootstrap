package radin.compilation.tags;

import radin.interphase.semantics.ASTNodeType;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.util.Arrays;
import java.util.HashSet;

public abstract class AbstractCompilationTag implements ICompilationTag {
    
    private HashSet<ASTNodeType> validAttachmentPoints;
    private String tagName;
    
    public AbstractCompilationTag(HashSet<ASTNodeType> validAttachmentPoints, String tagName) {
        this.validAttachmentPoints = validAttachmentPoints;
        this.tagName = tagName;
    }
    
    
    public AbstractCompilationTag(String tagName, ASTNodeType... types) {
        validAttachmentPoints = new HashSet<>(Arrays.asList(types));
        this.tagName = tagName;
    }
    
    public boolean canAttachTo(TypeAugmentedSemanticNode node) {
        if(isAttachToAny()) return true;
        return validAttachmentPoints.contains(node.getASTType());
    }
    
    public boolean isAttachToAny() {
        return validAttachmentPoints.isEmpty();
    }
    
    @Override
    public String toString() {
        return tagName;
    }
}
