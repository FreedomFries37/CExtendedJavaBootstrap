package radin.midanalysis.typeanalysis.analyzers;

import radin.core.lexical.Token;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.semantics.types.primitives.PointerType;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.output.typeanalysis.IVariableTypeTracker;
import radin.output.typeanalysis.TypeAnalyzer;

import java.util.LinkedList;
import java.util.List;

public class InNamespaceTypeAnalyzer extends TypeAnalyzer {
    public InNamespaceTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }

    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        AbstractSyntaxNode identityNode = node.getASTChild(ASTNodeType.id).getASTNode();
        Token token = identityNode.getToken();
        CXIdentifier namespace = new CXIdentifier(token);

        getCurrentTracker().enterNamespace(namespace);
        TypeAugmentedSemanticNode within = node.getChild(1);
        TopLevelDeclarationAnalyzer analyzer = new TopLevelDeclarationAnalyzer(within);
        if(!determineTypes(analyzer)) return false;
        getCurrentTracker().exitNamespace();
        return true;
    }
}
