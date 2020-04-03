package radin.backend.microcompilers;

import radin.midanalysis.MethodTASNTracker;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.output.typeanalysis.errors.IncorrectlyMissingCompoundStatement;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.CXParameter;
import radin.core.utility.UniversalCompilerSettings;

import java.io.PrintWriter;


public class MethodCompiler extends FunctionCompiler {
    
    private CXClassType owner;
    private CXMethod original;
    private CXClassType parent;
    
    public MethodCompiler(PrintWriter writer, int indent, CXMethod method) {
        super(writer, indent, method.getCFunctionName(), method.getReturnType(), method.getParametersExpanded(),
                MethodTASNTracker.getInstance().get(method));
        this.owner = method.getParent();
        this.parent = owner.getParent();
        this.original = method;
    }
    
    public MethodCompiler(PrintWriter writer, int indent, CXMethod method, TypeAugmentedSemanticNode cs) {
        super(writer, indent, method.getCFunctionName(), method.getReturnType(), method.getParametersExpanded(),
                cs);
        this.owner = method.getParent();
        this.parent = owner.getParent();
        this.original = method;
    }
    
    @Override
    public boolean compile() {
        println("// " + original);
        print(getReturnType().generateCDeclaration(getName()));
        print("(");
        boolean first= true;
        for (CXParameter parameter : getParameters()) {
            if(first) first = false;
            else print(", ");
            print(parameter.toString());
        }
        print(") ");
        
        println("{");
        setIndent(getIndent() + 1);
        print(owner.toPointer().generateCDeclaration("this"));
        println(" = " + "__this;");
        if(parent != null) {
            print(parent.toPointer().generateCDeclaration("super"));
            println(" = " + "__this;");
        }
        setIndent(getIndent() - 1);
        CompoundStatementCompiler compoundStatementCompiler = new CompoundStatementCompiler(getPrintWriter(),
                getIndent() + 1);
        if(getCompoundStatement() == null) throw new IncorrectlyMissingCompoundStatement();
        if(!compoundStatementCompiler.compile(getCompoundStatement())) return false;
        println("}");
        println();
        return true;
    }
    
    @Override
    protected void setIndent(int indent) {
        super.setIndent(indent);
        setPrintWriter(new IndentPrintWriter(getPrintWriter(), getIndent(), UniversalCompilerSettings.getInstance().getSettings().getIndent()));
    }
}