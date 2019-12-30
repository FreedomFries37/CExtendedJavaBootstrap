package radin.compilation;

import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileCompiler extends AbstractCompiler {
    
    public FileCompiler() {
        super(new PrintWriter(System.out));
    }
    
    public FileCompiler(File file) throws FileNotFoundException {
        super(new PrintWriter(file));
    }
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        return false;
    }
}
