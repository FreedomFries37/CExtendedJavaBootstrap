package radin.typeanalysis.analyzers;

import radin.compilation.tags.BasicCompilationTag;
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
            if(parent instanceof CXClassType) {
                CXClassType superType = ((CXClassType) parent).getParent();
                if(superType != null) {
                    if(getCurrentTracker().fieldVisible(superType, name)) {
                        declaration.getASTChild(ASTNodeType.id).addCompilationTag(BasicCompilationTag.SHADOWING_FIELD_NAME);
                    }
                }
            }
    
            switch (visibility) {
                case _public: {
                    getCurrentTracker().addPublicField(parent, name, declarationType);
                    break;
                }
                case internal: {
                    assert parent instanceof CXClassType;
                    getCurrentTracker().addInternalField(((CXClassType) parent), name, declarationType);
                    break;
                }
                case _private: {
                    assert parent instanceof CXClassType;
                    getCurrentTracker().addPrivateField(((CXClassType) parent), name, declarationType);
                    break;
                }
            }
        }
        
        return true;
    }
}
