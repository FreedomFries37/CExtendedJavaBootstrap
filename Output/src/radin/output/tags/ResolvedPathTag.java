package radin.output.tags;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXIdentifier;

public class ResolvedPathTag extends AbstractCompilationTag  {

    private final CXIdentifier absolutePath;

    public ResolvedPathTag(CXIdentifier absolutePath) {
        super("RESOLVED(" +absolutePath.toString() + ")", ASTNodeType.namespaced_id, ASTNodeType.id);
        this.absolutePath = absolutePath;
    }

    public CXIdentifier getAbsolutePath() {
        return absolutePath;
    }
}
