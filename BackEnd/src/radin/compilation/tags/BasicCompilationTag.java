package radin.compilation.tags;

import radin.interphase.semantics.ASTNodeType;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.util.Arrays;
import java.util.HashSet;

public enum BasicCompilationTag implements ICompilationTag{
    VIRTUAL_METHOD_CALL("VIRTUAL METHOD CALL", ASTNodeType.method_call),
    SHADOWING_FIELD_NAME("SHADOWING FIELD NAME", ASTNodeType.id)
    ;
    
    private HashSet<ASTNodeType> validAttachmentPoints;
    private String tagName;
    
    BasicCompilationTag(HashSet<ASTNodeType> validAttachmentPoints, String tagName) {
        this.validAttachmentPoints = validAttachmentPoints;
        this.tagName = tagName;
    }
    
    
    BasicCompilationTag(String tagName, ASTNodeType... types) {
        validAttachmentPoints = new HashSet<>(Arrays.asList(types));
        this.tagName = tagName;
    }
    
    public boolean canAttachTo(TypeAugmentedSemanticNode node) {
        if(isAttachToAny()) return true;
        return validAttachmentPoints.contains(node.getASTType());
    }
    
    public boolean isAttachToAny() {
        return !validAttachmentPoints.isEmpty();
    }
    
    @Override
    public String toString() {
        return tagName;
    }
}
