package radin.midanalysis.typeanalysis.analyzers;

import radin.midanalysis.ScopedTypeTracker;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.output.tags.BasicCompilationTag;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.Visibility;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.midanalysis.typeanalysis.TypeAnalyzer;

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
            assert declaration.getASTNode() instanceof TypedAbstractSyntaxNode;
            
            CXType declarationType =
                    ((TypedAbstractSyntaxNode) declaration.getASTNode()).getCxType().getTypeRedirection(ScopedTypeTracker.getEnvironment());
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
