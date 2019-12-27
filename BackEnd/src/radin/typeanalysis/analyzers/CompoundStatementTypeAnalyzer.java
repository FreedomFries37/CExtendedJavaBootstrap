package radin.typeanalysis.analyzers;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.CXType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;

public class CompoundStatementTypeAnalyzer extends TypeAnalyzer {
    private CXType returnType;
    private boolean createNewScope;
    
    public CompoundStatementTypeAnalyzer(TypeAugmentedSemanticNode tree, CXType returnType, boolean createNewScope) {
        super(tree);
        this.returnType = returnType;
        this.createNewScope = createNewScope;
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        if(createNewScope) typeTrackingClosure();
    
        for (TypeAugmentedSemanticNode child : node.getChildren()) {
            
            
            if(child.getASTType() == ASTNodeType.declarations) {
                StatementDeclarationTypeAnalyzer declarationTypeAnalyzer = new StatementDeclarationTypeAnalyzer(child);
                
                if(!determineTypes(declarationTypeAnalyzer)) return false;
            }
            
            
            
        }
        
        if(createNewScope) releaseTrackingClosure();
        return true;
    }
}
