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
    
    private String preamble;
    
    public String getPreamble() {
        return preamble;
    }
    
    public void setPreamble(String preamble) {
        this.preamble = preamble;
    }
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        if(preamble != null) {
            println(preamble);
            println();
        }
        try {
            TopLevelDeclarationCompiler topLevelDeclarationCompiler = new TopLevelDeclarationCompiler(getPrintWriter());
            topLevelDeclarationCompiler.compile(node);
            
        }catch (Throwable t) {
            t.printStackTrace();
            flush();
            close();
            return false;
        }
        
        flush();
        close();
        return true;
    }
}
