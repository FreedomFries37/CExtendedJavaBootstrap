package radin.v2.parsing.grammars;

import radin.core.lexical.TokenType;
import radin.v2.parsing.structure.GrammarBuilder;

import static radin.core.lexical.TokenType.*;

public class TestGrammar extends GrammarBuilder<TokenType> {
    
    
    public TestGrammar() {
        addProduction("stmt_list", "stmt_list", symbol("stmt"));
        addProduction("stmt_list", symbol("stmt"));
        
        addProduction("stmt", t_id, t_assign, symbol("expr"));
        addProduction("stmt", t_rshift, symbol("expr"));
        addProduction("stmt", t_lshift, t_id);
        
        addProduction("expr", symbol("term"));
        addProduction("expr", "expr", symbol("add_op"), symbol("term"));
    
        addProduction("add_op", t_add);
        addProduction("add_op", t_minus);
    
        addProduction("term", symbol("factor"));
        addProduction("term", "term", symbol("mult_op"), symbol("factor"));
    
        addProduction("mult_op", t_star);
        addProduction("mult_op", t_fwslash);
        
        addProduction("factor", t_lpar, "expr", t_rpar);
        addProduction("factor", t_id);
        addProduction("factor", t_literal);
        
        setStartingSymbol(symbol("stmt_list"));
    }
}
