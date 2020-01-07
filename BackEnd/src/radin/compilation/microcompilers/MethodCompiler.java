package radin.compilation.microcompilers;

import radin.core.semantics.types.methods.CXMethod;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.TypeAugmentedSemanticTree;
import radin.typeanalysis.analyzers.CompoundStatementTypeAnalyzer;

import java.io.PrintWriter;

public class MethodCompiler extends FunctionCompiler {
    
    public MethodCompiler(PrintWriter writer, int indent, CXMethod method) {
        super(writer, indent, method.getCFunctionName(), method.getReturnType(), method.getParametersExpanded(),
              TypeAnalyzer.getMethods().get(method));
    }
    
    
    
}