package radin.core.output.midanalysis.constantexpressions;

import radin.core.chaining.IToolChain;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.ASTMeaningfulNode;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

public class ConstantExpressionDeterminer implements IToolChain<ASTMeaningfulNode<?>, Number> {
    
    static Number performOp(Number rhs, Number lhs, String operator) {
        if(rhs instanceof Double) {
            return performOp(rhs.doubleValue(), lhs, operator);
        } else {
            return null;
        }
    }
    
    private static Number performOp(double rhs, Number lhs, String operator) {
        switch (operator) {
            case "*": {
                return rhs * lhs.doubleValue();
            }
            case "/": {
                return rhs / lhs.doubleValue();
            }
            case "+": {
                return rhs + lhs.doubleValue();
            }
            case "-": {
                return rhs - lhs.doubleValue();
            }
            case "%": {
                return rhs % lhs.doubleValue();
            }
            default:
                throw new IllegalStateException();
        }
    }
    private static Number performOp(float rhs, Number lhs, String operator) {
        switch (operator) {
            case "*": {
                return rhs * lhs.floatValue();
            }
        }
        return null;
    }
    private static Number performOp(long rhs, Number lhs, String operator) {
        return null;
    }
    private static Number performOp(int rhs, Number lhs, String operator) {
        return null;
    }
    private static Number performOp(short rhs, Number lhs, String operator) {
        return null;
    }
    private static Number performOp(byte rhs, Number lhs, String operator) {
        return null;
    }
    
    @Override
    public Number invoke(ASTMeaningfulNode<?> input) {
        switch (input.getTreeType()) {
            case binop:
                break;
            case uniop: {
                String opImage = input.getChildWithASTType(ASTNodeType.operator).getToken().getImage();
                Number inner = invoke(input.getChildAtIndex(1));
                switch (opImage) {
                    case "+": {
                        return inner;
                    }
                    case "-": {
                        return performOp(inner, -1, "*");
                    }
                    default:
                }
            }
            case ternary: {
                Number ifExpr = invoke(input.getChildAtIndex(0));
                if(ifExpr == null) {
                    return null;
                } else if(ifExpr.shortValue() == 0) { // false
                    return invoke(input.getChildAtIndex(2));
                } else {
                    return invoke(input.getChildAtIndex(1));
                }
            }
            case literal:
                try {
                    return NumberFormat.getInstance().parse(input.getToken().getImage());
                } catch (ParseException e) {
                    return null;
                }
            case id: {
                // TODO: 1/26/2020 Implement constexpr ids
                return null;
            }
            default:
                break;
        }
        return null;
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return null;
    }
}
