package radin.midanalysis.typeanalysis.analyzers;

import radin.output.tags.BasicCompilationTag;
import radin.midanalysis.typeanalysis.errors.NonVoidReturnTypeError;
import radin.core.lexical.TokenType;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.midanalysis.typeanalysis.TypeAnalyzer;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.typeanalysis.errors.IllegalStatementError;
import radin.midanalysis.typeanalysis.errors.IncorrectReturnTypeError;
import radin.core.utility.ICompilationSettings;

public class StatementAnalyzer extends TypeAnalyzer {
    
    private CXType returnType;
    private boolean returns;
    
    public StatementAnalyzer(TypeAugmentedSemanticNode tree, CXType returnType) {
        super(tree);
        this.returnType = returnType;
        returns = false;
    }
    
    public boolean isReturns() {
        return returns;
    }
    
    private void setReturns(boolean returns) {
        this.returns = returns;
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        
        if(node.getASTType() == ASTNodeType.declarations) {
            StatementDeclarationTypeAnalyzer declarationTypeAnalyzer = new StatementDeclarationTypeAnalyzer(node);
    
            return determineTypes(declarationTypeAnalyzer);
        }else if(node.getASTType() == ASTNodeType.assignment) {
            AssignmentTypeAnalyzer assignmentTypeAnalyzer = new AssignmentTypeAnalyzer(node);
    
            return determineTypes(assignmentTypeAnalyzer);
        } else if(node.getASTType() == ASTNodeType.method_call ||
                node.getASTType() == ASTNodeType.function_call ||
                node.getASTType() == ASTNodeType.postop ||
                (node.getASTType() == ASTNodeType.uniop &&
                        (node.getChild(0).getToken().getType() == TokenType.t_inc ||
                                node.getChild(0).getToken().getType() == TokenType.t_dec))) {
            ExpressionTypeAnalyzer expressionTypeAnalyzer = new ExpressionTypeAnalyzer(node);
            return determineTypes(expressionTypeAnalyzer);
            
            
        } else if(node.getASTType() == ASTNodeType.if_cond) {
            
            ExpressionTypeAnalyzer expressionTypeAnalyzer = new ExpressionTypeAnalyzer(node.getChild(0));
            if(!determineTypes(expressionTypeAnalyzer)) return false;
            
            TypeAugmentedSemanticNode ifTrue = node.getChild(1);
            
            boolean trueReturn = false;
            boolean falseReturn = false;
            if(ifTrue.getASTNode() != AbstractSyntaxNode.EMPTY) {
                
                StatementAnalyzer secondAnalyzer = new StatementAnalyzer(ifTrue, returnType);
                if(!determineTypes(secondAnalyzer)) return false;
                
                trueReturn = secondAnalyzer.isReturns();
            }
            
            if(node.getChildren().size() == 3) {
                
                TypeAugmentedSemanticNode ifFalse = node.getChild(2);
                if (ifFalse.getASTNode() != AbstractSyntaxNode.EMPTY) {
                    node.addCompilationTag(BasicCompilationTag.HAS_ELSE);
                    StatementAnalyzer secondAnalyzer = new StatementAnalyzer(ifFalse, returnType);
                    if (!determineTypes(secondAnalyzer)) return false;
                    
                    falseReturn = secondAnalyzer.isReturns();
                }
                
                
            }
            
            setReturns(trueReturn && falseReturn);
            
        } else if(node.getASTType() == ASTNodeType.while_cond) {
            
            
            ExpressionTypeAnalyzer expressionTypeAnalyzer = new ExpressionTypeAnalyzer(node.getChild(0));
            if(!determineTypes(expressionTypeAnalyzer)) return false;
            
            StatementAnalyzer analyzer = new StatementAnalyzer(node.getChild(1), returnType);
            return determineTypes(analyzer);
        } else if(node.getASTType() == ASTNodeType.do_while_cond) {
            StatementAnalyzer analyzer = new StatementAnalyzer(node.getChild(0), returnType);
            if(!determineTypes(analyzer)) return false;
            
            if(analyzer.isReturns()) {
                setReturns(analyzer.isReturns());
            }
            
            ExpressionTypeAnalyzer expressionTypeAnalyzer = new ExpressionTypeAnalyzer(node.getChild(1));
            return determineTypes(expressionTypeAnalyzer);
        } else if(node.getASTType() == ASTNodeType.for_cond) {
            typeTrackingClosure();
            if(node.getChild(0).getASTNode() != AbstractSyntaxNode.EMPTY) {
                TypeAnalyzer firstAnalyzer;
                if(node.getChild(0).getASTType() == ASTNodeType.declarations) {
                    firstAnalyzer = new StatementDeclarationTypeAnalyzer(node.getChild(0));
                } else {
                    firstAnalyzer = new ExpressionTypeAnalyzer(node.getChild(0));
                }
                
                if(!determineTypes(firstAnalyzer)) return false;
            }
            
            if(node.getChild(1).getASTNode() != AbstractSyntaxNode.EMPTY) {
                TypeAnalyzer secondAnalyzer;
                if(node.getChild(1).getASTType() == ASTNodeType.declarations) {
                    secondAnalyzer = new StatementDeclarationTypeAnalyzer(node.getChild(1));
                } else {
                    secondAnalyzer = new ExpressionTypeAnalyzer(node.getChild(1));
                }
                
                if(!determineTypes(secondAnalyzer)) return false;
            }
            
            if(node.getChild(2).getASTNode() != AbstractSyntaxNode.EMPTY) {
                TypeAnalyzer thirdAnalyzer = new ExpressionTypeAnalyzer(node.getChild(2));
                
                if(!determineTypes(thirdAnalyzer)) return false;
            }
            
            StatementAnalyzer statementAnalyzer = new StatementAnalyzer(node.getChild(3), returnType);
            
            if(!determineTypes(statementAnalyzer)) return false;
            
            
            releaseTrackingClosure();
        } else if(node.getASTType() == ASTNodeType._return) {
            if(node.getChildren().isEmpty() || node.getChild(0).getASTNode() == AbstractSyntaxNode.EMPTY) {
                if(returnType != CXPrimitiveType.VOID) {
                    throw new NonVoidReturnTypeError();
                }
                node.addCompilationTag(BasicCompilationTag.VOID_RETURN);
            } else {
                
                ExpressionTypeAnalyzer expressionTypeAnalyzer = new ExpressionTypeAnalyzer(node.getChild(0));
                if(!determineTypes(expressionTypeAnalyzer)) return false;
                
                CXType gottenReturnType = node.getChild(0).getCXType();
                if(node.getChild(0).getToken() != null && node.getChild(0).getToken().getImage() != null && node.getChild(0).getToken().getImage().equals(
                        "nullptr")) {
                    ICompilationSettings.debugLog.finer("Assigning to nullptr bypasses typesystem");
                }
                else if(returnType == CXPrimitiveType.VOID || !is(gottenReturnType, returnType)) {
                    throw new IncorrectReturnTypeError(returnType, gottenReturnType);
                }
                
            }
            setReturns(true);
        }else if(node.getASTType() == ASTNodeType.compound_statement) {
            CompoundStatementTypeAnalyzer typeAnalyzer = new CompoundStatementTypeAnalyzer(node, returnType, true);
            
            if(!determineTypes(typeAnalyzer)) return false;
            
            if(typeAnalyzer.isReturns()) {
                setReturns(true);
            }
            
        } else if(node.getASTNode() == AbstractSyntaxNode.EMPTY) return true;
        else throw new IllegalStatementError();
        
        return true;
    }
}
