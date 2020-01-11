package radin.core.output.core.input.frontend.v2.parsing.grammars;

import radin.core.output.core.input.frontend.v2.parsing.structure.GrammarBuilder;
import radin.core.lexical.TokenType;

public class TestGrammar extends GrammarBuilder<TokenType> {
    
    
    public TestGrammar() {
        addProduction("stmt_list", "stmt_list", symbol("stmt"));
        addProduction("stmt_list", symbol("stmt"));
        
        addProduction("stmt", TokenType.t_id, TokenType.t_assign, symbol("expr"));
        addProduction("stmt", TokenType.t_rshift, symbol("expr"));
        addProduction("stmt", TokenType.t_lshift, TokenType.t_id);
        
        addProduction("expr", symbol("term"));
        addProduction("expr", "expr", symbol("add_op"), symbol("term"));
    
        addProduction("add_op", TokenType.t_add);
        addProduction("add_op", TokenType.t_minus);
    
        addProduction("term", symbol("factor"));
        addProduction("term", "term", symbol("mult_op"), symbol("factor"));
    
        addProduction("mult_op", TokenType.t_star);
        addProduction("mult_op", TokenType.t_fwslash);
        
        addProduction("factor", TokenType.t_lpar, "expr", TokenType.t_rpar);
        addProduction("factor", TokenType.t_id);
        addProduction("factor", TokenType.t_literal);
        
        setStartingSymbol(symbol("stmt_list"));
    }
}
