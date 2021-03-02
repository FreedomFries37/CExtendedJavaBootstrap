package radin.midanalysis.pattern_replacement.singletons;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;
import radin.core.utility.Option;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.pattern_replacement.PatternSingleton;

public class ExpressionSingleton extends PatternSingleton {

    private Option<CXType> type;
    
    public ExpressionSingleton(Option<CXType> type) {
        this.type = type;
    }
    
    @Override
    public boolean match(TypeAugmentedSemanticNode node, TypeEnvironment environment) {
        if(nodeIsType(node, ASTNodeType.expressionTypes())) {
            if(type.isSome()) {
                if(node.getCXType() == null) return false;
                CXType exprType = node.getCXType();
                CXType lookingFor = type.unwrap();
                return environment.isStrict(exprType, lookingFor);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    
}
