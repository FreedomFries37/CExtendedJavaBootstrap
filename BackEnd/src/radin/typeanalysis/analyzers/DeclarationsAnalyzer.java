package radin.typeanalysis.analyzers;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.interphase.semantics.types.Visibility;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.compound.CXCompoundType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;

public class DeclarationsAnalyzer extends TypeAnalyzer {
    
    private ASTNodeType declarationType;
    private CXCompoundType parent;
    private Visibility visibility;
    
    public DeclarationsAnalyzer(TypeAugmentedSemanticNode tree, ASTNodeType declarationType, CXCompoundType parent, Visibility visibility) {
        super(tree);
        this.declarationType = declarationType;
        this.parent = parent;
        this.visibility = visibility;
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
    
        for (TypeAugmentedSemanticNode declaration : node.getAllChildren(declarationType)) {
            assert declaration.getASTNode() instanceof TypeAbstractSyntaxNode;
            
            CXType declarationType =
                    ((TypeAbstractSyntaxNode) declaration.getASTNode()).getCxType().getTypeRedirection(getEnvironment());
            declaration.setType(declarationType);
            declaration.setLValue(true);
            if(declarationType instanceof CXCompoundType) {
                CXCompoundType cxCompoundType = ((CXCompoundType) declarationType);
                if(!getCurrentTracker().isTracking(cxCompoundType)) {
                    getCurrentTracker().addBasicCompoundType(cxCompoundType);
                    getCurrentTracker().addIsTracking(cxCompoundType);
                }
            }
            String name = declaration.getASTChild(ASTNodeType.id).getToken().getImage();
    
            switch (visibility) {
                case _public: {
                    getCurrentTracker().addPublic(parent, true, name, declarationType);
                    break;
                }
                case internal: {
                    assert parent instanceof CXClassType;
                    getCurrentTracker().addInternal(((CXClassType) parent), true, name, declarationType);
                    break;
                }
                case _private: {
                    assert parent instanceof CXClassType;
                    getCurrentTracker().addPrivate(((CXClassType) parent), true, name, declarationType);
                    break;
                }
            }
        }
        
        return true;
    }
}
