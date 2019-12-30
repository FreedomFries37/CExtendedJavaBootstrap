package radin.compilation.microcompilers;

import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.types.methods.CXMethod;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.PrintWriter;

public class MethodCompiler extends FunctionCompiler {
    
    public MethodCompiler(PrintWriter writer, int indent, CXMethod method) {
        super(writer, indent, method.getCFunctionName(), method.getReturnType(), method.getParameters(),
                TypeAnalyzer.getMethods().get(method));
    }
    
}