package radin.parsing;

import radin.lexing.Lexer;
import radin.lexing.Token;
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
        CategoryNode child = new CategoryNode("Expression");
        
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
                if(!parseDoubleOr(child)) return false;
                if(!parseDoubleOrTail(child)) return false;
                if(!parseExpressionTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseExpressionTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ExpressionTail");
        
        if(consume(TokenType.t_qmark)) {
            if(!parseExpression(child)) return false;
            if(!consume(TokenType.t_colon)) return false;
            if(!parseExpression(child)) return false;
        }
        
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDoubleOr(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DoubleOr");
        
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
                if(!parseDoubleAnd(child)) return false;
                if(!parseDoubleAndTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDoubleOrTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DoubleOrTail");
        
        if(consume(TokenType.t_dor)) {
            if(!parseDoubleOr(child)) return false;
            if(!parseDoubleOrTail(child)) return false;
        }
        
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDoubleAnd(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DoubleAnd");
        
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
                if(!parseOr(child)) return false;
                if(!parseOrTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDoubleAndTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DoubleAndTail");
        
        if(consume(TokenType.t_dand)) {
            if(!parseDoubleAnd(child)) return false;
            if(!parseDoubleAndTail(child)) return false;
        }
        
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseOr(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Or");
        
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
                if(!parseNot(child)) return false;
                if(!parseNotTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseOrTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("OrTail");
        
        if(consume(TokenType.t_bar)) {
            if(!parseOr(child)) return false;
            if(!parseOrTail(child)) return false;
        }
        
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseNot(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Not");
        
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
                if(!parseAnd(child)) return false;
                if(!parseAndTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseNotTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("NotTail");
        
        if(consume(TokenType.t_crt)) {
            if(!parseNot(child)) return false;
            if(!parseNotTail(child)) return false;
        }
        
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseAnd(CategoryNode parent) {
        CategoryNode child = new CategoryNode("And");
        
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
                if(!parseEquation(child)) return false;
                if(!parseEquationTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseAndTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("AndTail");
        
        if(consume(TokenType.t_and)) {
            if(!parseAnd(child)) return false;
            if(!parseAndTail(child)) return false;
        }
        
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseEquation(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Equation");
        
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
                if(!parseC(child)) return false;
                if(!parseCTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseEquationTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("EquationTail");
        
        
        switch (getCurrentType()) {
            case t_eq:
            case t_neq: {
                consumeAndAddAsLeaf(child);
                if(!parseEquation(child)) return false;
                if(!parseEquationTail(child)) return false;
            }
            default:
                break;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseC(CategoryNode parent) {
        CategoryNode child = new CategoryNode("C");
        
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
                if(!parseG(child)) return false;
                if(!parseGTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseCTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("CTail");
        
        
        switch (getCurrentType()) {
            case t_gte:
            case t_gt:
            case t_lte:
            case t_lt: {
                consumeAndAddAsLeaf(child);
                if(!parseC(child)) return false;
                if(!parseCTail(child)) return false;
            }
            default:
                break;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseG(CategoryNode parent) {
        CategoryNode child = new CategoryNode("G");
        
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
                if(!parseT(child)) return false;
                if(!parseTTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseGTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("GTail");
        
        
        switch (getCurrentType()) {
            case t_lshift:
            case t_rshift: {
                consumeAndAddAsLeaf(child);
                if(!parseG(child)) return false;
                if(!parseGTail(child)) return false;
            }
            default:
                break;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseT(CategoryNode parent) {
        CategoryNode child = new CategoryNode("T");
        
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
                if(!parseFactor(child)) return false;
                if(!parseFactorTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TTail");
        
        
        switch (getCurrentType()) {
            case t_add:
            case t_minus:{
                consumeAndAddAsLeaf(child);
                if(!parseT(child)) return false;
                if(!parseTTail(child)) return false;
            }
            default:
                break;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseFactor(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Factor");
        
        switch (getCurrentType()) {
            case t_minus:
            case t_not:
            case t_bang:
            case t_star:
            case t_and:
            case t_add:
            case t_inc:
            case t_dec: {
                consumeAndAddAsLeaf(child);
                if(!parseFactor(child)) return false;
                break;
            }
            case t_string:
            case t_literal: {
                consumeAndAddAsLeaf(child);
                break;
            }
            case t_lpar:
            case t_id: {
                if(!parseAtom(child)) return false;
                if(!parseAtomTail(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseFactorTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("FactorTail");
        
        
        switch (getCurrentType()) {
            case t_star:
            case t_fwslash:
            case t_percent:{
                consumeAndAddAsLeaf(child);
                if(!parseFactor(child)) return false;
                if(!parseFactorTail(child)) return false;
            }
            default:
                break;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseAtom(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Factor");
        
        switch (getCurrentType()) {
            case t_lpar: {
                next();
                if(!parseExpression(child)) return false;
                if(!consume(TokenType.t_rpar)) return false;
                break;
            }
            case t_id: {
                consumeAndAddAsLeaf(child);
                break;
            }
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseAtomTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("AtomTail");
        
        
        switch (getCurrentType()) {
            case t_arrow:
            case t_dot: {
                consumeAndAddAsLeaf(child);
                if(!match(TokenType.t_id)) return false;
                consumeAndAddAsLeaf(child);
                if(!parseFunctionCall(child)) return false;
                if(!parseAtomTail(child)) return false;
                break;
            }
            case t_lbrac: {
                consumeAndAddAsLeaf(child);
                if(!parseExpression(child));
                if(!match(TokenType.t_rbrac)) return false;
                if(!parseAtomTail(child)) return false;
                break;
            }
            case t_dec:
            case t_inc: {
                consumeAndAddAsLeaf(child);
                break;
            }
            default:
                break;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseFunctionCall(CategoryNode parent) {
        CategoryNode output = new CategoryNode("FunctionCall");
        
        if(consume(TokenType.t_lpar)) {
            if(!parseArgsList(output)) return false;
            if(!consume(TokenType.t_rpar)) return false;
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
