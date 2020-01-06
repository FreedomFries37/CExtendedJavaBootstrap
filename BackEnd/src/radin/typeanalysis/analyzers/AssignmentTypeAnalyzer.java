package radin.typeanalysis.analyzers;

import radin.compilation.tags.BasicCompilationTag;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.wrapped.ConstantType;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticNode;
import radin.typeanalysis.errors.ConstModificationError;
import radin.typeanalysis.errors.IllegalLValueError;
import radin.typeanalysis.errors.IncorrectTypeError;

public class AssignmentTypeAnalyzer extends TypeAnalyzer {
    
    public AssignmentTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        assert node.getASTType() == ASTNodeType.assignment;
        
        TypeAugmentedSemanticNode lhs = node.getChild(0);
        TypeAugmentedSemanticNode rhs = node.getChild(2);
        
        TypeAnalyzer analyzer = new ExpressionTypeAnalyzer(lhs);
        if (!determineTypes(analyzer)) {
            return false;
        }
        if(lhs.getCXType() instanceof ConstantType) {
            
            if(lhs.getASTType() == ASTNodeType.id)
                throw new ConstModificationError(lhs.getToken().getImage(), lhs.getCXType());
            else {
                throw new ConstModificationError(lhs.getCXType());
            }
        }
        CXType rhsType;
        if(rhs.getASTType() != ASTNodeType.assignment) {
            analyzer = new ExpressionTypeAnalyzer(rhs);
            if (!determineTypes(analyzer)) {
                return false;
            }
            rhsType = rhs.getCXType();
        }else {
            if(!determineTypes(rhs)) return false;
            rhsType = rhs.getChild(0).getCXType();
        }
        
        if(!lhs.isLValue()) throw new IllegalLValueError(lhs);
    
        Token operator = node.getASTChild(ASTNodeType.assignment_type).getToken();
        if(operator.getType() != TokenType.t_assign) {
            node.addCompilationTag(BasicCompilationTag.OPERATOR_ASSIGNMENT);
        }
        
        if(operator.getType() == TokenType.t_assign) {
            
            if(!is(rhsType, lhs.getCXType())) {
                setIsFailurePoint(rhs);
                throw new IncorrectTypeError(lhs.getCXType(), rhsType, lhs.findFirstToken(), rhs.findFirstToken());
            }
            //if(!rhsType.is(lhs.getCXType(), getEnvironment())) throw new IncorrectTypeError(lhs.getCXType(), rhsType);
            
        } else if(operator.getType() == TokenType.t_operator_assign) {
        
        
        
        } else throw new IllegalArgumentException();
        
        node.setType(CXPrimitiveType.VOID);
        return true;
    }
}
