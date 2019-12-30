package radin.compilation.microcompilers;

import radin.compilation.AbstractIndentedOutputCompiler;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.PrintWriter;

public class ClassCompiler extends AbstractIndentedOutputCompiler {
    
    private CXClassType cxClassType;
    
    public ClassCompiler(PrintWriter printWriter, int indent, CXClassType cxClassType) {
        super(printWriter, indent);
        this.cxClassType = cxClassType;
    }
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        
        cxClassType.generateSuperMethods(getSettings().getvTableName());
        
        
        
        return false;
    }
}
