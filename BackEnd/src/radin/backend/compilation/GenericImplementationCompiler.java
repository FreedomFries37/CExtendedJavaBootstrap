package radin.backend.compilation;

import radin.backend.microcompilers.FunctionCompiler;
import radin.backend.microcompilers.IndentPrintWriter;
import radin.backend.microcompilers.TopLevelDeclarationCompiler;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.generics.CXGenericFunction;
import radin.core.utility.UniversalCompilerSettings;
import radin.midanalysis.GenericModule;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.TypeAugmentedSemanticTree;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import static radin.core.utility.ICompilationSettings.createBuildFile;

public class GenericImplementationCompiler extends AbstractIndentedOutputSingleOutputCompiler {
    
    private GenericModule genericModule;
    
    public GenericImplementationCompiler(IndentPrintWriter printWriter, GenericModule genericModule) throws IOException {
        super(printWriter, 0);
        this.genericModule = genericModule;
    }
    
    @Override
    public boolean compile() {
        List<TypeAugmentedSemanticNode> children = new LinkedList<>();
        for (CXGenericFunction registeredGenericFunction : genericModule.getRegisteredGenericFunctions()) {
            for (AbstractSyntaxNode createdTree : registeredGenericFunction.getCreatedTrees()) {
                TypeAugmentedSemanticNode node = TypeAugmentedSemanticTree.convertAST(createdTree, registeredGenericFunction.getEnvironment());
    
                children.add(node);
            }
        }
        
        TypeAugmentedSemanticNode functions = new TypeAugmentedSemanticNode(
                new AbstractSyntaxNode(
                        ASTNodeType.top_level_decs
                )
        );
        
        functions.getMutableChildren().addAll(children);
    
        TopLevelDeclarationCompiler compiler = new TopLevelDeclarationCompiler(getPrintWriter());
        return compiler.compile(functions);
    }
    
    @Override
    protected void setIndent(int indent) {
        super.setIndent(indent);
        setPrintWriter(new IndentPrintWriter(getPrintWriter(), getIndent(), UniversalCompilerSettings.getInstance().getSettings().getIndent()));
    }
}
