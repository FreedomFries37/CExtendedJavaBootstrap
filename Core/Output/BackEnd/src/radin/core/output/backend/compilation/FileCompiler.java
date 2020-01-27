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
        super(new PrintWriter(getCreatedFile(file)));
    }
    
    @Override
    public <V> void setVariable(String variable, V value) {
        if(variable.equals("file")) {
            File f;
            if(value instanceof String) {
                f = getCreatedFile(new File((String) value));
            } else if(value instanceof File) {
                f = getCreatedFile((File) value);
            } else {
                throw new UnsupportedOperationException();
            }
           
           
            try {
                setPrintWriter(new PrintWriter(f));
            } catch (FileNotFoundException ignore) {
            }
        }
        
    }
    
    private static File getCreatedFile(File value) {
        var nextName = value.getName();
        if(nextName.endsWith(".cx")) {
            nextName = nextName.substring(0, nextName.length() - 3) + ".c";
        } else {
            nextName += ".c";
        }
        return new File(nextName);
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
