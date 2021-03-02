package radin.midanalysis.pattern_replacement.singletons;

import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.TypeEnvironment;
import radin.core.utility.Option;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.pattern_replacement.PatternSingleton;

public class TokenSingleton extends PatternSingleton {
    
    private TokenType tokenType;
    private Option<String> image;
    
    public TokenSingleton(TokenType tokenType, Option<String> image) {
        this.tokenType = tokenType;
        this.image = image;
    }
    
    @Override
    public boolean match(TypeAugmentedSemanticNode node, TypeEnvironment environment) {
        Token token = node.getToken();
        if(token == null) return false;
        if(!token.getType().equals(tokenType)) return false;
        if(image.isSome()) {
            return image.unwrap().equals(token.getImage());
        }
        return true;
    }
}
