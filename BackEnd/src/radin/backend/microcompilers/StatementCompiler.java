package radin.backend.microcompilers;

import radin.backend.compilation.AbstractIndentedOutputCompiler;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.output.tags.ArrayWithSizeTag;
import radin.output.tags.BasicCompilationTag;
import radin.output.tags.MultiDimensionalArrayWithSizeTag;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;

import java.io.PrintWriter;

import static radin.core.semantics.ASTNodeType.*;

public class StatementCompiler extends AbstractIndentedOutputCompiler {
    
    
    
    public StatementCompiler(PrintWriter printWriter, int indent) {
        super(printWriter, indent);
        
    }
    
    public StatementCompiler(IndentPrintWriter printWriter) {
        super(printWriter);
        this.setIndent(printWriter.getIndent());
    }
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        ExpressionCompiler expressionCompiler = new ExpressionCompiler(getPrintWriter());
        switch (node.getASTType()) {
            case declarations: {
                
                for (TypeAugmentedSemanticNode child : node.getChildren()) {
                    if(child.getCXType() == null && child.getASTNode() instanceof TypedAbstractSyntaxNode) {
                        child.setType(((TypedAbstractSyntaxNode) child.getASTNode()).getCxType());
                    } else if(child.getCXType() == null) {
                        throw new NullPointerException();
                    }
                    CXType type = child.getCXType();
                    switch (child.getASTType()) {
                        case declaration: {
                            String varName = child.getChild(0).getToken().getImage();
                            String s = type.generateCDeclaration(varName);
                            if(child.containsCompilationTag(ArrayWithSizeTag.class)) {
                                ArrayWithSizeTag compilationTag = child.getCompilationTag(ArrayWithSizeTag.class);
                                String size = expressionCompiler.compileToString(compilationTag.getExpression());
                                s = s.replace("$REPLACE ME$", size);
                            } else if(child.containsCompilationTag(MultiDimensionalArrayWithSizeTag.class)) {
                                MultiDimensionalArrayWithSizeTag tag =
                                        child.getCompilationTag(MultiDimensionalArrayWithSizeTag.class);
                                for (int i = 0; i < tag.getExpressions().size(); i++) {
                                    TypeAugmentedSemanticNode augmentedSemanticNode = tag.getExpressions().get(i);
                                    String size = expressionCompiler.compileToString(augmentedSemanticNode);
                                    if(size == null) return false;
                                    String replace = String.format("$REPLACE ME %d$", i);
                                    s = s.replace(replace, size);
                                }
                            }
                            print(s);
                            break;
                        }
                        case initialized_declaration: {
                            String varName = child.getASTChild(declaration).getChild(0).getToken().getImage();
                            String s = type.generateCDeclaration(varName);
                            if(child.containsCompilationTag(ArrayWithSizeTag.class)) {
                                ArrayWithSizeTag compilationTag = child.getCompilationTag(ArrayWithSizeTag.class);
                                String size = expressionCompiler.compileToString(compilationTag.getExpression());
                                s = s.replace("$REPLACE ME$", size);
                            }
                            print(s);
                            print(" = ");
                            if(!expressionCompiler.compile(child.getChild(1))) return false;
                            break;
                        }
                        default:
                            return false;
                    }
                    
                    print(';');
                }
                break;
            }
            case assignment: {
                if(!expressionCompiler.compile(node.getChild(0))) return false;
                TypeAugmentedSemanticNode assignment_op = node.getASTChild(ASTNodeType.assignment_type);
                print(" ");
                if(assignment_op.containsCompilationTag(BasicCompilationTag.OPERATOR_ASSIGNMENT)) {
                    print(assignment_op.getToken().getImage());
                } else {
                    print(assignment_op.getToken().getType().toString());
                }
                print(" ");
                if (!expressionCompiler.compile(node.getChild(2))) {
                    return false;
                }
                print(';');
                break;
            }
            case uniop:
            case postop:
            case method_call:
            case function_call: {
                if(!expressionCompiler.compile(node)) return false;
                print(';');
                break;
            }
            case if_cond: {
                print("if (");
                if (!expressionCompiler.compile(node.getChild(0))) {
                    return false;
                }
                print(") ");
                if(!compile(node.getChild(1))) return false;
                if(node.containsCompilationTag(BasicCompilationTag.HAS_ELSE)) {
                    print(" else ");
                    if(!compile(node.getChild(2))) return false;
                }
                
                break;
            }
            case while_cond: {
                print("while (");
                if (!expressionCompiler.compile(node.getChild(0))) {
                    return false;
                }
                print(")");
                if(!compile(node.getChild(1))) return false;
                break;
            }
            case do_while_cond: {
                print ("do ");
                if(!compile(node.getChild(0))) return false;
                print("while (");
                if(!expressionCompiler.compile(node.getChild(1))) return false;
                print(");");
                break;
            }
            case for_cond: {
                print("for (");
                if(!compile(node.getChild(0)))return false;
                if(!expressionCompiler.compile(node.getChild(1))) return false;
                print("; ");
                if(!expressionCompiler.compile(node.getChild(2))) return false;
                print(")");
                if(!compile(node.getChild(3))) return false;
                break;
            }
            case _return: {
                print("return");
                if(!node.containsCompilationTag(BasicCompilationTag.VOID_RETURN)) {
                    print(" ");
                    if(!expressionCompiler.compile(node.getChild(0))) return false;
                }
                print(";");
                break;
            }
            case compound_statement: {
                println("{");
                CompoundStatementCompiler compoundStatementCompiler = new CompoundStatementCompiler(getPrintWriter(),
                        getIndent() + 1);
                compoundStatementCompiler.compile(node);
                print("}");
                break;
            }
            case empty: {
                print(";");
                break;
            }
            default:
                return false;
        }
        
        
        return true;
    }
    
   
}
