package radin.core.semantics;

import radin.core.lexical.Token;

import java.util.List;

import static radin.core.semantics.ASTNodeType.ast;

public class TokenStoringAbstractSyntaxNode extends AbstractSyntaxNode {

    private List<? extends Token> tokens;
    
    public TokenStoringAbstractSyntaxNode(List<? extends Token> tokens) {
        super(ast);
        this.tokens = tokens;
    }
    
    public List<? extends Token> getTokens() {
        return tokens;
    }
}
