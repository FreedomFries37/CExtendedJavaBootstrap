package radin.midanalysis.typeanalysis.analyzers;


import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.exceptions.IncorrectParameterTypesError;
import radin.core.semantics.types.*;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.semantics.types.primitives.*;
import radin.core.semantics.types.wrapped.CXDynamicTypeDefinition;
import radin.core.semantics.types.wrapped.CXMappedType;
import radin.core.semantics.types.wrapped.ConstantType;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.Reference;
import radin.core.utility.UniversalCompilerSettings;
import radin.midanalysis.ScopedTypeTracker;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.output.tags.BasicCompilationTag;
import radin.output.tags.ConstructorCallTag;
import radin.output.tags.MethodCallTag;
import radin.output.tags.SuperCallTag;
import radin.output.typeanalysis.TypeAnalyzer;
import radin.output.typeanalysis.errors.IllegalAccessError;
import radin.output.typeanalysis.errors.*;

import java.util.regex.Pattern;

public class ExpressionTypeAnalyzer extends TypeAnalyzer {
    
    public ExpressionTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        if(node.isTypedExpression()) return true;
        
        if(node.getASTNode().getTreeType() == ASTNodeType._super) {
            node.setType(getCurrentTracker().getType("super"));
            return true;
        }
        
        if(node.getASTType() == ASTNodeType.sizeof) {
            node.setType(UnsignedPrimitive.createUnsigned(LongPrimitive.create()));
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.literal) {
            String image = node.getToken().getImage();
            Pattern floatingPoint = Pattern.compile("-?\\d+\\.\\d*|\\d*\\.\\d+");
            Pattern integer = Pattern.compile("\\d+|0b[01]+|0x[a-fA-F]+");
            Pattern character = Pattern.compile("'(.|\\\\.)'", Pattern.DOTALL);
            
            if(floatingPoint.matcher(image).matches()) {
                node.setType(CXPrimitiveType.DOUBLE);
            } else if(integer.matcher(image).matches()) {
                node.setType(LongPrimitive.create());
            } else if(character.matcher(image).matches()) {
                node.setType(CXPrimitiveType.CHAR);
            } else {
                setIsFailurePoint(node);
                return false;
            }
            
            return true;
        }
        
