package radin.core.output.backend.microcompilers;

import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.midanalysis.TypeAugmentedSemanticTree;
import radin.core.output.backend.compilation.AbstractIndentedOutputSingleOutputCompiler;
import radin.core.output.tags.PriorConstructorTag;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXStructType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;

import java.io.PrintWriter;

public class ClassCompiler extends AbstractIndentedOutputSingleOutputCompiler {
    
    private CXClassType cxClassType;
    private TypeAugmentedSemanticNode corresponding;
    
    public ClassCompiler(PrintWriter printWriter, int indent, CXClassType cxClassType, TypeAugmentedSemanticNode corresponding) {
        super(printWriter, indent);
        this.cxClassType = cxClassType;
        this.corresponding = corresponding;
    }
    
    @Override
    public boolean compile() {
        cxClassType.generateSuperMethods(getSettings().getvTableName());
        CXStructType structEquivalent = cxClassType.getStructEquivalent();
        
        CXStructType vTable = cxClassType.getVTable();
        print(structEquivalent.generateCDeclaration());
        println(";");
        println();
        print(vTable.generateCDefinition());
        println(";");
    
        
        print(structEquivalent.generateCDefinition());
        println(";");
        println();
        for (CXMethod cxMethod : cxClassType.getConcreteMethodsOrder()) {
            println(cxMethod.generateCDeclaration());
        }
    
        for (CXMethod cxMethod : cxClassType.getVirtualMethodsOrder()) {
            println(cxMethod.generateCDeclaration());
        }
        println();
        for (CXConstructor constructor : cxClassType.getConstructors()) {
            println(constructor.generateCDeclaration());
        }
        println();
        
        // CREATE INIT METHOD;
        {
            CXMethod initMethod = cxClassType.getInitMethod();
            AbstractSyntaxNode methodBody = initMethod.getMethodBody();
            TypeAugmentedSemanticNode augmentedSemanticNode =
                    new TypeAugmentedSemanticTree(methodBody, cxClassType.getEnvironment()).getHead();
            //augmentedSemanticNode.printTreeForm();
            FunctionCompiler initFunctionCompiler = new FunctionCompiler(
                    getPrintWriter(),
                    0,
                    initMethod.getCFunctionName(),
                    initMethod.getReturnType(),
                    initMethod.getParameters(),
                    augmentedSemanticNode
            );
            print("static ");
            if (!initFunctionCompiler.compile()) return false;
        }
        // PRINT METHODS
        for (CXMethod cxMethod : cxClassType.getConcreteMethodsOrder()) {
            if(cxMethod.getMethodBody() != null) {
                MethodCompiler methodCompiler = new MethodCompiler(getPrintWriter(), 0, cxMethod);
                if(!methodCompiler.compile()) return false;
            }
            println();
        }
        println();
        for (CXConstructor constructor : cxClassType.getConstructors()) {
            if(constructor.getMethodBody() != null) {
                
                TypeAugmentedSemanticNode constructTAST =
                        corresponding.findFromASTNode(constructor.getCorrespondingASTNode());
    
                if(constructTAST == null) throw new NullPointerException();
                TypeAugmentedSemanticNode body = constructTAST.getASTChild(ASTNodeType.compound_statement);
                ConstructorCompiler constructorCompiler;
                if(constructTAST.containsCompilationTag(PriorConstructorTag.class)) {
                    PriorConstructorTag compilationTag = constructTAST.getCompilationTag(PriorConstructorTag.class);
                    
                    constructorCompiler = new ConstructorCompiler(getPrintWriter(), constructor,
                            body,
                            compilationTag);
                } else {
                    constructorCompiler = new ConstructorCompiler(getPrintWriter(), constructor, body);
                }
                if(!constructorCompiler.compile()) return false;
            }
        }
        
        
        // CREATE STATIC SUPER METHODS
        for (CXMethod generatedSuper : cxClassType.getGeneratedSupers()) {
            print("static ");
            MethodCompiler methodCompiler = new MethodCompiler(getPrintWriter(), 0, generatedSuper);
            if(!methodCompiler.compile()) return false;
        }
    
        
        
        for (CXMethod cxMethod : cxClassType.getVirtualMethodsOrder()) {
            if(cxMethod.getMethodBody() != null && cxMethod.getParent() == cxClassType) {
                MethodCompiler methodCompiler = new MethodCompiler(getPrintWriter(), 0, cxMethod);
                if(!methodCompiler.compile()) return false;
                println();
            }
            
        }
        
    
        return true;
    }
}
