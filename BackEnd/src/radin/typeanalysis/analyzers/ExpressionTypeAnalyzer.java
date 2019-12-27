package radin.typeanalysis.analyzers;

import com.sun.nio.sctp.NotificationHandler;
import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.exceptions.InvalidPrimitiveException;
import radin.interphase.semantics.types.*;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.compound.CXCompoundType;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;
import radin.interphase.semantics.types.primitives.LongPrimitive;
import radin.interphase.semantics.types.primitives.UnsignedPrimitive;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.errors.*;
import radin.typeanalysis.errors.IllegalAccessError;

import java.util.regex.Pattern;

public class ExpressionTypeAnalyzer extends TypeAnalyzer {
    
    public ExpressionTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        if(node.isTypedExpression()) return true;
        
        
        if(node.getASTNode().getType() == ASTNodeType.literal) {
            String image = node.getToken().getImage();
            Pattern floatingPoint = Pattern.compile("-?\\d+\\.\\d*|\\d*\\.\\d+");
            Pattern integer = Pattern.compile("\\d+|0b[01]+|0x[a-fA-F]+");
            Pattern character = Pattern.compile("'(.|\\.)'");
            
            if(floatingPoint.matcher(image).matches()) {
                node.setType(CXPrimitiveType.DOUBLE);
            } else if(integer.matcher(image).matches()) {
                node.setType(LongPrimitive.create());
            } else if(character.matcher(image).matches()) {
                node.setType(CXPrimitiveType.CHAR);
            } else {
                return false;
            }
            
            return true;
        }
        
        if(node.getASTNode().getType() == ASTNodeType.string) {
            node.setType(new PointerType(CXPrimitiveType.CHAR));
            return true;
        }
        
        if(node.getASTNode().getType() == ASTNodeType.id) {
            String image = node.getToken().getImage();
            if(!getCurrentTracker().entryExists(image)) return false;
            CXType type = getCurrentTracker().getType(image);
            node.setType(type);
            node.setLValue(true);
            return true;
        }
        
        if(node.getASTNode().getType() == ASTNodeType.binop) {
            Token opToken = node.getASTChild(ASTNodeType.operator).getToken();
            TypeAugmentedSemanticNode lhs = node.getChild(1);
            TypeAugmentedSemanticNode rhs = node.getChild(2);
            
            if(!determineTypes(lhs)) return false;
            if(!determineTypes(rhs)) return false;
            
            if(!canBinaryOp(lhs.getCXType(), rhs.getCXType())) {
                throw new IllegalTypesForOperationError(opToken, lhs.getCXType(), rhs.getCXType());
            }
            if(isComparison(opToken.getType())) {
                node.setType(CXPrimitiveType.INTEGER);
            } else {
                node.setType(lhs.getCXType());
            }
            node.setLValue(lhs.isLValue() || rhs.isLValue());
            return true;
        }
        
        if(node.getASTNode().getType() == ASTNodeType.indirection) {
            TypeAugmentedSemanticNode child = node.getChild(0);
            if(!determineTypes(child)) return false;
            if(!canDereference(child.getCXType())) throw new IllegalTypesForOperationError(node.getToken(),
                    child.getCXType());
            assert child.getCXType() instanceof PointerType;
            CXType subType = ((PointerType) child.getCXType()).getSubType();
            if(subType instanceof CompoundTypeReference) {
                subType =
                        getEnvironment().getNamedCompoundType(((CompoundTypeReference) subType).getTypename());
            }
            node.setType(
                    subType
            );
            node.setLValue(child.isLValue());
            return true;
        }
        
        if(node.getASTNode().getType() == ASTNodeType.addressof) {
            TypeAugmentedSemanticNode child = node.getChild(0);
            
            if(!determineTypes(child)) return false;
            if(!child.isLValue()) throw new IllegalLValueError(child);
            node.setType(new PointerType(child.getCXType()));
            node.setLValue(true);
            return true;
        }
        
        if(node.getASTNode().getType() == ASTNodeType.uniop) {
            TypeAugmentedSemanticNode child = node.getChild(1);
            if(!determineTypes(child)) return false;
            CXType childCXType = child.getCXType();
            if(!childCXType.isPrimitive()) throw new IllegalTypesForOperationError(node.getToken(),
                    childCXType);
    
            Token opToken = node.getASTChild(ASTNodeType.operator).getToken();
            if(opToken.getType() == TokenType.t_inc || opToken.getType() == TokenType.t_dec) {
                if(!canIncrementOrDecrement(childCXType)) throw new IllegalTypesForOperationError(node.getToken(),
                        childCXType);
                
                node.setType(childCXType);
            } else if(opToken.getType() == TokenType.t_bang) {
                node.setType(CXPrimitiveType.INTEGER);
            } else {
                node.setType(childCXType);
            }
            
            node.setLValue(child.isLValue());
            
            return true;
        }
        
