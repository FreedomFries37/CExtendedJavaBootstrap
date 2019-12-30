package radin.compilation.microcompilers;

import radin.compilation.AbstractCompiler;
import radin.compilation.tags.BasicCompilationTag;
import radin.compilation.tags.MethodCallTag;
import radin.interphase.lexical.Token;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.typeanalysis.TypeAugmentedSemanticNode;

import java.io.PrintWriter;

public class ExpressionCompiler extends AbstractCompiler {
    
    public ExpressionCompiler(PrintWriter printWriter) {
        super(printWriter);
    }
    
    @Override
    public boolean compile(TypeAugmentedSemanticNode node) {
        
        switch (node.getASTType()) {
            case string: {
                if(getSettings().autoCreateStrings()) {
                    // TODO: Create auto string creation
                    break;
                } // else go to the same process as ids and literals
            }
            case id:
            case literal: {
                print(node.getToken().getImage());
                break;
            }
            case uniop: {
                Token opToken = node.getASTChild(ASTNodeType.operator).getToken();
                TypeAugmentedSemanticNode child = node.getChild(1);
                print(opToken);
                if (!compile(child)) {
                    return false;
                }
                break;
            }
            case postop: {
                Token opToken = node.getASTChild(ASTNodeType.operator).getToken();
                TypeAugmentedSemanticNode child = node.getChild(0);
                if (!compile(child)) {
                    return false;
                }
                print(opToken);
                break;
            }
            case binop: {
                Token opToken = node.getASTChild(ASTNodeType.operator).getToken();
                TypeAugmentedSemanticNode lhs = node.getChild(1);
                TypeAugmentedSemanticNode rhs = node.getChild(2);
                print("(");
                if (!compile(lhs)) {
                    return false;
                }
                print(" ");
                print(opToken);
                print(" ");
                if (!compile(rhs)) {
                    return false;
                }
                print(")");
                break;
            }
            case cast: {
                CXType castType = node.getCXType();
                TypeAugmentedSemanticNode child = node.getChild(0);
                print("(");
                print(castType.generateCDefinition());
                print(") ");
                if(!compile(child)) return false;
                break;
            }
            case indirection: {
                print("*");
                TypeAugmentedSemanticNode child = node.getChild(0);
                compile(child);
                break;
            }
            case array_reference: {
                TypeAugmentedSemanticNode lhs = node.getChild(0);
                TypeAugmentedSemanticNode rhs = node.getChild(1);
                
                if(!compile(lhs)) return false;
                print("[");
                if(!compile(rhs)) return false;
                print("]");
                break;
            }
            case function_call: {
                TypeAugmentedSemanticNode call = node.getChild(0);
                TypeAugmentedSemanticNode sequence = node.getASTChild(ASTNodeType.sequence);
                
                if(!compile(call)) return false;
                print("(");
                if(!compile(sequence)) return false;
                print(")");
                break;
            }
            case sequence: {
                boolean first = true;
                for (TypeAugmentedSemanticNode child : node.getChildren()) {
                    if(first) first = false;
                    else print(", ");
                    if(!compile(child)) return false;
                }
                
                break;
            }
            case ternary: {
                TypeAugmentedSemanticNode expression = node.getChild(0);
                TypeAugmentedSemanticNode lhs = node.getChild(1);
                TypeAugmentedSemanticNode rhs = node.getChild(2);
                
                print("(");
                if(!compile(expression)) return false;
                print("?");
                if(!compile(lhs)) return false;
                print(":");
                if(!compile(rhs)) return false;
                print(")");
                break;
            }
            case method_call: {
                TypeAugmentedSemanticNode objectInteraction = node.getChild(0);
                MethodCallTag methodCallTag = objectInteraction.getCompilationTag(MethodCallTag.class);
                boolean isVirtualCall =
                        objectInteraction.containsCompilationTag(BasicCompilationTag.VIRTUAL_METHOD_CALL);
                
                
                
                break;
            }
            default:
                return false;
        }
        
        return true;
    }
}