        if(node.getASTType() == ASTNodeType._true || node.getASTType() == ASTNodeType._false) {
            node.setType(UnsignedPrimitive.createUnsigned(CXPrimitiveType.CHAR));
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.string) {
            node.setType(new PointerType(CXPrimitiveType.CHAR));
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.id) {
            String image = node.getToken().getImage();
            if(!getCurrentTracker().variableExists(image)) {
                throw new IdentifierDoesNotExistError(image);
            }
            CXType type = getCurrentTracker().getType(image);
            if(type instanceof CXDynamicTypeDefinition) {
                type = ((CXDynamicTypeDefinition) type).getOriginal();
            }
            node.setType(type);
            node.setLValue(true);
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.binop) {
            Token opToken = node.getASTChild(ASTNodeType.operator).getToken();
            TypeAugmentedSemanticNode lhs = node.getChild(1);
            TypeAugmentedSemanticNode rhs = node.getChild(2);
            
            if(!determineTypes(lhs)) {
                setIsFailurePoint(lhs);
                return false;
            }
            if(!determineTypes(rhs)) {
                setIsFailurePoint(rhs);
                return false;
            }
            
            if(!canBinaryOp(lhs.getCXType(), rhs.getCXType())) {
                throw new IllegalTypesForOperationError(opToken, lhs.getCXType(), rhs.getCXType());
            }
            if(isComparison(opToken.getType())) {
                if(ScopedTypeTracker.getEnvironment().isStandardBooleanDefined()) {
                    node.setType(ScopedTypeTracker.getEnvironment().getTypeDefinition("bool"));
                } else {
                    node.setType(UnsignedPrimitive.createUnsignedShort());
                }
            } else {
                if(rhs.getCXType() instanceof PointerType) {
                    node.setType(rhs.getCXType());
                } else node.setType(lhs.getCXType());
                
                if(lhs.getCXType() instanceof PointerType || lhs.getCXType() instanceof ArrayType) {
                    node.setLValue(true);
                } else if(rhs.getCXType() instanceof PointerType || rhs.getCXType() instanceof ArrayType) {
                    node.setLValue(true);
                } else node.setLValue(false);
                
                if(node.getCXType() instanceof ConstantType) {
                    node.setType(((ConstantType) node.getCXType()).getSubtype());
                }
                
            }
            
            
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.indirection) {
            TypeAugmentedSemanticNode child = node.getChild(0);
            if(!determineTypes(child)){
                setIsFailurePoint(child);
                return false;
            }
            if(!canDereference(child.getCXType()))
                throw new IllegalTypesForOperationError(new Token(TokenType.t_star),
                    child.getCXType(), child.findFirstToken());
            assert child.getCXType() instanceof PointerType;
            
            if(strictIs(((PointerType) child.getCXType()).getSubType(), CXPrimitiveType.VOID)) throw new VoidDereferenceError();
            
            if(child.getASTType() == ASTNodeType.constructor_call) {
                node.addCompilationTag(BasicCompilationTag.NEW_OBJECT_DEREFERENCE);
            }
            
            CXType subType = ((PointerType) child.getCXType()).getSubType();
            if(subType instanceof CXCompoundTypeNameIndirection) {
                subType =
                        ScopedTypeTracker.getEnvironment().getNamedCompoundType(((CXCompoundTypeNameIndirection) subType).getTypename());
            }
            
            
            node.setType(
                    subType
            );
            node.setLValue(child.isLValue());
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.addressof) {
            TypeAugmentedSemanticNode child = node.getChild(0);
            
            if(!determineTypes(child)) {
                setIsFailurePoint(child);
                return false;
            }
            
            if(!child.isLValue()) throw new IllegalLValueError(child.findFirstToken());
            node.setType(new PointerType(child.getCXType()));
            node.setLValue(true);
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.uniop) {
            TypeAugmentedSemanticNode child = node.getChild(1);
            if(!determineTypes(child)) {
                setIsFailurePoint(child);
                return false;
            }
            CXType childCXType = child.getCXType();
            single_op(node, child, childCXType);
            
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.postop) {
            TypeAugmentedSemanticNode child = node.getChild(0);
            if(!determineTypes(child)) {
                setIsFailurePoint(child);
                return false;
            }
            CXType childCXType = child.getCXType();
            single_op(node, child, childCXType);
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.cast) {
            assert node.getASTNode() instanceof TypedAbstractSyntaxNode;
            CXType castType = ((TypedAbstractSyntaxNode) node.getASTNode()).getCxType();
            TypeAugmentedSemanticNode child = node.getChild(0);
            if(!determineTypes(child)) {
                setIsFailurePoint(child);
                return false;
            }
            CXType fromCXType = child.getCXType();
            
            if(!ScopedTypeTracker.environment.is(castType, CXPrimitiveType.VOID.toPointer()) &&
                    !is(castType, fromCXType)) {
                throw new IllegalCastError(fromCXType, castType, child.findFirstToken());
            }
            //if(!castType.is(fromCXType, getEnvironment())) throw new IllegalCastError(fromCXType, castType);
            node.setType(castType);
            node.setLValue(child.isLValue());
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.array_reference) {
            TypeAugmentedSemanticNode lhs = node.getChild(0);
            TypeAugmentedSemanticNode rhs = node.getChild(1);
     
            if(!determineTypes(lhs)) {
                setIsFailurePoint(lhs);
                return false;
            }
            if(!determineTypes(rhs)) {
                setIsFailurePoint(rhs);
                return false;
            }
            
            CXType cxType = lhs.getCXType();
            boolean isConstant = false;
            if(cxType instanceof ConstantType) {
                cxType = ((ConstantType) cxType).getSubtype();
                isConstant = true;
            }
            if(cxType instanceof CXDynamicTypeDefinition) {
                cxType = ((CXDynamicTypeDefinition) cxType).getWrappedType();
            }
            
            if(!(canBinaryOp(cxType, rhs.getCXType()) || canDereference(cxType))) {
                throw new IllegalTypesForOperationError(node.getASTNode().getToken(), cxType, rhs.getCXType());
            }
            CXType nextType;
            if(cxType instanceof ArrayType) {
                nextType = ((ArrayType) cxType).getBaseType();
            } else {
                nextType = ((PointerType) cxType).getSubType();
            }
            if(isConstant) {
                nextType = new ConstantType(nextType);
            }
            node.setType(nextType);
            node.setLValue(lhs.isLValue() || rhs.isLValue());
            
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.function_call) {
            /*
            if(node.getChild(0).getASTType() != ASTNodeType.id) {
            
    
            } else {
                String name = node.getASTChild(ASTNodeType.id).getToken().getImage();
                if (getCurrentTracker().variableExists(name) && !getCurrentTracker().functionExists(name)) {
                    throw new IdentifierNotFunctionError(name);
                }
                if (!getCurrentTracker().functionExists(name)) {
                    throw new IdentifierDoesNotExistError(name);
                }
                CXType type = getCurrentTracker().getType(name);
                node.setType(type);
            }
           
             */
            if(!determineTypes(node.getChild(0))) {
                setIsFailurePoint(node.getChild(0));
                return false;
            }
            
            SequenceTypeAnalyzer sequenceTypeAnalyzer = new SequenceTypeAnalyzer(node.getASTChild(ASTNodeType.sequence));
            if(!determineTypes(sequenceTypeAnalyzer)) return false;
            
            assert node.getChild(0).getCXType() instanceof CXFunctionPointer;
            CXFunctionPointer cxType = (CXFunctionPointer) node.getChild(0).getCXType();
            node.setType(cxType.getReturnType());
            node.setLValue(false);
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.field_get) {
            TypeAugmentedSemanticNode objectInteraction = node.getChild(0);
            if(!determineTypes(objectInteraction)) throw new IllegalAccessError(node.getChild(0).findFirstToken());
            
            
            
            assert objectInteraction.getCXType() instanceof CXCompoundType || objectInteraction.getCXType() instanceof ConstantType;
            String name =node.getChild(1).getToken().getImage();
            
            ICompilationSettings.debugLog.fine("Checking if " + objectInteraction.getCXType() + " has field " + name + "...");
            CXType parentType;
            if(objectInteraction.getCXType() instanceof CXCompoundType) {
                parentType = objectInteraction.getCXType();
            } else if(objectInteraction.getCXType() instanceof ConstantType){
                parentType = ((ConstantType) objectInteraction.getCXType()).getSubtype().getTypeRedirection(ScopedTypeTracker.getEnvironment());
            } else {
                setIsFailurePoint(node.getChild(1));
                throw new IllegalAccessError(objectInteraction.getCXType(), name, node.getChild(0).findFirstToken()
                );
            }
            
            
            if(objectInteraction.getASTType() == ASTNodeType.indirection) {
                node.addCompilationTag(BasicCompilationTag.INDIRECT_FIELD_GET);
            }
            
            CXType nextType;
            
            
            if(!getCurrentTracker().fieldVisible((CXCompoundType) parentType, name)) {
                if(!UniversalCompilerSettings.getInstance().getSettings().isInRuntimeCompilationMode())
                    throw new IllegalAccessError(parentType, name, node.getChild(0).findFirstToken());
                else {
                    if(parentType instanceof CXClassType) {
                        typeTrackingClosureSuperLoad(((CXClassType) parentType));
                        if(!getCurrentTracker().fieldVisible((CXCompoundType) parentType, name)) {
                            throw new IllegalAccessError(parentType, name, node.getChild(0).findFirstToken());
                        } else {
                            nextType = getCurrentTracker().getFieldType(((CXClassType) parentType), name);
                            releaseTrackingClosure();
                        }
                    }else {
                        node.setType(CXPrimitiveType.VOID.toPointer());
                        node.setLValue(true);
                        return true;
                    }
                }
            }
            else {
                if (objectInteraction.getCXType() instanceof CXClassType) {
                    nextType = getCurrentTracker().getFieldType(((CXClassType) parentType), name);
                } else {
                    nextType = getCurrentTracker().getFieldType((CXCompoundType) parentType, name);
                }
            }
            
            if(nextType instanceof CXMappedType) {
                ((CXMappedType) nextType).update();
            }
            
            if(nextType == null) throw new IllegalAccessError(node.getChild(0).findFirstToken());
            node.setType(nextType);
            ICompilationSettings.debugLog.fine("Found. Field Type = " + nextType);
            node.setLValue(objectInteraction.isLValue());
            return true;
        }
        
