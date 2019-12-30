package radin.compilation;

import radin.compilation.microcompilers.ClassCompiler;
import radin.compilation.microcompilers.ExpressionCompiler;
import radin.compilation.microcompilers.FunctionCompiler;
import radin.compilation.microcompilers.IndentPrintWriter;
import radin.compilation.tags.TypeDefHelperTag;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.compound.CXFunctionPointer;
import radin.interphase.semantics.types.methods.CXParameter;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class FileCompiler extends AbstractCompiler {
    
    public FileCompiler() {
        super(new PrintWriter(System.out));
    }
    
    public FileCompiler(File file) throws FileNotFoundException {
        super(new PrintWriter(file));
    }
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        for (TypeAugmentedSemanticNode topLevelDeclaration : node.getChildren()) {
            switch (topLevelDeclaration.getASTType()) {
                case typedef: {
                    
                    print("typedef ");
                    CXType originalType;
                    if(topLevelDeclaration.containsCompilationTag(TypeDefHelperTag.class)) {
                        TypeDefHelperTag typeDefHelperTag =
                                topLevelDeclaration.getCompilationTag(TypeDefHelperTag.class);
                        originalType = typeDefHelperTag.getOriginalType();
                    } else {
                        originalType = topLevelDeclaration.getCXType();
                    }
                    print(originalType
                            .generateCDefinition(
                                    topLevelDeclaration.getASTChild(ASTNodeType.id).getToken().getImage()
                            )
                    );
                    /*
                    print(originalType.generateCDefinition());
                    print(" ");
                    print(topLevelDeclaration.getASTChild(ASTNodeType.id).getToken().getImage());
                    println(";");
                    */
                    println(";");
                    break;
                }
                case function_definition: {
                    String name = topLevelDeclaration.getASTChild(ASTNodeType.id).getToken().getImage();
                    CXType returnType = ((TypeAbstractSyntaxNode) topLevelDeclaration.getASTNode()).getCxType();
                    List<CXParameter> parameters = new LinkedList<>();
                    for (TypeAugmentedSemanticNode child : topLevelDeclaration.getASTChild(ASTNodeType.parameter_list).getChildren()) {
                        CXType pType = ((TypeAbstractSyntaxNode) child.getASTNode()).getCxType();
                        String pName = child.getASTChild(ASTNodeType.id).getToken().getImage();
                        
                        parameters.add(new CXParameter(pType, pName));
                    }
                    TypeAugmentedSemanticNode compoundStatement =
                            topLevelDeclaration.getASTChild(ASTNodeType.compound_statement);
                    FunctionCompiler functionCompiler = new FunctionCompiler(
                            getPrintWriter(),
                            0,
                            name,
                            returnType,
                            parameters,
                            compoundStatement);
                    functionCompiler.compile();
                    break;
                }
                case qualifiers_and_specifiers: {
                    TypeAugmentedSemanticNode child = topLevelDeclaration.getChild(0);
                    ASTNodeType astType = child.getASTType();
                    TypeAbstractSyntaxNode astNode =
                            ((TypeAbstractSyntaxNode) child.getASTNode());
                    CXType type;
                    if(astType == ASTNodeType.specifier && !astNode.getCxType().isPrimitive()) {
                        if (child.containsCompilationTag(TypeDefHelperTag.class)) {
                            TypeDefHelperTag compilationTag =
                                    child.getCompilationTag(TypeDefHelperTag.class);
                            type = compilationTag.getOriginalType();
                        } else {
                            type = astNode.getCxType();
                        }
    
                        print(type.generateCDefinition());
                        println(";");
                    }
                    
                    break;
                }
                case declarations: {
                    for (TypeAugmentedSemanticNode child : topLevelDeclaration.getChildren()) {
                        CXType type = child.getCXType();
                        switch (child.getASTType()) {
                            case declaration: {
                                String varName = child.getChild(0).getToken().getImage();
                                print(type.generateCDefinition(varName));
                                break;
                            }
                            case initialized_declaration: {
                                ExpressionCompiler expressionCompiler = new ExpressionCompiler(getPrintWriter());
                                String varName = child.getASTChild(ASTNodeType.declaration).getChild(0).getToken().getImage();
                                print(type.generateCDefinition(varName));
                                print(" = ");
                                if(!expressionCompiler.compile(child.getChild(1))) return false;
                                break;
                            }
                            case function_description: {
                                type = ((TypeAbstractSyntaxNode) child.getASTNode()).getCxType();
                                TypeAugmentedSemanticNode id = child.getChild(0);
                                String funcName = id.getToken().getImage();
                                print(type.generateCDefinition(funcName));
                                print("(");
                                assert id.getCXType() instanceof CXFunctionPointer;
                                boolean first = true;
                                for (CXType parameterType : ((CXFunctionPointer) id.getCXType()).getParameterTypes()) {
                                    if(first) first =false;
                                    else print(", ");
                                    print(parameterType.generateCDefinition());
                                }
                                print(")");
                                break;
                            }
                           
                            default:
                                return false;
                        }
                        println(";");
                    }
                }
                case class_type_definition: {
                    CXClassType cxClass = (CXClassType) ((TypeAbstractSyntaxNode) topLevelDeclaration.getASTNode()).getCxType();
        
                    ClassCompiler classCompiler =
                            new ClassCompiler(getPrintWriter(), 0, cxClass);
        
                    if(!classCompiler.compile()) return false;
                }
                default:
                    break;
            }
        }
        flush();
        close();
        return true;
    }
}
