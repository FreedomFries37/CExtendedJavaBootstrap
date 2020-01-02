package radin.typeanalysis.analyzers;

import radin.compilation.tags.ArrayWithSizeTag;
import radin.compilation.tags.BasicCompilationTag;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.CXCompoundTypeNameIndirection;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.interphase.semantics.types.compound.CXCompoundType;
import radin.interphase.semantics.types.compound.CXFunctionPointer;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;
import radin.interphase.semantics.types.wrapped.ArrayType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.errors.IncorrectTypeError;
import radin.typeanalysis.errors.TypeNotDefinedError;
import radin.typeanalysis.errors.VoidTypeError;

import java.util.LinkedList;
import java.util.List;

public class StatementDeclarationTypeAnalyzer extends TypeAnalyzer {
    
    public StatementDeclarationTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        assert node.getASTNode().getType() == ASTNodeType.declarations;
        
        for (TypeAugmentedSemanticNode declaration : node.getChildren()) {
            CXType declarationType;
            String name;
            
            if(declaration.getASTType() == ASTNodeType.declaration) {
                assert declaration.getASTNode() instanceof TypeAbstractSyntaxNode;
                
                declarationType =
                        ((TypeAbstractSyntaxNode) declaration.getASTNode()).getCxType().getTypeRedirection(getEnvironment());
                
                if(strictIs(declarationType, CXPrimitiveType.VOID)) throw new VoidTypeError();
                
                
                if(declarationType instanceof CXCompoundTypeNameIndirection) {
                    declarationType =
                            getEnvironment().getNamedCompoundType(((CXCompoundTypeNameIndirection) declarationType).getTypename());
                } else if(declarationType == null) {
                    throw new TypeNotDefinedError(declaration.findFirstToken().getPrevious());
                }
                
                if(declarationType instanceof ArrayType) {
                    AbstractSyntaxNode size = ((ArrayType) declarationType).getSize();
                    if(size != null) {
                        ArrayWithSizeTag tag = new ArrayWithSizeTag(size, getEnvironment());
                        declaration.addCompilationTag(tag);
                        if(tag.isConstant()) {
                            declaration.addCompilationTag(BasicCompilationTag.CONSTANT_SIZE);
                        } else {
                            ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(tag.getExpression());
                            if(!determineTypes(typeAnalyzer)) {
                                setIsFailurePoint(declaration);
                                return false;
                            }
                        }
                    }
                }
                
                
                
                name = declaration.getASTChild(ASTNodeType.id).getToken().getImage();
                
            } else if(declaration.getASTType() == ASTNodeType.initialized_declaration) {
                
                // same process as prior but also checks to see if can place expression into type
                TypeAugmentedSemanticNode subDeclaration = declaration.getASTChild(ASTNodeType.declaration);
                declarationType =
                        ((TypeAbstractSyntaxNode) subDeclaration.getASTNode()).getCxType().getTypeRedirection(getEnvironment());
                if(declarationType instanceof CXCompoundTypeNameIndirection) {
                    declarationType =
                            getEnvironment().getNamedCompoundType(((CXCompoundTypeNameIndirection) declarationType).getTypename());
                } else if(declarationType == null) {
                    throw new TypeNotDefinedError(subDeclaration.findFirstToken().getPrevious());
                }
    
                name = subDeclaration.getASTChild(ASTNodeType.id).getToken().getImage();
                
                TypeAugmentedSemanticNode expression = declaration.getChild(1);
                ExpressionTypeAnalyzer analyzer = new ExpressionTypeAnalyzer(expression);
                if(!determineTypes(analyzer)) return false;
                
                if(!is(expression.getCXType(), declarationType))
                    throw new IncorrectTypeError(declarationType, expression.getCXType(),
                            declaration.findFirstToken(), expression.findFirstToken());
                //if(!expression.getCXType().is(declarationType, getEnvironment())) throw new IncorrectTypeError
                // (declarationType, expression.getCXType());
                
                
                
            } else if(declaration.getASTType() == ASTNodeType.function_description) {
                declarationType =
                        ((TypeAbstractSyntaxNode) declaration.getASTNode()).getCxType().getTypeRedirection(getEnvironment());
    
                name = declaration.getASTChild(ASTNodeType.id).getToken().getImage();
                
                
                getCurrentTracker().addFunction(name, declarationType);
    
                TypeAugmentedSemanticNode astChild = declaration.getASTChild(ASTNodeType.parameter_list);
                List<CXType> typeList = new LinkedList<>();
                for (TypeAugmentedSemanticNode child : astChild.getChildren()) {
                    typeList.add(((TypeAbstractSyntaxNode) child.getASTNode()).getCxType());
                }
                CXFunctionPointer pointer = new CXFunctionPointer(declarationType, typeList);
                
                getCurrentTracker().addVariable(name, pointer);
                declaration.getASTChild(ASTNodeType.id).setType(pointer);
                return true;
            } else {
                return false;
            }
            
            if(declarationType instanceof CXCompoundType) {
                CXCompoundType cxCompoundType = ((CXCompoundType) declarationType);
                if(!getCurrentTracker().isTracking(cxCompoundType)) {
                    getCurrentTracker().addBasicCompoundType(cxCompoundType);
                    getCurrentTracker().addIsTracking(cxCompoundType);
                }
            }
            
            
            
            
            getCurrentTracker().addVariable(name, declarationType);
            declaration.setType(declarationType);
        }
        
        
        return true;
    }
}
