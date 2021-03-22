package radin.midanalysis.typeanalysis.analyzers;

import radin.core.semantics.types.CXIdentifier;
import radin.midanalysis.TypeAugmentedSemanticTree;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.output.typeanalysis.IVariableTypeTracker;
import radin.midanalysis.typeanalysis.TypeAnalyzer;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.types.primitives.PointerType;
import radin.output.tags.BasicCompilationTag;
import radin.output.tags.GenericLocationTag;

import java.util.LinkedList;
import java.util.List;

public class ProgramTypeAnalyzer extends TypeAnalyzer {

    private boolean closure;

    public ProgramTypeAnalyzer(AbstractSyntaxNode program) {
        this(new TypeAugmentedSemanticTree(program, getEnvironment()).getHead());
        assert program.getTreeType() == ASTNodeType.top_level_decs;
    }


    
    public ProgramTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
        this.closure = true;
    }

    public ProgramTypeAnalyzer(TypeAugmentedSemanticNode tree, boolean closure) {
        super(tree);
        this.closure = closure;
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        return determineTypes(node, true);
    }
    
    public boolean determineTypes(TypeAugmentedSemanticNode node, boolean closure) {
        if(closure && this.closure) {
            typeTrackingClosure();
        }
        assert node.getASTType() == ASTNodeType.top_level_decs;
        for (TypeAugmentedSemanticNode child : node.getChildren()) {
            TopLevelDeclarationAnalyzer topLevelDeclarationAnalyzer = new TopLevelDeclarationAnalyzer(child);
            if(!determineTypes(topLevelDeclarationAnalyzer)) {
                node.printTreeForm();
                return false;
            }
        }

        if(closure && this.closure) {
            releaseTrackingClosure();
        }
        return true;
    }
}
