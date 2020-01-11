package radin.core.output.tags;

import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.ASTNodeType;


import java.util.Arrays;
import java.util.HashSet;

public enum BasicCompilationTag implements ICompilationTag{
    VIRTUAL_METHOD_CALL("VIRTUAL METHOD CALL", ASTNodeType.method_call),
    SHADOWING_FIELD_NAME("SHADOWING FIELD NAME", ASTNodeType.id),
    INDIRECT_METHOD_CALL("INDIRECT METHOD CALL", ASTNodeType.method_call),
    INDIRECT_FIELD_GET("INDIRECT FIELD GET", ASTNodeType.field_get),
    NEW_OBJECT_DEREFERENCE("NEW OBJECT DEREFERENCE", ASTNodeType.indirection),
    COMPILE_AS_FIELD_GET("COMPILE AS FIELD GET", ASTNodeType.method_call),
    OPERATOR_ASSIGNMENT("OPERATOR ASSIGNMENT", ASTNodeType.assignment_type),
    HAS_ELSE("HAS ELSE", ASTNodeType.if_cond),
    VOID_RETURN("VOID RETURN", ASTNodeType._return),
    CONSTANT_SIZE("CONST SIZE", ASTNodeType.declaration)
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
