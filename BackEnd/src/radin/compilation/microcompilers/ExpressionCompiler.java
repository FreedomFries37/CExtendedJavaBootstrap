package radin.compilation.microcompilers;

import radin.compilation.AbstractCompiler;
import radin.compilation.tags.BasicCompilationTag;
import radin.compilation.tags.MethodCallTag;
import radin.core.lexical.Token;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.CXType;
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
                //print(" ");
                print(opToken);
                //print(" ");
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
            case addressof: {
                print("&");
                print("(");
                TypeAugmentedSemanticNode child = node.getChild(0);
                if(!compile(child)) return false;
                print(')');
                break;
            }
            case indirection: {
                print('(');
                print("*");
                TypeAugmentedSemanticNode child = node.getChild(0);
                if(!compile(child)) return false;
                print(')');
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
                String objectInteractionImage;
                boolean isLValueMethodCall = objectInteraction.isLValue();
                boolean needToGetReference = !node.containsCompilationTag(BasicCompilationTag.INDIRECT_METHOD_CALL);
                MethodCallTag methodCallTag = objectInteraction.getCompilationTag(MethodCallTag.class);
    
                if(!isLValueMethodCall) {
                    if (needToGetReference || getSettings().isReduceIndirection()) {
                        objectInteractionImage = compileToString(objectInteraction); // save for later use
                        if (objectInteractionImage == null) return false;
                        print(objectInteractionImage);
                        print('.');
                    } else {
                        objectInteractionImage = compileToString(objectInteraction.getChild(0));
                        if (objectInteractionImage == null) return false;
                        print(objectInteractionImage);
                        print("->");
                    }
                    boolean isVirtualCall =
                            objectInteraction.containsCompilationTag(BasicCompilationTag.VIRTUAL_METHOD_CALL);
    
                    if (isVirtualCall) {
                        print(getSettings().getvTableName());
                        print('.');
                    }
    
                    if (needToGetReference) {
                        objectInteractionImage = "&" + "(" + objectInteractionImage + ")";
                    }
    
                    String sequence = compileToString(node.getASTChild(ASTNodeType.sequence));
                    if(sequence == null) return false;
                    if (sequence.isEmpty()) {
                        print(methodCallTag.getMethod().methodCall(objectInteractionImage));
                    } else {
                        print(methodCallTag.getMethod().methodCall(objectInteractionImage, sequence));
                    }
                } else if(node.getChild(0).containsCompilationTag(BasicCompilationTag.NEW_OBJECT_DEREFERENCE)){
                    TypeAugmentedSemanticNode original = node.getChild(0).getChild(0); // get constructor call
                    objectInteractionImage = compileToString(original);
                    if(objectInteractionImage == null) return false;
    
                    String sequence = compileToString(node.getASTChild(ASTNodeType.sequence));
                    if(sequence == null) return false;
                    if (sequence.isEmpty()) {
                        print(methodCallTag.getMethod().methodAsFunctionCall(objectInteractionImage));
                    } else {
                        print(methodCallTag.getMethod().methodAsFunctionCall(objectInteractionImage, sequence));
                    }
                    
                } else return false;
                
                break;
            }
            case field_get: {
                TypeAugmentedSemanticNode objectInteraction = node.getChild(0);
                boolean isIndirect = objectInteraction.containsCompilationTag(BasicCompilationTag.INDIRECT_FIELD_GET);
    
                if (!isIndirect || getSettings().isReduceIndirection()) {
                    if(!compile(objectInteraction)) return false;
                    print('.');
                } else {
                    if(!compile(objectInteraction.getChild(0))) return false;
                    print("->");
                }
                
                if(!compile(node.getChild(1))) return false;
                
                break;
            }
            case constructor_call: {
            
            }
            case empty: {
                break;
            }
            default:
                return false;
        }
        
        
        return true;
    }
    
    
}
