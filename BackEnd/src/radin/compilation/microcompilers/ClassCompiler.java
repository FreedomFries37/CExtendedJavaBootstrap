package radin.compilation.microcompilers;

import radin.compilation.AbstractIndentedOutputSingleOutputCompiler;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXStructType;
import radin.core.semantics.types.methods.CXMethod;

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
        
        CXStructType vTable = cxClassType.getVTable();
        print(vTable.generateCDefinition());
        println(";");
    
        
        print(structEquivalent.generateCDefinition());
        println(";");
        println();
        for (CXMethod cxMethod : cxClassType.getConcreteMethodsOrder()) {
            println(cxMethod.generateCDeclaration());
        }
    
        for (CXMethod cxMethod : cxClassType.getVirtualMethodOrder()) {
            println(cxMethod.generateCDeclaration());
        }
        println();
        for (CXMethod cxMethod : cxClassType.getConcreteMethodsOrder()) {
            if(cxMethod.getMethodBody() != null) {
                MethodCompiler methodCompiler = new MethodCompiler(getPrintWriter(), 0, cxMethod);
                if(!methodCompiler.compile()) return false;
            }
            println();
        }
    
        for (CXMethod cxMethod : cxClassType.getVirtualMethodOrder()) {
            if(cxMethod.getMethodBody() != null) {
                MethodCompiler methodCompiler = new MethodCompiler(getPrintWriter(), 0, cxMethod);
                if(!methodCompiler.compile()) return false;
            }
            println();
        }
    
    
        return true;
    }
}
