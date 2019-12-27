package radin.typeanalysis.analyzers;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.CompoundTypeReference;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.errors.IncorrectTypeError;

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
                if(declarationType instanceof CompoundTypeReference) {
                    declarationType =
                            getEnvironment().getNamedCompoundType(((CompoundTypeReference) declarationType).getTypename());
                }
                name = declaration.getASTChild(ASTNodeType.id).getToken().getImage();
                
            } else if(declaration.getASTType() == ASTNodeType.initialized_declaration) {
                
                // same process as prior but also checks to see if can place expression into type
                TypeAugmentedSemanticNode subDeclaration = declaration.getASTChild(ASTNodeType.declaration);
                declarationType =
                        ((TypeAbstractSyntaxNode) subDeclaration.getASTNode()).getCxType().getTypeRedirection(getEnvironment());
                if(declarationType instanceof CompoundTypeReference) {
                    declarationType =
                            getEnvironment().getNamedCompoundType(((CompoundTypeReference) declarationType).getTypename());
                }
                name = subDeclaration.getASTChild(ASTNodeType.id).getToken().getImage();
                
                TypeAugmentedSemanticNode expression = declaration.getChild(1);
                ExpressionTypeAnalyzer analyzer = new ExpressionTypeAnalyzer(expression);
                if(!determineTypes(analyzer)) return false;
                
                if(!expression.getCXType().is(declarationType, getEnvironment())) throw new IncorrectTypeError(declarationType, expression.getCXType());
                
                
                
            } else {
                return false;
            }
            
            
            getCurrentTracker().addEntry(name, declarationType);
        }
        
        
        return true;
    }
}
