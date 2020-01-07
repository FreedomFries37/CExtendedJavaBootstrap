package radin.compilation.microcompilers;

import radin.compilation.AbstractIndentedOutputSingleOutputCompiler;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.methods.CXParameter;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.errors.IncorrectlyMissingCompoundStatement;

import java.io.PrintWriter;
import java.util.List;

public class FunctionCompiler extends AbstractIndentedOutputSingleOutputCompiler {

    private String name;
    private CXType returnType;
    private List<CXParameter> parameters;
    private TypeAugmentedSemanticNode compoundStatement;
    
    public FunctionCompiler(PrintWriter printWriter, int indent, String name, CXType returnType, List<CXParameter> parameters, TypeAugmentedSemanticNode compoundStatement) {
        super(printWriter, indent);
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.compoundStatement = compoundStatement;
    }
    
    public boolean compile() {
        println();
        print(returnType.generateCDefinition(name));
        print("(");
        boolean first= true;
        for (CXParameter parameter : parameters) {
            if(first) first = false;
            else print(", ");
            print(parameter.toString());
        }
        print(") ");
        println("{");
        CompoundStatementCompiler compoundStatementCompiler = new CompoundStatementCompiler(getPrintWriter(),
                getIndent() + 1);
        if(compoundStatement == null) throw new IncorrectlyMissingCompoundStatement();
        compoundStatementCompiler.compile(compoundStatement);
        println("}");
        println();
        return true;
    }
    
}
