package radin.midanalysis.typeanalysis.analyzers;

import radin.core.semantics.types.CXIdentifier;
import radin.output.tags.ArrayWithSizeTag;
import radin.output.tags.BasicCompilationTag;
import radin.output.tags.ResolvedPathTag;
import radin.output.typeanalysis.IVariableTypeTracker;
import radin.output.typeanalysis.errors.IncorrectTypeError;
import radin.output.typeanalysis.errors.RedeclarationError;
import radin.output.typeanalysis.errors.TypeNotDefinedError;
import radin.output.typeanalysis.errors.VoidTypeError;
import radin.output.tags.MultiDimensionalArrayWithSizeTag;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.CXCompoundTypeNameIndirection;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.core.semantics.types.primitives.ArrayType;
import radin.output.typeanalysis.TypeAnalyzer;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.core.utility.ICompilationSettings;

import java.util.LinkedList;
import java.util.List;

public class StatementDeclarationTypeAnalyzer extends TypeAnalyzer {

    private IVariableTypeTracker.NameType nameType;


    public StatementDeclarationTypeAnalyzer(TypeAugmentedSemanticNode tree, IVariableTypeTracker.NameType type) {
        super(tree);
        this.nameType = type;
    }

    public StatementDeclarationTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        this(tree, IVariableTypeTracker.NameType.LOCAL);
    }
    
    private static boolean constantDeterminer(TypeAugmentedSemanticNode node) {
        ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(node);
        return typeAnalyzer.determineTypes() && !typeAnalyzer.hasErrors();
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        assert node.getASTNode().getTreeType() == ASTNodeType.declarations;
        
        for (TypeAugmentedSemanticNode declaration : node.getChildren()) {
            CXType declarationType;
            CXIdentifier name;
            TypeAugmentedSemanticNode identifierNode = null;

            
            if(declaration.getASTType() == ASTNodeType.declaration) {
                assert declaration.getASTNode() instanceof TypedAbstractSyntaxNode;
                
                declarationType =
                        ((TypedAbstractSyntaxNode) declaration.getASTNode()).getCxType();// .getTypeRedirection
                // (getEnvironment());
                
                if(strictIs(declarationType, CXPrimitiveType.VOID)) {
                    ICompilationSettings.debugLog.finer(declarationType.toString());
                    throw new VoidTypeError();
                }
                
                
                if(declarationType instanceof CXCompoundTypeNameIndirection) {
                    declarationType =
                            getEnvironment().getNamedCompoundType(((CXCompoundTypeNameIndirection) declarationType).getTypename());
                } else if(declarationType == null) {
                    throw new TypeNotDefinedError(declaration.findFirstToken().getPrevious());
                }
                
                if(declarationType instanceof ArrayType) {
                    if(((ArrayType) declarationType).getSizes().size() == 1) {
                        AbstractSyntaxNode size = ((ArrayType) declarationType).getSize();
                        if (size != null) {
                            ArrayWithSizeTag tag = new ArrayWithSizeTag(size, getEnvironment(), StatementDeclarationTypeAnalyzer::constantDeterminer);
                            declaration.addCompilationTag(tag);
                            if (tag.isConstant()) {
                                declaration.addCompilationTag(BasicCompilationTag.CONSTANT_SIZE);
                            } else {
                                ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(tag.getExpression());
                                if (!determineTypes(typeAnalyzer)) {
                                    setIsFailurePoint(declaration);
                                    return false;
                                }
                            }
                        }
                    } else {
                        List<AbstractSyntaxNode> sizes = ((ArrayType) declarationType).getSizes();
                        int dimension = ((ArrayType) declarationType).getDimensions();
                        MultiDimensionalArrayWithSizeTag tag =
                                new MultiDimensionalArrayWithSizeTag(dimension, sizes, getEnvironment(),
                                        StatementDeclarationTypeAnalyzer::constantDeterminer);
                        declaration.addCompilationTag(tag);
                        if (tag.isConstant()) {
                            declaration.addCompilationTag(BasicCompilationTag.CONSTANT_SIZE);
                        } else {
                            for (TypeAugmentedSemanticNode expression : tag.getExpressions()) {
                                ExpressionTypeAnalyzer typeAnalyzer = new ExpressionTypeAnalyzer(expression);
                                if (!determineTypes(typeAnalyzer)) {
                                    setIsFailurePoint(declaration);
                                    return false;
                                }
                            }
                           
                        }
                    }
                }
                
                
                
                name = new CXIdentifier(declaration.getASTChild(ASTNodeType.id).getToken());
                identifierNode = declaration.getASTChild(ASTNodeType.id);
                
            } else if(declaration.getASTType() == ASTNodeType.initialized_declaration) {
                
                // same process as prior but also checks to see if can place expression into type
                TypeAugmentedSemanticNode subDeclaration = declaration.getASTChild(ASTNodeType.declaration);
                declarationType = ((TypedAbstractSyntaxNode) subDeclaration.getASTNode()).getCxType();
                if(declarationType instanceof CXCompoundTypeNameIndirection) {
                    declarationType =
                            getEnvironment().getNamedCompoundType(((CXCompoundTypeNameIndirection) declarationType).getTypename());
                } else if(declarationType == null) {
                    throw new TypeNotDefinedError(subDeclaration.findFirstToken().getPrevious());
                }
    
                name = new CXIdentifier(subDeclaration.getASTChild(ASTNodeType.id).getToken());

                if(getCurrentTracker().globalVariableExists(name)) {
                    throw new RedeclarationError(name.getIdentifierString());
                }

                identifierNode = subDeclaration.getASTChild(ASTNodeType.id);
                
                TypeAugmentedSemanticNode expression = declaration.getChild(1);
                ExpressionTypeAnalyzer analyzer = new ExpressionTypeAnalyzer(expression);
                if(!determineTypes(analyzer)) {
                    getCurrentTracker().addLocalVariable(name.getIdentifierString(), declarationType);
                    return false;
                }
                
                if(expression.getToken() != null &&
                        expression.getToken().getImage() != null &&
                        expression.getToken().getImage().equals("nullptr")) {
                    ICompilationSettings.debugLog.finer("Assigning to nullptr bypasses typesystem");
                }
                else if(!is(expression.getCXType(), declarationType))
                    throw new IncorrectTypeError(declarationType, expression.getCXType(),
                            declaration.findFirstToken(), expression.findFirstToken());
                //if(!expression.getCXType().is(declarationType, getEnvironment())) throw new IncorrectTypeError
                // (declarationType, expression.getCXType());
                
                
                
            } else if(declaration.getASTType() == ASTNodeType.function_description) {
                declarationType =
                        ((TypedAbstractSyntaxNode) declaration.getASTNode()).getCxType().getTypeRedirection(getEnvironment());
    
                name = new CXIdentifier(declaration.getASTChild(ASTNodeType.id).getToken());
                identifierNode = declaration.getASTChild(ASTNodeType.id);
                

    
                TypeAugmentedSemanticNode astChild = declaration.getASTChild(ASTNodeType.parameter_list);
                List<CXType> typeList = new LinkedList<>();
                for (TypeAugmentedSemanticNode child : astChild.getChildren()) {
                    typeList.add(((TypedAbstractSyntaxNode) child.getASTNode()).getCxType());
                }
                CXFunctionPointer pointer = new CXFunctionPointer(declarationType, typeList);
                // getCurrentTracker().addFunction(name, pointer, false);
                getCurrentTracker().addGlobalVariable(name, pointer);
                declaration.getASTChild(ASTNodeType.id).setType(pointer);

                CXIdentifier id = getCurrentTracker().resolveIdentifier(name);
                declaration.getASTChild(ASTNodeType.id).addCompilationTag(new ResolvedPathTag(id));

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
            
            
            
            if(isBaseTracker()) {
                ICompilationSettings.debugLog.finer("Adding global variable " + name + " of type " + declarationType);
            }
            switch (nameType) {

                case LOCAL:
                    getCurrentTracker().addLocalVariable(name.getIdentifierString(), declarationType);
                    break;
                case GLOBAL:

                    CXIdentifier id = getCurrentTracker().addGlobalVariable(name, declarationType);
                    identifierNode.addCompilationTag(new ResolvedPathTag(id));
                    break;
            }

            declaration.setType(declarationType);
        }
        
        
        return true;
    }
}
