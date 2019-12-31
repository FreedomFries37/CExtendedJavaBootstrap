package radin.typeanalysis.analyzers;

import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.errors.IncorrectReturnTypeError;
import radin.typeanalysis.errors.UnreachableCodeError;

public class CompoundStatementTypeAnalyzer extends TypeAnalyzer {
    private CXType returnType;
    private boolean createNewScope;
    private boolean returns;
    
    public CompoundStatementTypeAnalyzer(TypeAugmentedSemanticNode tree, CXType returnType, boolean createNewScope) {
        super(tree);
        this.returnType = returnType;
        this.createNewScope = createNewScope;
    }
    
    public boolean isReturns() {
        return returns;
    }
    
    private void setReturns(boolean returns) {
        this.returns = returns;
    }
    
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        if(createNewScope) typeTrackingClosure();
    
        /*
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
    
                ExpressionTypeAnalyzer expressionTypeAnalyzer = new ExpressionTypeAnalyzer(child.getChild(0));
                if(!determineTypes(expressionTypeAnalyzer)) return false;
                
                TypeAugmentedSemanticNode ifTrue = child.getChild(0);
                if(ifTrue.getASTNode() != AbstractSyntaxNode.EMPTY) {
    
                    TypeAnalyzer secondAnalyzer;
                    
    
                    if(!determineTypes(secondAnalyzer)) return false;
                }
                
    
    
            } else if(child.getASTType() == ASTNodeType.while_cond) {
    
    
    
            } else if(child.getASTType() == ASTNodeType.do_while_cond) {
    
    
    
            } else if(child.getASTType() == ASTNodeType.for_cond) {
                typeTrackingClosure();
                if(child.getChild(0).getASTNode() != AbstractSyntaxNode.EMPTY) {
                    TypeAnalyzer firstAnalyzer;
                    if(child.getChild(0).getASTType() == ASTNodeType.declarations) {
                        firstAnalyzer = new StatementDeclarationTypeAnalyzer(child.getChild(0));
                    } else {
                        firstAnalyzer = new ExpressionTypeAnalyzer(child.getChild(0));
                    }
                    
                    if(!determineTypes(firstAnalyzer)) return false;
                }
    
                if(child.getChild(1).getASTNode() != AbstractSyntaxNode.EMPTY) {
                    TypeAnalyzer secondAnalyzer;
                    if(child.getChild(1).getASTType() == ASTNodeType.declarations) {
                        secondAnalyzer = new StatementDeclarationTypeAnalyzer(child.getChild(1));
                    } else {
                        secondAnalyzer = new ExpressionTypeAnalyzer(child.getChild(1));
                    }
        
                    if(!determineTypes(secondAnalyzer)) return false;
                }
    
                if(child.getChild(2).getASTNode() != AbstractSyntaxNode.EMPTY) {
                    TypeAnalyzer thirdAnalyzer = new ExpressionTypeAnalyzer(child.getChild(2));
                    
                    if(!determineTypes(thirdAnalyzer)) return false;
                }
    
                TypeAnalyzer statementAnalyzer;
                if(child.getChild(3).getASTType() == ASTNodeType.compound_statement) {
                    statementAnalyzer = new CompoundStatementTypeAnalyzer(child.getChild(3), returnType, false);
                } else {
                    statementAnalyzer = new ExpressionTypeAnalyzer(child.getChild(3));
                }
                
                if(!determineTypes(statementAnalyzer)) return false;
                
                
                releaseTrackingClosure();
            } else if(child.getASTType() == ASTNodeType._return) {
                if(child.getChildren().isEmpty() || child.getChild(0).getASTNode() == AbstractSyntaxNode.EMPTY) {
                    if(returnType != CXPrimitiveType.VOID) {
                        throw new NonVoidReturnType();
                    }
                } else {
                    
                    ExpressionTypeAnalyzer expressionTypeAnalyzer = new ExpressionTypeAnalyzer(child.getChild(0));
                    if(!determineTypes(expressionTypeAnalyzer)) return false;
    
                    CXType gottenReturnType = child.getChild(0).getCXType();
                    if(returnType == CXPrimitiveType.VOID || !gottenReturnType.is(returnType, getEnvironment())) {
                        throw new IncorrectReturnTypeError(returnType, gottenReturnType);
                    }
                    
                }
            }else if(child.getASTType() == ASTNodeType.compound_statement) {
                CompoundStatementTypeAnalyzer typeAnalyzer = new CompoundStatementTypeAnalyzer(child, returnType, true);
                
                if(!determineTypes(typeAnalyzer)) return false;
            } else return false;
            
            
        }
        
         */
        boolean output = true;
        for (TypeAugmentedSemanticNode child : node.getChildren()) {
            if(isReturns()) throw new UnreachableCodeError();
            
                StatementAnalyzer analyzer = new StatementAnalyzer(child,returnType);
            
            
            
            if(!determineTypes(analyzer)){
                setIsFailurePoint(child);
                output = false;
            }
            
            if(analyzer.isReturns()) {
                setReturns(analyzer.isReturns());
            }
            
        }
        
        
        if(createNewScope) releaseTrackingClosure();
        return output;
    }
}
