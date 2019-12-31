package radin.compilation.microcompilers;

import radin.compilation.AbstractIndentedOutputCompiler;
import radin.compilation.tags.BasicCompilationTag;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.CXType;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.PrintWriter;

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
                    CXType type = child.getCXType();
                    switch (child.getASTType()) {
                        case declaration: {
                            String varName = child.getChild(0).getToken().getImage();
                            print(type.generateCDefinition(varName));
                            break;
                        }
                        case initialized_declaration: {
                            String varName = child.getASTChild(ASTNodeType.declaration).getChild(0).getToken().getImage();
                            print(type.generateCDefinition(varName));
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