        if(node.getASTNode().getTreeType() == ASTNodeType.method_call) {
            TypeAugmentedSemanticNode objectInteraction = node.getChild(0);
            if(!determineTypes(objectInteraction)) {
                throw new IllegalAccessError(node.getChild(0).getToken());
            }
            boolean isSuperCall = false;
            if(objectInteraction.getASTType() == ASTNodeType.indirection) {
                node.addCompilationTag(BasicCompilationTag.INDIRECT_METHOD_CALL);
                if(objectInteraction.getChild(0).getASTType() == ASTNodeType._super) {
                    isSuperCall = true;
                }
            }
    
            Token name = node.getChild(1).getToken();
    
            TypeAugmentedSemanticNode sequenceNode = node.getASTChild(ASTNodeType.sequence);
            SequenceTypeAnalyzer analyzer = new SequenceTypeAnalyzer(sequenceNode);
    
            if(!determineTypes(analyzer)) return false;
    
            ParameterTypeList typeList = new ParameterTypeList(analyzer.getCollectedTypes());
            
            if(objectInteraction.getCXType() instanceof CXFunctionPointer) {
                CXFunctionPointer fp = (CXFunctionPointer) objectInteraction.getCXType();
                if(!typeList.equals(fp.getParameterTypeList(), ScopedTypeTracker.getEnvironment())) throw new IncorrectParameterTypesError();
                
                node.setType(fp.getReturnType());
                return true;
            }
            
            
            CXType cxClass;
            if(objectInteraction.getCXType() instanceof CXCompoundType) {
                cxClass = objectInteraction.getCXType();
            }
            
            else {
                cxClass = ((ConstantType) objectInteraction.getCXType()).getSubtype().getTypeRedirection(ScopedTypeTracker.getEnvironment());
            }
            
            //assert cxClass instanceof CXClassType;
           
            
            
            if(!(cxClass instanceof CXClassType) || !getCurrentTracker().methodVisible(((CXClassType) cxClass),
                    name.getImage(),
                    typeList)) {
                if(getCurrentTracker().fieldVisible((CXCompoundType) cxClass, name.getImage())) {
                    node.addCompilationTag(BasicCompilationTag.COMPILE_AS_FIELD_GET);
                    
                    assert getCurrentTracker().getFieldType((CXCompoundType) cxClass, name.getImage()) instanceof CXFunctionPointer;
                    node.setType(((CXFunctionPointer) getCurrentTracker().getFieldType((CXCompoundType) cxClass,
                            name.getImage())).getReturnType());
                    
                    return true;
                }
                ICompilationSettings.debugLog.finer("Couldn't find methods with name " + name.getImage());
                ICompilationSettings.debugLog.finest("Available options = " + getCurrentTracker().allMethodsAvailable());
                throw new IllegalAccessError(cxClass, name.getImage(), typeList, node.getChild(0).findFirstToken(), name);
            }
            
            CXType nextType = getCurrentTracker().getMethodType(((CXClassType) cxClass), name.getImage(), typeList);
            if(nextType == null) throw new IllegalAccessError(node.getChild(0).findFirstToken());
            
            if(!isSuperCall) {
                Reference<Boolean> ref = new Reference<>();
                CXMethod method = ((CXClassType) cxClass).getMethod(name, typeList, ref);
                if (ref.getValue()) {
                    node.addCompilationTag(BasicCompilationTag.VIRTUAL_METHOD_CALL);
                }
                
                node.addCompilationTag(new MethodCallTag(method));
            } else {
                CXMethod superMethod = ((CXClassType) cxClass).getSuperMethod(name.getImage(), typeList);
                if(superMethod != null)
                    node.addCompilationTag(new SuperCallTag(superMethod));
            }
            
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
            if(lhs.getCXType().is(rhs.getCXType(), ScopedTypeTracker.getEnvironment())) {
                outputType = rhs.getCXType();
            } else if(rhs.getCXType().is(lhs.getCXType(), ScopedTypeTracker.getEnvironment())) {
                outputType = lhs.getCXType();
            } else {
                throw new IncorrectTypeError(rhs.getCXType(), lhs.getCXType(), lhs.findFirstToken(), rhs.findFirstToken());
            }
            
            node.setType(outputType);
            node.setLValue(lhs.isLValue() && rhs.isLValue());
            return true;
        }
        
