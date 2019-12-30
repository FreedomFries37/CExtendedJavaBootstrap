package radin.compilation.microcompilers;

import radin.compilation.AbstractIndentedOutputCompiler;
import radin.compilation.AbstractIndentedOutputSingleOutputCompiler;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.compound.CXStructType;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.PrintWriter;

public class ClassCompiler extends AbstractIndentedOutputSingleOutputCompiler {
    
    private CXClassType cxClassType;
    
    public ClassCompiler(PrintWriter printWriter, int indent, CXClassType cxClassType) {
        super(printWriter, indent);
        this.cxClassType = cxClassType;
    }
    
    @Override
    public boolean compile() {
        cxClassType.generateSuperMethods(getSettings().getvTableName());
    
        CXStructType vTable = cxClassType.getVTable();
        print(vTable.generateCDefinition());
        println(";");
    
        CXStructType structEquivalent = cxClassType.getStructEquivalent();
        print(structEquivalent.generateCDefinition());
        println(";");
    
    
        return true;
    }
}
