package radin.compilation;

import radin.compilation.microcompilers.ClassCompiler;
import radin.compilation.microcompilers.ExpressionCompiler;
import radin.compilation.microcompilers.FunctionCompiler;
import radin.compilation.microcompilers.TopLevelDeclarationCompiler;
import radin.compilation.tags.TypeDefHelperTag;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypeAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.semantics.types.methods.CXParameter;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class FileCompiler extends AbstractCompiler {
    
    public FileCompiler() {
        super(new PrintWriter(System.out));
    }
    
    public FileCompiler(File file) throws FileNotFoundException {
        super(new PrintWriter(file));
    }
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        TopLevelDeclarationCompiler topLevelDeclarationCompiler = new TopLevelDeclarationCompiler(getPrintWriter());
        if(!topLevelDeclarationCompiler.compile(node)) return false;
        flush();
        close();
        return true;
    }
}
