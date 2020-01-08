package radin.compilation.microcompilers;

import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXStructType;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.CXParameter;
import radin.core.semantics.types.wrapped.PointerType;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.TypeAugmentedSemanticTree;
import radin.typeanalysis.analyzers.CompoundStatementTypeAnalyzer;

import java.io.PrintWriter;
import java.util.List;

import static radin.typeanalysis.TypeAnalyzer.getCompilationSettings;

public class MethodCompiler extends FunctionCompiler {
    
    public MethodCompiler(PrintWriter writer, int indent, CXMethod method) {
        super(writer, indent, method.getCFunctionName(), method.getReturnType(), method.getParametersExpanded(),
              fix(method.getMethodBody(), method.getParent(), method.getReturnType(), method.getParametersExpanded(),
                      method));
    }
    
    public static TypeAugmentedSemanticNode fix(AbstractSyntaxNode node, CXClassType owner, CXType returnType,
                                                List<CXParameter> parameters, CXMethod method) {
        TypeAugmentedSemanticNode tree = new TypeAugmentedSemanticTree(node, owner.getEnvironment()).getHead();
        CompoundStatementTypeAnalyzer typeAnalyzer = new CompoundStatementTypeAnalyzer(tree, returnType, false);
        
        typeAnalyzer.typeTrackingClosureLoad(owner);
    
       
        
        typeAnalyzer.typeTrackingClosure();
        for (CXMethod cxMethod : owner.getVirtualMethodsOrder()) {
            if(!typeAnalyzer.getCurrentTracker().variableExists(cxMethod.getCFunctionName()))
                typeAnalyzer.getCurrentTracker().addVariable(cxMethod.getCFunctionName(), cxMethod.getFunctionPointer());
        }
        if(owner.getParent() != null) {
            for (CXMethod cxMethod : owner.getParent().getVirtualMethodsOrder()) {
                if(!typeAnalyzer.getCurrentTracker().variableExists(cxMethod.getCFunctionName()))
                    typeAnalyzer.getCurrentTracker().addVariable(cxMethod.getCFunctionName(), cxMethod.getFunctionPointer());
            }
        }
    
        CXStructType vTable = owner.getVTable();
        typeAnalyzer.getCurrentTracker().addBasicCompoundType(vTable);
        typeAnalyzer.getCurrentTracker().addPrivateField(owner, getCompilationSettings().getvTableName(),
                new PointerType(vTable));
        
    
        for (CXParameter parameter : parameters) {
            typeAnalyzer.getCurrentTracker().addVariable(parameter.getName(), parameter.getType());
        }
        
        if(!typeAnalyzer.determineTypes()) return null;
        typeAnalyzer.releaseTrackingClosure();
        typeAnalyzer.releaseTrackingClosure();
        
        return tree;
    }
    
}