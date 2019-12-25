package radin.parsing;

import radin.interphase.lexical.Token;
import radin.lexing.Lexer;
import radin.interphase.lexical.TokenType;

import java.awt.desktop.OpenURIEvent;
import java.util.Arrays;
import java.util.HashSet;

public class Parser extends BasicParser {
    
    private HashSet<String> typedefed;
    
    public Parser(Lexer lexer, String... types) {
        super(lexer);
        typedefed = new HashSet<>();
        typedefed.addAll(Arrays.asList(types));
    }
    
    public HashSet<String> getTypedefed() {
        return typedefed;
    }
    
    private boolean isTypeName(String image) {
        return typedefed.contains(image);
    }
    
    
    @Override
    protected Token getCurrent() {
        Token output = super.getCurrent();
        if(output.getType().equals(TokenType.t_id) && isTypeName(output.getImage())) {
            output = new Token(TokenType.t_typename, output.getImage());
        }
        return output;
    }
    
    public CategoryNode parse() {
        CategoryNode output = new CategoryNode("Program");
        lexer.reset();
        if(!parseTopLevelDecsList(output)) {
            return null;
        }
        return output;
    }
    
    private boolean parseTopLevelDecsList(CategoryNode parent) {
        CategoryNode output = new CategoryNode("TopLevelDecsList");
    
        if(!getCurrentType().equals(TokenType.t_eof)) {
            if (!parseTopLevelDeclaration(output)) return false;
            if (!parseTopLevelDecsTail(output)) return false;
        }
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseTopLevelDeclaration(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TopLevelDeclaration");
    
        switch (getCurrentType()) {
            case t_typedef: {
                if(!parseTypeDef(child)) return false;
                if(!consume(TokenType.t_semic)) return error("Missing semi-colon");
                break;
            }
            default: {
                if (!parseExpression(child)) return false;
                if (!consume(TokenType.t_semic)) return false;
                break;
            }
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTopLevelDecsTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TopLevelDecsTail");
        
        if(!getCurrentType().equals(TokenType.t_eof)) {
            if(!parseTopLevelDecsList(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    
    
    private boolean parseAssignment(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Assignment");
        
        
        parent.addChild(child);
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
        
        if(match(TokenType.t_dor)) {
            consumeAndAddAsLeaf(child);
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
        
        if(match(TokenType.t_dand)) {
            consumeAndAddAsLeaf(child);
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
        
        if(match(TokenType.t_bar)) {
            consumeAndAddAsLeaf(child);
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
        
        if(match(TokenType.t_crt)) {
            consumeAndAddAsLeaf(child);
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
        
        if(match(TokenType.t_and)) {
            consumeAndAddAsLeaf(child);
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
            case t_lpar: {
                boolean attemptParse = attemptParse(this::parseCastExpression, child);
                if(!attemptParse) {
                    if(!parseAtom(child)) return false;
                    if(!parseAtomTail(child)) return false;
                }
                break;
            }
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
    
    private boolean parseCastExpression(CategoryNode parent){
        CategoryNode child = new CategoryNode("CastExpression");
        
        if(!consume(TokenType.t_lpar)) return false;
        if(!parseTypeName(child)) return false;
        if(!consume(TokenType.t_rpar)) return false;
        if(!parseFactor(child)) return false;
        
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
        CategoryNode child = new CategoryNode("Atom");
        
        switch (getCurrentType()) {
            case t_lpar: {
                next();
                if(!parseExpression(child)) return false;
                if(!consume(TokenType.t_rpar)) return error("Missing matching )");
                break;
            }
            case t_id: {
                consumeAndAddAsLeaf(child);
                if(!parseFunctionCall(child)) return false;
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
                if(!consume(TokenType.t_rbrac)) return error("Missing matching ]");
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
                if(!parseArgsList(output)) return false;
                break;
            }
            default:
                break;
        }
        
        parent.addChild(output);
        return true;
    }
    
   
    
    // TYPES
    
    private boolean parseTypeName(CategoryNode parent) {
        CategoryNode output = new CategoryNode("TypeName");
        
        if(!parseSpecsAndQuals(output)) return false;
        if(!parseAbstractDeclarator(output)) return false;
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseSpecsAndQuals(CategoryNode parent) {
        CategoryNode output = new CategoryNode("SpecsAndQuals");
        
        switch (getCurrentType()) {
            case t_const: {
                if(!parseQualifier(output)) return false;
                if(!parseSpecsAndQualsTail(output)) return false;
                break;
            }
            default:
                if(!parseSpecifier(output)) return false;
                if(!parseSpecsAndQualsTail(output)) return false;
                break;
        }
       
        
        parent.addChild(output);
        return true;
    }
    
    
    
    
    private boolean parseSpecsAndQualsTail(CategoryNode parent) {
        CategoryNode output = new CategoryNode("SpecsAndQualsTail");
        
        switch (getCurrentType()) {
            case t_id: {
                if(!isTypeName(getCurrent().getImage())) break;
            }
            case t_typename:
            case t_void:
            case t_char:
            case t_int:
            case t_long:
            case t_float:
            case t_double:
            case t_unsigned:
            case t_struct:
            case t_union:
            case t_class: {
                if(!parseSpecsAndQuals(output)) return false;
                break;
            }
            default:
                break;
        }
        
        parent.addChild(output);
        return true;
    }
    
   
    
    private boolean parseQualifier(CategoryNode parent) {
        CategoryNode output = new CategoryNode("Qualifier");
        
        if(!match(TokenType.t_const)) return false;
        consumeAndAddAsLeaf(output);
        
        parent.addChild(output);
        return true;
    }
    
    
    private boolean parseQualifierList(CategoryNode parent) {
        CategoryNode output = new CategoryNode("QualifierList");
        
        if(!parseQualifier(output)) return false;
        if(!parseQualifierListTail(output)) return false;
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseQualifierListTail(CategoryNode parent) {
        CategoryNode output = new CategoryNode("QualifierListTail");
        
        if(match(TokenType.t_const)) {
            if(!parseQualifierList(parent)) return false;
        }
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseSpecifier(CategoryNode parent) {
        CategoryNode output = new CategoryNode("Specifier");
    
        switch (getCurrentType()) {
            
            case t_id: {
                if (!isTypeName(getCurrent().getImage())) return error("Not valid specifier");
                consumeAndAddAsLeaf(output);
                break;
            }
            case t_typename:
            case t_void:
            case t_char:
            case t_int:
            case t_long:
            case t_float:
            case t_double:
            case t_unsigned: {
                consumeAndAddAsLeaf(output);
                break;
            }
            case t_struct:
            case t_union: {
                if(!parseStructOrUnionSpecifier(output)) return false;
                break;
            }
            case t_class: {
                if(!parseClassSpecifier(output)) return false;
                break;
            }
            default:
                return error("Not valid specifier");
        }
        
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseStructOrUnionSpecifier(CategoryNode parent) {
        CategoryNode output = new CategoryNode("StructOrUnionSpecifier");
    
        if(!parseStructOrUnion(output)) return false;
        switch (getCurrentType()) {
            case t_id: {
                if(!consumeAndAddAsLeaf(TokenType.t_id, output)) return false;
                if(consume(TokenType.t_lcurl)) {
                    
                    if(!parseStructDeclarationList(output)) return false;
                    if(!consume(TokenType.t_rcurl)) return false;
                }
                break;
            }
            case t_lcurl: {
                getNext();
                if(!parseStructDeclarationList(output)) return false;
                if(!consume(TokenType.t_rcurl)) return false;
            }
            case t_typename: {
                return error("Can't use typename in struct/union declaration");
            }
        }
        
    
        parent.addChild(output);
        return true;
    }
    
    private boolean parseStructOrUnion(CategoryNode parent) {
        CategoryNode output = new CategoryNode("StructOrUnion");
        
        if(!(match(TokenType.t_struct) || match(TokenType.t_union))) return false;
        consumeAndAddAsLeaf(output);
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseClassSpecifier(CategoryNode parent) {
        CategoryNode output = new CategoryNode("ClassSpecifier");
        
        if(!consume(TokenType.t_class)) return false;
        if(!match(TokenType.t_id)) return false;
        consumeAndAddAsLeaf(output);
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseAbstractDeclarator(CategoryNode parent) {
        CategoryNode output = new CategoryNode("AbstractDeclarator");
    
        switch (getCurrentType()) {
            case t_star: {
                if(!parsePointer(output)) return false;
                if(!parseDirectAbstractDeclarator(output)) return false;
                break;
            }
            case t_lpar:
            case t_lbrac: {
                if(!parseDirectAbstractDeclarator(output)) return false;
            }
            default:
                break;
        }
    
        parent.addChild(output);
        return true;
    }
    
    private boolean parseDirectAbstractDeclarator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DirectAbstractDeclarator");
        
        switch (getCurrentType()) {
            case t_lbrac: {
                if(!consumeAndAddAsLeaf(TokenType.t_lbrac, child)) return false;
                if(!consumeAndAddAsLeaf(TokenType.t_rbrac, child)) return error("Missing matching ]");
                if(!parseDirectAbstractDeclarator(child)) return false;
                break;
            }
            case t_lpar: {
                // TODO add this
                
                break;
            }
            default:
                break;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parsePointer(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Pointer");
        
        if(!consume(TokenType.t_star)) return false;
        if (match(TokenType.t_const)) {
            if (!parseQualifierList(child)) return false;
        }
        if(match(TokenType.t_star)) {
            if(!parsePointer(child)) return false;
        }
        
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTypeDef(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TypeDef");
        
        if(!consume(TokenType.t_typedef)) return false;
        if(!parseTypeName(child)) return false;
        if(!match(TokenType.t_id)) return false;
        String typename = getCurrent().getImage();
        consumeAndAddAsLeaf(child);
        
        typedefed.add(typename);
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDeclarator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Declarator");
        
        if(match(TokenType.t_star)) {
            if(!parsePointer(child)) return false;
        }
        
        if(!parseDirectDeclarator(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDirectDeclarator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DirectDeclarator");
        
        switch (getCurrentType()) {
            case t_id: {
                if(isTypeName(getCurrent().getImage())) return error("Can't use typename as identifier");
                consumeAndAddAsLeaf(child);
                break;
            }
            case t_lpar: {
                getNext();
                if(!parseDeclarator(child)) return false;
                if(!consume(TokenType.t_rpar)) return error("Missing matching )");
                break;
            }
            default:
                return false;
        }
        
        if(!parseDirectDeclaratorTail(child)) return false;
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDirectDeclaratorTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DirectDeclaratorTail");
    
        switch (getCurrentType()) {
            case t_lpar: {
                // TODO: implement this after paramter type list implemented
                break;
            }
            case t_rbrac: {
                getNext();
                if(!consume(TokenType.t_rbrac)) return false;
                if(!parseDirectDeclaratorTail(child)) return false;
                break;
            }
            default:
                break;
        }
        
        
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseConstantExpression(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ConstantExpression");
        
        if(!consumeAndAddAsLeaf(TokenType.t_literal, child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclarator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclarator");
        
        if(consume(TokenType.t_colon)) {
            if(!parseConstantExpression(child)) return false;
        } else {
            
            if(!parseDeclarator(child)) return false;
            if(consume(TokenType.t_colon)) {
                if(!parseConstantExpression(child)) return false;
            }
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclaratorList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclaratorList");
        
        if(!parseStructDeclarator(child)) return false;
        if(!parseStructDeclaratorListTail(child)) return false;
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclaratorListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclaratorListTail");
        
        if(consume(TokenType.t_colon)) {
            if(!parseStructDeclaratorList(child)) return false;
        }
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclaration(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclaration");
     
        if(!parseSpecsAndQuals(child)) return false;
        if(!parseStructDeclaratorList(child)) return false;
        if(!consume(TokenType.t_semic)) return error("Missing ;");
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclarationList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclarationList");
        
        if(!parseStructDeclaration(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclarationListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclarationListTail");
        
        if(!match(TokenType.t_rcurl)) {
            if(!parseStructDeclaratorList(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseParameterTypeList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ParameterTypeList");
        
        if(!parseParameterList(child)) return false;
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseParameterList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ParameterList");
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseParameterListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ParameterListTail");
        
        if(consume(TokenType.t_colon)) {
            if(!parseParameterList(child)) return false;
        }
        
        
        parent.addChild(child);
        return true;
    }
}
