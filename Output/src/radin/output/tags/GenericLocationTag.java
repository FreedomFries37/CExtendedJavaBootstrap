package radin.output.tags;

import radin.core.semantics.ASTNodeType;

import java.util.HashSet;

public class GenericLocationTag extends AbstractCompilationTag {
    
    public GenericLocationTag() {
        super("GENERIC LOCATOR", ASTNodeType.generic);
    }
}
