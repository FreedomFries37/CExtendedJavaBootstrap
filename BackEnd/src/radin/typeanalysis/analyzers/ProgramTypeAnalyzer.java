package radin.typeanalysis.analyzers;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypeAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.TypeAugmentedSemanticTree;

import java.util.LinkedList;
import java.util.List;

public class ProgramTypeAnalyzer extends TypeAnalyzer {
    
    public ProgramTypeAnalyzer(AbstractSyntaxNode program) {
        this(new TypeAugmentedSemanticTree(program, getEnvironment()).getHead());
        assert program.getType() == ASTNodeType.top_level_decs;
    }
    
    public ProgramTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
    
        for (TypeAugmentedSemanticNode child : node.getChildren()) {
            
            
            if(child.getASTType() == ASTNodeType.class_type_definition) {
                
                ClassTypeAnalyzer classTypeAnalyzer = new ClassTypeAnalyzer(child);
                if(!determineTypes(classTypeAnalyzer)) return false;
            } else if(child.getASTType() == ASTNodeType.function_definition) {
                
                FunctionTypeAnalyzer functionTypeAnalyzer = new FunctionTypeAnalyzer(child);
                if(!determineTypes(functionTypeAnalyzer)) return false;
    
                TypeAbstractSyntaxNode astNode =
                        ((TypeAbstractSyntaxNode) child.getASTNode());
                
                String name = child.getASTChild(ASTNodeType.id).getToken().getImage();
                CXType returnType = astNode.getCxType();
                node.setType(returnType);
                if(!getCurrentTracker().functionExists(name)) {
                    getCurrentTracker().addFunction(name, returnType);
    
                    TypeAugmentedSemanticNode astChild = child.getASTChild(ASTNodeType.parameter_list);
                    List<CXType> typeList = new LinkedList<>();
                    for (TypeAugmentedSemanticNode param : astChild.getChildren()) {
                        typeList.add(((TypeAbstractSyntaxNode) param.getASTNode()).getCxType());
                    }
                    CXFunctionPointer pointer = new CXFunctionPointer(returnType, typeList);
    
    
                    getCurrentTracker().addVariable(name, pointer);
                    child.getASTChild(ASTNodeType.id).setType(pointer);
                }
                
            } else if(child.getASTType() == ASTNodeType.declarations) {
                StatementDeclarationTypeAnalyzer analyzer = new StatementDeclarationTypeAnalyzer(child);
                
                if(!determineTypes(analyzer)) return false;
            
            } else if(child.getASTType() == ASTNodeType.qualifiers_and_specifiers) {
            
                if(child.getASTNode().getChild(0) instanceof TypeAbstractSyntaxNode) {
                    CXType declarationType = ((TypeAbstractSyntaxNode) child.getASTNode().getChild(0)).getCxType();
    
                    if(declarationType instanceof CXCompoundType) {
                        CXCompoundType cxCompoundType = ((CXCompoundType) declarationType);
                        if(!getCurrentTracker().isTracking(cxCompoundType)) {
                            getCurrentTracker().addBasicCompoundType(cxCompoundType);
                            getCurrentTracker().addIsTracking(cxCompoundType);
                        }
                    }
                }
            } else if(child.getASTType() == ASTNodeType.typedef) {
                child.setType(((TypeAbstractSyntaxNode) child.getASTNode()).getCxType());
            }
            
            
            
            
            
        }
        
        
        
        
        
        
        return true;
    }
}
