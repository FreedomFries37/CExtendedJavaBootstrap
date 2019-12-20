package radin.parsing;

import radin.lexing.Lexer;
import radin.lexing.TokenType;

public class Parser extends BasicParser {
    
    
    public Parser(Lexer lexer) {
        super(lexer);
    }
    
    public ParseNode parse() {
        CategoryNode output = new CategoryNode("program");
        
        if(!parse_top_level_decs(output)) return null;
        return output;
    }
    
    private boolean parse_top_level_decs(CategoryNode parent) {
    
        
        
        return true;
    }
    
    private boolean parseExpression(CategoryNode parent) {
        CategoryNode output = new CategoryNode("Expression");
    
        switch (getCurrentType()) {
            case t_minus:
            case t_not:
            case t_bang:
            case t_star:
            case t_and:
            case t_add:
            case t_inc:
            case t_dec:
            case t_lpar:
            case t_literal:
            case t_id:
            case t_string: {
                if(!parseDoubleOr(output)) return false;
                if(!parseDoubleOrTail(output)) return false;
                if(!parseExpressionTail(output)) return false;
                break;
            }
            default:
                return false;
        }
    
        parent.addChild(output);
        return true;
    }
    
    
    
    private boolean parseArgsList(CategoryNode parent) {
        CategoryNode output = new CategoryNode("ArgsList");
    
        switch (getCurrentType()) {
            case t_minus:
            case t_not:
            case t_bang:
            case t_star:
            case t_and:
            case t_add:
            case t_inc:
            case t_dec:
            case t_lpar:
            case t_literal:
            case t_id:
            case t_string: {
                if(!parseExpression(output)) return false;
                if(!parseArgsListTail(output)) return false;
                break;
            }
            default:
                break;
        }
    
        parent.addChild(output);
        return true;
    }
    
    private boolean parseArgsListTail(CategoryNode parent) {
        CategoryNode output = new CategoryNode("ArgsListTail");
        
        switch (getCurrentType()) {
            case t_comma: {
                getNextType();
                if(!parseExpression(parent)) return false;
                if(!parseArgsList(parent)) return false;
                break;
            }
            default:
                break;
        }
        
        parent.addChild(output);
        return true;
    }
    
    
    
    // ARITHMETIC
    
    
    
}
