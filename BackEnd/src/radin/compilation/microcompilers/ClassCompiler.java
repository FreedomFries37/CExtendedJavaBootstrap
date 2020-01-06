package radin.compilation.microcompilers;

import radin.compilation.AbstractIndentedOutputSingleOutputCompiler;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXStructType;

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
        CXStructType structEquivalent = cxClassType.getStructEquivalent();
        print("typedef ");
        print(structEquivalent.getTypeIndirection().generateCDefinition(cxClassType.getTypeName()));
        println(";");
        
        CXStructType vTable = cxClassType.getVTable();
        print(vTable.generateCDefinition());
        println(";");
    
        
        print(structEquivalent.generateCDefinition());
        println(";");
    
    
        return true;
    }
}