        if(node.getASTNode().getType() == ASTNodeType.cast) {
            assert node.getASTNode() instanceof TypeAbstractSyntaxNode;
            CXType castType = ((TypeAbstractSyntaxNode) node.getASTNode()).getCxType();
            TypeAugmentedSemanticNode child = node.getChild(0);
            if(!determineTypes(child)) return false;
            CXType fromCXType = child.getCXType();
            
            if(!castType.is(fromCXType, getEnvironment())) throw new IllegalCastError(fromCXType, castType);
            node.setType(castType);
            node.setLValue(child.isLValue());
            return true;
        }
        
        if(node.getASTNode().getType() == ASTNodeType.array_reference) {
            TypeAugmentedSemanticNode lhs = node.getChild(0);
            TypeAugmentedSemanticNode rhs = node.getChild(1);
    
            if(!determineTypes(lhs)) return false;
            if(!determineTypes(rhs)) return false;
    
            if(!canBinaryOp(lhs.getCXType(), rhs.getCXType()) || !canDereference(lhs.getCXType())) {
                throw new IllegalTypesForOperationError(node.getASTNode().getToken(), lhs.getCXType(), rhs.getCXType());
            }
            
            if(lhs.getCXType() instanceof ArrayType) {
                node.setType(((ArrayType) lhs.getCXType()).getBaseType());
            } else {
                node.setType(((PointerType) lhs.getCXType()).getSubType());
            }
    
            node.setLValue(lhs.isLValue() || rhs.isLValue());
            
            return true;
        }
        
        if(node.getASTNode().getType() == ASTNodeType.function_call) {
            String name =node.getASTChild(ASTNodeType.id).getToken().getImage();
            if(!getCurrentTracker().entryExists(name)) return false;
            CXType type = getCurrentTracker().getType(name);
            node.setType(type);
            node.setLValue(false);
            return true;
        }
        
        if(node.getASTNode().getType() == ASTNodeType.field_get) {
            TypeAugmentedSemanticNode objectInteraction = node.getChild(0);
            if(!determineTypes(objectInteraction)) return false;
            assert objectInteraction.getCXType() instanceof CXCompoundType;
            String name =node.getASTChild(ASTNodeType.id).getToken().getImage();
            CXType nextType;
            
            if(objectInteraction.getCXType() instanceof CXClassType) {
                nextType = getCurrentTracker().getFieldType(((CXClassType) objectInteraction.getCXType()), name);
            } else {
                nextType = getCurrentTracker().getFieldType((CXCompoundType) objectInteraction.getCXType(), name);
            }
            if(nextType == null) throw new IllegalAccessError();
            node.setType(nextType);
            node.setLValue(true);
            return true;
        }
    
        if(node.getASTNode().getType() == ASTNodeType.method_call) {
            TypeAugmentedSemanticNode objectInteraction = node.getChild(0);
            if(!determineTypes(objectInteraction)) return false;
            assert objectInteraction.getCXType() instanceof CXClassType;
            String name = node.getASTChild(ASTNodeType.id).getToken().getImage();
            CXClassType cxClass = (CXClassType) objectInteraction.getCXType();
            CXType nextType = getCurrentTracker().getMethodType(cxClass,
                    name);
            if(nextType == null) throw new IllegalAccessError();
            
            
            
            
            
            node.setType(nextType);
        
            return true;
        }
        
        if(node.getASTType() == ASTNodeType.ternary) {
            TypeAugmentedSemanticNode expression = node.getChild(0);
            TypeAugmentedSemanticNode lhs = node.getChild(1);
            TypeAugmentedSemanticNode rhs = node.getChild(2);
            
            if(!determineTypes(expression)) return false;
            if(!determineTypes(lhs)) return false;
            if(!determineTypes(rhs)) return false;
            
            if(!expression.getCXType().isPrimitive()) throw  new IllegalRValueError();
            CXType outputType;
            if(lhs.getCXType().is(rhs.getCXType(), getEnvironment())) {
                outputType = rhs.getCXType();
            } else if(rhs.getCXType().is(lhs.getCXType(), getEnvironment())) {
                outputType = lhs.getCXType();
            } else {
                throw new IncorrectTypeError(rhs.getCXType(), lhs.getCXType());
            }
            
            node.setType(outputType);
            node.setLValue(lhs.isLValue() && rhs.isLValue());
            return true;
        }
        
        return false;
    }
    
    private boolean canIncrementOrDecrement(CXType type) {
        return type instanceof PointerType || type instanceof LongPrimitive
                || type instanceof UnsignedPrimitive
                || type == CXPrimitiveType.INTEGER
                || type == CXPrimitiveType.CHAR;
    }
    
    private boolean canBinaryOp(CXType left, CXType right) {
        return left.isPrimitive() && right.isPrimitive();
    }
    
    private boolean isComparison(TokenType operator) {
        return operator == TokenType.t_eq || operator == TokenType.t_neq || operator == TokenType.t_lt || operator == TokenType.t_lte
                || operator == TokenType.t_gte || operator == TokenType.t_gt || operator == TokenType.t_dand || operator == TokenType.t_dor;
    }
    
    private boolean canDereference(CXType object) {
        return object instanceof PointerType;
    }
    
    private boolean canArrayAccess(CXType object) {
        return object instanceof PointerType || object instanceof ArrayType;
    }
    
    
}
