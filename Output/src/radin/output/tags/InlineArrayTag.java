package radin.output.tags;

import radin.core.semantics.ASTNodeType;

public class InlineArrayTag extends AbstractCompilationTag {
    
    private final int size;
    
    public InlineArrayTag(int size) {
        super("INLINE ARRAY", ASTNodeType.inline_array);
        this.size = size;
    }
    
    public int getSize() {
        return size;
    }
}
