package radin.backend.microcompilers;

import radin.backend.compilation.AbstractIndentedOutputSingleOutputCompiler;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.methods.CXParameter;
import radin.output.typeanalysis.errors.IncorrectlyMissingCompoundStatement;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;

import java.io.PrintWriter;
import java.util.List;

public class FunctionCompiler extends AbstractIndentedOutputSingleOutputCompiler {
    
    private String name;
    private CXType returnType;
    private List<CXParameter> parameters;
    private TypeAugmentedSemanticNode compoundStatement;
    public static TypeEnvironment environment;
    
    
    public FunctionCompiler(PrintWriter printWriter, int indent, String name, CXType returnType, List<CXParameter> parameters, TypeAugmentedSemanticNode compoundStatement) {
        super(printWriter, indent);
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.compoundStatement = compoundStatement;
    }
    
    public boolean compile() {
        ICompilationSettings.debugLog.finest("Compiling " + returnType.generateCDeclaration(name));
       
        boolean isGetClass = false;
        
        /*
        if(name.equals("__get_class")) {
            isGetClass = true;
            ICompilationSettings.debugLog.info("__get_class Function found, will add dynamic class id table");
        }
         */
        
        if(name.equals("main") && UniversalCompilerSettings.getInstance().getSettings().isLookForMainFunction()) {
            print(returnType.generateCDeclaration("__main"));
        } else {
            print(returnType.generateCDeclaration(name));
        }
        print("(");
        boolean first= true;
        for (CXParameter parameter : parameters) {
            if(first) first = false;
            else print(", ");
            print(parameter.toString());
        }
        print(") ");
        println("{");
        if(!isGetClass) {
            CompoundStatementCompiler compoundStatementCompiler = new CompoundStatementCompiler(getPrintWriter(),
                    getIndent() + 1);
            if (compoundStatement == null) throw new IncorrectlyMissingCompoundStatement();
            if (!compoundStatementCompiler.compile(compoundStatement)) return false;
        } else {
            setIndent(getIndent() + 1);
            println("switch(id) {");
            for (CXClassType createdClass : environment.getCreatedClasses()) {
                int id = environment.getTypeId(createdClass);
                println("case " + id + ": { // " + createdClass);
                println("static Class");
                println("}");
                println("break;");
            }
            println("default: break;");
            println("}");
            setIndent(getIndent() - 1);
        }
        
        println("}");
        println();
        return true;
    }
    
    protected String getName() {
        return name;
    }
    
    protected CXType getReturnType() {
        return returnType;
    }
    
    protected List<CXParameter> getParameters() {
        return parameters;
    }
    
    protected TypeAugmentedSemanticNode getCompoundStatement() {
        return compoundStatement;
    }
}
