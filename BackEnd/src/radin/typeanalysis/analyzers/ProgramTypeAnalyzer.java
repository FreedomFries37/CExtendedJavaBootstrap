package radin.typeanalysis.analyzers;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.TypeAugmentedSemanticTree;
import radin.typeanalysis.TypeTracker;

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
                getCurrentTracker().addFunction(name, returnType);
                
            } else if(child.getASTType() == ASTNodeType.declarations) {
                StatementDeclarationTypeAnalyzer analyzer = new StatementDeclarationTypeAnalyzer(child);
                
                if(!determineTypes(analyzer)) return false;
            
            }
            
            
            
            
            
        }
        
        
        
        
        
        
        return true;
    }
}
