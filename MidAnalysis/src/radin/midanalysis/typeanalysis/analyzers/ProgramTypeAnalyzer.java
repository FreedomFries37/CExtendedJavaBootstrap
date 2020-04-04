package radin.midanalysis.typeanalysis.analyzers;

import radin.midanalysis.TypeAugmentedSemanticTree;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.midanalysis.typeanalysis.TypeAnalyzer;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.types.primitives.PointerType;

import java.util.LinkedList;
import java.util.List;

public class ProgramTypeAnalyzer extends TypeAnalyzer {
    
    public ProgramTypeAnalyzer(AbstractSyntaxNode program) {
        this(new TypeAugmentedSemanticTree(program, getEnvironment()).getHead());
        assert program.getTreeType() == ASTNodeType.top_level_decs;
    }
    
    
    public ProgramTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        return determineTypes(node, true);
    }
    
    public boolean determineTypes(TypeAugmentedSemanticNode node, boolean closure) {
        if(closure) typeTrackingClosure();
        for (TypeAugmentedSemanticNode child : node.getChildren()) {
            
            
            if(child.getASTType() == ASTNodeType.class_type_definition) {
                
                ClassTypeAnalyzer classTypeAnalyzer = new ClassTypeAnalyzer(child);
                if(!determineTypes(classTypeAnalyzer)) {
                    setIsFailurePoint(child);
                    return false;
                }
            } else if(child.getASTType() == ASTNodeType.function_definition) {
                
                FunctionTypeAnalyzer functionTypeAnalyzer = new FunctionTypeAnalyzer(child);
                if(!determineTypes(functionTypeAnalyzer)) return false;
    
                TypedAbstractSyntaxNode astNode =
                        ((TypedAbstractSyntaxNode) child.getASTNode());
                
                String name = child.getASTChild(ASTNodeType.id).getToken().getImage();
                CXType returnType = astNode.getCxType();
                node.setType(returnType);
                if(!getCurrentTracker().functionExists(name)) {
                    getCurrentTracker().addFunction(name, returnType, true);
    
                    TypeAugmentedSemanticNode astChild = child.getASTChild(ASTNodeType.parameter_list);
                    List<CXType> typeList = new LinkedList<>();
                    for (TypeAugmentedSemanticNode param : astChild.getChildren()) {
                        typeList.add(((TypedAbstractSyntaxNode) param.getASTNode()).getCxType());
                    }
                    CXFunctionPointer pointer = new CXFunctionPointer(returnType, typeList);
    
    
                    getCurrentTracker().addVariable(name, pointer);
                    child.getASTChild(ASTNodeType.id).setType(pointer);
                }
                
            } else if(child.getASTType() == ASTNodeType.declarations) {
                StatementDeclarationTypeAnalyzer analyzer = new StatementDeclarationTypeAnalyzer(child);
                
                if(!determineTypes(analyzer)) return false;
            
            } else if(child.getASTType() == ASTNodeType.qualifiers_and_specifiers) {
            
                if(child.getASTNode().getChild(0) instanceof TypedAbstractSyntaxNode) {
                    CXType declarationType = ((TypedAbstractSyntaxNode) child.getASTNode().getChild(0)).getCxType();
    
                    if(declarationType instanceof CXCompoundType) {
                        CXCompoundType cxCompoundType = ((CXCompoundType) declarationType);
                        if(!getCurrentTracker().isTracking(cxCompoundType)) {
                            getCurrentTracker().addBasicCompoundType(cxCompoundType);
                            getCurrentTracker().addIsTracking(cxCompoundType);
                        }
                    }
                }
            } else if(child.getASTType() == ASTNodeType.typedef) {
                child.setType(((TypedAbstractSyntaxNode) child.getASTNode()).getCxType());
            } else if(child.getASTType() == ASTNodeType.top_level_decs) {
                if(!determineTypes(child, false)) {
                    setIsFailurePoint(child);
                    return false;
                }
            } else if(child.getASTType() == ASTNodeType.implement) {
                CXClassType subType = (CXClassType) ((PointerType) ((TypedAbstractSyntaxNode) child.getASTNode()).getCxType()).getSubType();
                ImplementationTypeAnalyzer implementationTypeAnalyzer = new ImplementationTypeAnalyzer(child,
                        subType);
                
                if(!determineTypes(implementationTypeAnalyzer)) {
                    setIsFailurePoint(child);
                    return false;
                }
                
                
            } else if(child.getASTType() == ASTNodeType.generic) {
                GenericTypeAnalyzer genericTypeAnalyzer = new GenericTypeAnalyzer(child);
                
                if(!determineTypes(genericTypeAnalyzer)) return false;
            }
            
            
            
            
            
        }
        
        if(closure) releaseTrackingClosure();
        return true;
    }
}
