package radin.compilation.microcompilers;

import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.CXParameter;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.core.semantics.types.wrapped.PointerType;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.TypeAugmentedSemanticTree;
import radin.typeanalysis.analyzers.CompoundStatementTypeAnalyzer;
import radin.typeanalysis.errors.IncorrectlyMissingCompoundStatement;

import java.io.PrintWriter;
import java.util.List;

public class MethodCompiler extends FunctionCompiler {
    
    public MethodCompiler(PrintWriter writer, int indent, CXMethod method) {
        super(writer, indent, method.getCFunctionName(), method.getReturnType(), method.getParametersExpanded(),
              fix(method.getMethodBody(), method.getParent(), method.getReturnType(), method.getParametersExpanded()));
    }
    
    public static TypeAugmentedSemanticNode fix(AbstractSyntaxNode node, CXClassType owner, CXType returnType,
                                                List<CXParameter>parameters) {
        TypeAugmentedSemanticNode tree = new TypeAugmentedSemanticTree(node, owner.getEnvironment()).getHead();
        CompoundStatementTypeAnalyzer typeAnalyzer = new CompoundStatementTypeAnalyzer(tree, returnType, false);
        
        typeAnalyzer.typeTrackingClosureLoad(owner);
        typeAnalyzer.typeTrackingClosure();
    
        for (CXParameter parameter : parameters) {
            typeAnalyzer.getCurrentTracker().addVariable(parameter.getName(), parameter.getType());
        }
        
        if(!typeAnalyzer.determineTypes()) return null;
        typeAnalyzer.releaseTrackingClosure();
        typeAnalyzer.releaseTrackingClosure();
        
        return tree;
    }
    
}