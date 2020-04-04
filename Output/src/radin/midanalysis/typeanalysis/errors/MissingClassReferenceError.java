package radin.midanalysis.typeanalysis.errors;

import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXIdentifier;

/**
 * Should be caught, is used for {@code using} statements
 */
public class MissingClassReferenceError extends Error {
    private CXIdentifier missingClassIdentifier;
    private AbstractSyntaxNode usingTree;
    
    public MissingClassReferenceError(CXIdentifier missingClassIdentifier, AbstractSyntaxNode usingTree) {
        super("Missing " + missingClassIdentifier + "; waiting for input");
        this.missingClassIdentifier = missingClassIdentifier;
        this.usingTree = usingTree;
    }
    
    /**
     * Gets the identifier that the using statement is searching for
     * @return an identifier
     */
    public CXIdentifier getMissingClassIdentifier() {
        return missingClassIdentifier;
    }
    
    /**
     * Gets the Abstract Syntax Tree that corresponds to the Using Statement
     * @return the tree
     */
    public AbstractSyntaxNode getUsingTree() {
        return usingTree;
    }
}
