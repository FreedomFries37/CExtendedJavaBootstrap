package radin.compilation.microcompilers;

import radin.compilation.AbstractIndentedOutputCompiler;
import radin.compilation.AbstractIndentedOutputSingleOutputCompiler;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.interphase.semantics.types.methods.CXParameter;
import radin.typeanalysis.TypeAugmentedSemanticNode;

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
        print(returnType.generateCDefinition(name));
        print("(");
        boolean first= true;
        for (CXParameter parameter : parameters) {
            if(first) first = false;
            else print(", ");
            print(parameter.toString());
        }
        println(")");
        println("{");
        CompoundStatementCompiler compoundStatementCompiler = new CompoundStatementCompiler(getPrintWriter(),
                getIndent() + 1);
        compoundStatementCompiler.compile(compoundStatement);
        println("}");
        return true;
    }
    
}
