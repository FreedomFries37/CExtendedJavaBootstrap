package radin.core.output.backend.compilation;

import radin.core.output.backend.microcompilers.TopLevelDeclarationCompiler;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
