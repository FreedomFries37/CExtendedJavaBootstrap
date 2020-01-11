package radin.core.output.backend.microcompilers;

import radin.core.semantics.types.methods.CXMethod;

import java.io.PrintWriter;


public class MethodCompiler extends FunctionCompiler {
    
    public MethodCompiler(PrintWriter writer, int indent, CXMethod method) {
        super(writer, indent, method.getCFunctionName(), method.getReturnType(), method.getParametersExpanded(),
              null);
    }
    
    
    
}