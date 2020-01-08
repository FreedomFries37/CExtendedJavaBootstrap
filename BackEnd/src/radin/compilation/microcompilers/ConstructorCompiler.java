package radin.compilation.microcompilers;

import radin.compilation.AbstractIndentedOutputSingleOutputCompiler;
import radin.compilation.tags.PriorConstructorTag;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXParameter;
import radin.core.semantics.types.wrapped.PointerType;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.analyzers.CompoundStatementTypeAnalyzer;

import java.io.PrintWriter;


public class ConstructorCompiler extends AbstractIndentedOutputSingleOutputCompiler {
    
    private CXConstructor constructor;
    private TypeAugmentedSemanticNode body;
    private PriorConstructorTag tag;
    
    public ConstructorCompiler(PrintWriter printWriter, CXConstructor constructor, TypeAugmentedSemanticNode body) {
        super(printWriter, 0);
        this.body = body;
        this.constructor = constructor;
    }
    
    public ConstructorCompiler(PrintWriter printWriter, CXConstructor constructor, TypeAugmentedSemanticNode body, PriorConstructorTag tag) {
        super(printWriter, 0);
        this.constructor = constructor;
        this.body = body;
        this.tag = tag;
    }
    
    @Override
    public boolean compile() {
        println();
        print(constructor.getReturnType().generateCDefinition(constructor.getCFunctionName()));
        print("(");
        boolean first= true;
        String firstParamName = "";
        for (CXParameter parameter : constructor.getParametersExpanded()) {
            if(first) {
                firstParamName = parameter.getName();
                first = false;
            }
            else print(", ");
            print(parameter.toString());
        }
        print(") ");
        println("{");
        if(tag != null) {
            setIndent(getIndent() + 1);
            String call;
            if(tag.getPriorConstructor().getParameters().size() == 0) {
                call = tag.getPriorConstructor().methodAsFunctionCall(firstParamName);
            } else {
                ExpressionCompiler expressionCompiler = new ExpressionCompiler(getPrintWriter());
                String sequence = expressionCompiler.compileToString(tag.getSequence());
                if(sequence == null) return false;
                call = tag.getPriorConstructor().methodAsFunctionCall(firstParamName, sequence);
            }
            
            print(call);
            println(";");
            setIndent(getIndent() - 1);
        }
        setIndent(getIndent() + 1);
        PointerType pointerType = new PointerType(constructor.getParent());
        print(pointerType.generateCDefinition("this"));
        print(" = (");
        print(pointerType.generateCDefinition());
        print(") ");
        print(firstParamName);
        println(";");
        setIndent(getIndent() - 1);
        
        CompoundStatementCompiler compoundStatementCompiler = new CompoundStatementCompiler(getPrintWriter(), 1);
        
        if(!compoundStatementCompiler.compile(body)) return false;
        
        setIndent(getIndent() + 1);
        print("return ");
        println("this;");
        setIndent(getIndent() - 1);
        
        println("}");
        println();
        return true;
    }
    
    @Override
    protected void setIndent(int indent) {
        super.setIndent(indent);
        setPrintWriter(new IndentPrintWriter(getPrintWriter(), getIndent(), getSettings().getIndent()));
    }
}