        if(node.getASTType() == ASTNodeType.constructor_call) {
            assert node.getASTNode() instanceof TypedAbstractSyntaxNode;
            CXType base = ((TypedAbstractSyntaxNode) node.getASTNode()).getCxType();
            if(base instanceof ICXWrapper) {
                base = ((ICXWrapper) base).getWrappedType();
            }
            CXType constructedType = ((PointerType) base).getSubType();
            
            assert constructedType instanceof CXClassType;
            SequenceTypeAnalyzer sequenceTypeAnalyzer = new SequenceTypeAnalyzer(node.getASTChild(ASTNodeType.sequence));
            
            if(!determineTypes(sequenceTypeAnalyzer)) return false;
            ParameterTypeList parameterTypeList = new ParameterTypeList(sequenceTypeAnalyzer.getCollectedTypes());
            
            if(!getCurrentTracker().constructorVisible(((CXClassType) constructedType), parameterTypeList)) {
                throw new NoConstructorError(((CXClassType) constructedType), parameterTypeList);
            }
            
            CXConstructor constructor = ((CXClassType) constructedType).getConstructor(parameterTypeList);
            node.addCompilationTag(new ConstructorCallTag(constructor));
            
            node.setType(new PointerType(constructedType));
            return true;
        }
        
        return false;
    }
    
    public void single_op(TypeAugmentedSemanticNode node, TypeAugmentedSemanticNode child, CXType childCXType) {
        if(!(childCXType.isPrimitive() || childCXType instanceof PointerType)) throw new IllegalTypesForOperationError(node.getToken(),
                childCXType);
        Token opToken = node.getASTChild(ASTNodeType.operator).getToken();
        if(opToken.getType() == TokenType.t_inc || opToken.getType() == TokenType.t_dec) {
            if(!canIncrementOrDecrement(childCXType))
                throw new IllegalTypesForOperationError(opToken,
                    childCXType);
            
            node.setType(childCXType);
        } else if(opToken.getType() == TokenType.t_bang) {
            node.setType(CXPrimitiveType.INTEGER);
        } else {
            node.setType(childCXType);
        }
        
        node.setLValue(child.isLValue());
    }
    
    private boolean canIncrementOrDecrement(CXType type) {
        return type instanceof PointerType || type instanceof LongPrimitive
                || type instanceof UnsignedPrimitive
                || type == CXPrimitiveType.INTEGER
                || type == CXPrimitiveType.CHAR;
    }
    
    private boolean canBinaryOp(CXType left, CXType right) {
        
        return (left instanceof Dereference || left.isPrimitive()) && (right.isPrimitive() || right instanceof Dereference);
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
