package radin.typeanalysis.analyzers;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.errors.IncorrectReturnTypeError;

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
            }else if(child.getASTType() == ASTNodeType.assignment) {
                AssignmentTypeAnalyzer assignmentTypeAnalyzer = new AssignmentTypeAnalyzer(child);
                
                if(!determineTypes(assignmentTypeAnalyzer)) return false;
            } else if(child.getASTType() == ASTNodeType.method_call || child.getASTType() == ASTNodeType.function_call) {
                ExpressionTypeAnalyzer expressionTypeAnalyzer = new ExpressionTypeAnalyzer(child);
                if(!determineTypes(expressionTypeAnalyzer)) return false;
            
            
            } else if(child.getASTType() == ASTNodeType.if_cond) {
    
    
    
            } else if(child.getASTType() == ASTNodeType.while_cond) {
    
    
    
            } else if(child.getASTType() == ASTNodeType.do_while_cond) {
    
    
    
            } else if(child.getASTType() == ASTNodeType.for_cond) {
    
    
    
            } else if(child.getASTType() == ASTNodeType._return) {
                if(child.getChildren().isEmpty()) {
                    if(returnType != CXPrimitiveType.VOID) {
                        throw new NonVoidReturnType();
                    }
                } else {
                    ExpressionTypeAnalyzer expressionTypeAnalyzer = new ExpressionTypeAnalyzer(child.getChild(0));
                    if(!determineTypes(expressionTypeAnalyzer)) return false;
    
                    CXType gottenReturnType = child.getChild(0).getCXType();
                    if(!gottenReturnType.is(returnType, getEnvironment())) throw new IncorrectReturnTypeError(returnType, gottenReturnType);
                    
                }
            }else if(child.getASTType() == ASTNodeType.compound_statement) {
    
    
    
            } else return false;
            
            
        }
        
        if(createNewScope) releaseTrackingClosure();
        return true;
    }
}
