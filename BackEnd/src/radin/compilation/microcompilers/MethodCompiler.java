package radin.compilation.microcompilers;

import radin.core.semantics.types.methods.CXMethod;
import radin.typeanalysis.TypeAnalyzer;

import java.io.PrintWriter;

public class MethodCompiler extends FunctionCompiler {
    
    public MethodCompiler(PrintWriter writer, int indent, CXMethod method) {
        super(writer, indent, method.getCFunctionName(), method.getReturnType(), method.getParameters(),
                TypeAnalyzer.getMethods().get(method));
    }
    
}