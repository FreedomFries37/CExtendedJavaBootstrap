package radin.core.output.core.input.frontend.v2.parsing.grammars;

import radin.core.lexical.TokenType;
import radin.core.output.core.input.frontend.v2.parsing.structure.GrammarBuilder;
import radin.core.output.core.input.frontend.v2.parsing.structure.Symbol;

import static radin.core.lexical.TokenType.*;

public class ExpressionGrammar extends GrammarBuilder<TokenType> {
    
    public ExpressionGrammar() {
        super();
    
        Symbol T = symbol("Expression");
        setStartingSymbol(T);
        
        addProduction("Additive", "Additive", t_add, symbol("Multiplicative"));
        addProduction("Additive", "Additive", t_minus, symbol("Multiplicative"));
        addProduction("Additive", symbol("Multiplicative"));
        
        addProduction("Multiplicative", "Multiplicative", t_fwslash, symbol("CastExpression"));
        addProduction("Multiplicative", "Multiplicative", t_star, "CastExpression");
        addProduction("Multiplicative", "Multiplicative", t_percent, "CastExpression");
        addProduction("Multiplicative", "CastExpression");
        
        addProduction("CastExpression", symbol("UnaryExpression"));
        addProduction("CastExpression", t_lpar, symbol("TypeName"),  t_rpar, symbol("UnaryExpression"));
    
        addProduction("UnaryExpression", t_inc, symbol("UnaryExpression"));
        addProduction("UnaryExpression", t_dec, symbol("UnaryExpression"));
        addProduction("UnaryExpression", t_and, symbol("CastExpression"));
        addProduction("UnaryExpression", t_star, symbol("CastExpression"));
        addProduction("UnaryExpression", t_add, symbol("CastExpression"));
        addProduction("UnaryExpression", t_minus, symbol("CastExpression"));
        addProduction("UnaryExpression", t_not, symbol("CastExpression"));
        addProduction("UnaryExpression", t_bang, symbol("CastExpression"));
        addProduction("UnaryExpression", t_sizeof, symbol("UnaryExpression"));
        addProduction("UnaryExpression", t_sizeof, t_lpar, symbol("TypeName"), t_rpar);
        addProduction("UnaryExpression", symbol("PostFixExpression"));
        
        addProduction("PostFixExpression", symbol("PrimaryExpression"));
        addProduction("PostFixExpression", symbol("PostFixExpression"), t_lbrac, symbol("Expression"), t_rbrac);;
        addProduction("PostFixExpression", symbol("PostFixExpression"), t_lpar, t_rpar);
        addProduction("PostFixExpression", symbol("PostFixExpression"), t_lpar, symbol("ArgumentExpressionList"),
                t_rpar);
        addProduction("PostFixExpression", symbol("PostFixExpression"), t_dot, t_id);
        addProduction("PostFixExpression", symbol("PostFixExpression"), t_arrow, t_id);
        addProduction("PostFixExpression", symbol("PostFixExpression"), t_inc);
        addProduction("PostFixExpression", symbol("PostFixExpression"), t_dec);
        
        addProduction("PrimaryExpression", t_id);
        addProduction("PrimaryExpression", t_literal);
        addProduction("PrimaryExpression", t_string);
        addProduction("PrimaryExpression", t_lpar, "Expression", t_rpar);
        addProduction("PrimaryExpression", t_new, "TypeName", t_lpar, "ArgumentExpressionList", t_rpar);
    
        addProduction("ShiftExpression", "Additive");
        addProduction("ShiftExpression", "ShiftExpression", t_lshift, symbol("Additive"));
        addProduction("ShiftExpression", "ShiftExpression", t_rshift, "Additive");
    
        addProduction("RelationalExpression", "ShiftExpression");
        addProduction("RelationalExpression", "RelationalExpression", t_lt, symbol("ShiftExpression"));
        addProduction("RelationalExpression", "RelationalExpression", t_lte, "ShiftExpression");
        addProduction("RelationalExpression", "RelationalExpression", t_gt, symbol("ShiftExpression"));
        addProduction("RelationalExpression", "RelationalExpression", t_gte, "ShiftExpression");
    
        addProduction("EqualityExpression", "RelationalExpression");
        addProduction("EqualityExpression", "EqualityExpression", t_eq, symbol("RelationalExpression"));
        addProduction("EqualityExpression", "EqualityExpression", t_neq, "RelationalExpression");
    
        addProduction("AndExpression", "EqualityExpression");
        addProduction("AndExpression", "AndExpression", t_and, symbol("EqualityExpression"));
    
        addProduction("ExclusiveOrExpression", "AndExpression");
        addProduction("ExclusiveOrExpression", "ExclusiveOrExpression", t_crt, symbol("AndExpression"));
    
        addProduction("InclusiveOrExpression", "ExclusiveOrExpression");
        addProduction("InclusiveOrExpression", "InclusiveExpression", t_bar, symbol("ExclusiveOrExpression"));
    
        addProduction("LogicalAndExpression", "InclusiveOrExpression");
        addProduction("LogicalAndExpression", "LogicalAndExpression", t_dand, symbol("InclusiveOrExpression"));
    
        addProduction("LogicalOrExpression", "LogicalAndExpression");
        addProduction("LogicalOrExpression", "LogicalOrExpression", t_dor, symbol("LogicalAndExpression"));
    
        addProduction("ConditionalExpression", "LogicalOrExpression");
        addProduction("ConditionalExpression", "LogicalOrExpression", t_qmark, "Expression", t_colon,
                "ConditionalExpression");
        
        addProduction("AssignmentExpression", "ConditionalExpression");
        addProduction("AssignmentExpression", "PostFixExpression", t_assign, "AssignmentExpression");
        addProduction("AssignmentExpression", "PostFixExpression", t_operator_assign, "AssignmentExpression");
        
        addProduction("Expression", "AssignmentExpression");
        addProduction("Expression", "Expression", t_comma, "AssignmentExpression");
    
        addProduction("ConstantExpression", "ConditionalExpression");
        
        addProduction("ArgumentExpressionList", "AssignmentExpression");
        addProduction("ArgumentExpressionList", "ArgumentExpressionList", t_comma, "AssignmentExpression");
    }
}
