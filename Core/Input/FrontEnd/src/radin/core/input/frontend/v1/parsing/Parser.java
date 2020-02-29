package radin.core.input.frontend.v1.parsing;

import jdk.jshell.spi.ExecutionControl;
import radin.core.input.ITokenizer;
import radin.core.input.frontend.directastparsing.ASTParser;
import radin.core.input.frontend.v1.semantics.SynthesizedMissingException;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.TokenStoringAbstractSyntaxNode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static radin.core.lexical.TokenType.*;

public class Parser extends BasicParser {
    
    private HashSet<String> typedefed;
    private HashSet<String> compoundTypeNames;
    private Stack<HashSet<String>> typedefStack;
    private Stack<HashSet<String>> compoundTypesStack;
   
    public Parser() {
        super();
        typedefed = new HashSet<>();
        compoundTypeNames = new HashSet<>();
        typedefStack = new Stack<>();
        typedefStack.add(new HashSet<>());
        compoundTypesStack = new Stack<>();
        
    }
    
    
    public HashSet<String> getTypedefed() {
        return typedefed;
    }
    
    private boolean isTypeName(String image) {
        return typedefed.contains(image) || typedefStack.peek().contains(image);
    }
    
    private boolean isCompoundTypeName(String image) {
        return compoundTypeNames.contains(image);
    }
    
    @Override
    public Token getCurrent() {
        Token output = super.getCurrent();
        if (output.getType().equals(TokenType.t_id) && (isTypeName(output.getImage()))) {
            
            
            output = new Token(TokenType.t_typename, output.getImage())
                    .addColumnAndLineNumber(output.getColumn(), output.getLineNumber());
        }
        return output;
    }
    
    public void typedefClosure() {
        typedefStack.push(new HashSet<>(typedefStack.peek()));
    }
    
    public void releaseTypeDefClosure() {
        typedefStack.pop();
    }
    
    public void addTempTypeDef(String name) {
        typedefStack.peek().add(name);
    }
    
    @Override
    protected void pushState() {
        super.pushState();
        typedefClosure();
        typedefStack.peek().addAll(new HashSet<>(typedefed));
        compoundTypesStack.push(new HashSet<>(compoundTypeNames));
    }
    
    @Override
    protected boolean popState() {
        boolean b = super.popState();
        if (b) {
            typedefStack.pop();
            compoundTypesStack.pop();
        }
        return b;
    }
    
    @Override
    protected boolean applyState() {
        boolean b = super.applyState();
        if (b) {
            typedefed = typedefStack.pop();
            compoundTypeNames = compoundTypesStack.pop();
        }
        return b;
    }
    
    @Override
    public void reset() {
        typedefed.clear();
        compoundTypeNames.clear();
        typedefStack.clear();
        typedefStack.add(new HashSet<>());
        compoundTypesStack.clear();
    }
    
    protected Token undoTypeName(Token other) {
        if (other.getType() != TokenType.t_typename) return other;
        return new Token(TokenType.t_id, other.getImage()).addColumnAndLineNumber(other.getColumn(), other.getColumn());
    }
    
    public CategoryNode parse() {
        CategoryNode output = new CategoryNode("Program");
        lexer.reset();
        if (!parseTopLevelDecsList(output)) {
            return null;
        }
        return output;
    }
    
    private boolean parseASTBlock(CategoryNode parent) {
        CategoryNode child = new CategoryNode("AST");
        
        if(!consume(t_ast)) return false;
        if(!consume(t_lcurl)) return false;
        List<Token> tokenList = new LinkedList<>();
        int level = 0;
        while (level >= 0) {
            if(match(t_lcurl)) {
                level++;
            } else if(match(t_rcurl)) {
                level--;
            }
            if(level >= 0) {
                tokenList.add(getCurrent());
                getNext();
            }
        }
        child.setInherit(new TokenStoringAbstractSyntaxNode(tokenList));
        if(!consume(t_rcurl)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseGenericDeclaration(CategoryNode parent) {
        CategoryNode child = new CategoryNode("GenericDeclaration");
        
        if(!consume(t_for)) return false;
        
        if(!consume(t_lt)) return false;
        if(!parseTypeParameterList(child)) return false;
        if(!consume(t_gt)) return error("Missing matching >");
        typedefClosure();
        
        /*
        CategoryNode ptr = child.getCategoryNode("IdentifierList");
        while (ptr != null) {
            Token id = ptr.getLeafNode(t_id).getToken();
            
            ptr = ptr.getCategoryNode("IdentifierListTail");
            if(ptr != null) {
                if (ptr.hasChildCategory("IdentifierList")) {
                    ptr = ptr.getCategoryNode("IdentifierList");
                } else ptr = null;
            }
        }
        */
    
        
        if (getCurrentType() == t_class) {
            error("GENERIC CLASSES NOT YET IMPLEMENTED");
        } else {
            if(!parseFunctionDefinition(child)) return false;
        }
    
    
        releaseTypeDefClosure();
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTypeParameter(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TypeParameter");
        
        if(!match(t_id)) return error("Type Parameter must be a valid identifier");
        consumeAndAddAsLeaf(t_id, child);
        addTempTypeDef(child.getLeafNode(t_id).getToken().getImage());
        if(match(t_colon)) {
            if(!parseInherit(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTypeParameterList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TypeParameterList");
        
        if(!parseTypeParameter(child)) return false;
        if(!parseTypeParameterListTail(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTypeParameterListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TypeParameterListTail");
        
        if(consume(t_comma)) {
            if (!parseTypeParameterList(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTopLevelDecsList(CategoryNode parent) {
        CategoryNode output = new CategoryNode("TopLevelDecsList");
        
        if (!getCurrentType().equals(TokenType.t_eof) && getCurrentType() != t_rcurl) {
            if (!parseTopLevelDeclaration(output)) return false;
            if (!parseTopLevelDecsTail(output)) return false;
        }
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseTopLevelDeclaration(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TopLevelDeclaration");
        
        if (match(t_lbrac)) {
            if (!parseCompilationTagList(child)) return false;
        }
        
        OUTER:
        switch (getCurrentType()) {
            case t_typedef: {
                if (!parseTypeDef(child)) return false;
                if (!consume(TokenType.t_semic)) return missingError("Missing semi-colon");
                break;
            }
            case t_class:
                switch (attemptParse(this::parseClassDeclaration, child)) {
                    case PARSED:
                        break OUTER;
                    case ROLLBACK:
                    case DESYNC:
                        break;
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
            case t_lpar:
            case t_const: {
                Token corresponding = getCurrent();
                if (!oneMustParse(child, this::parseFunctionDefinition, this::parseDeclaration)) {
                    return error("Could not parse declaration", true, corresponding);
                    //return error("Could not parse declaration",true, corresponding);
                }
                break;
            }
            case t_in: {
                if (!parseInIdentifier(child)) return false;
                break;
            }
            case t_implement: {
                if (!parseImplement(child)) return false;
                break;
            }
            case t_using: {
                if (!parseUsing(child)) return false;
                break;
            }
            case t_ast: {
                if(!parseASTBlock(child)) return false;
                break;
            }
            case t_for: {
                if(!parseGenericDeclaration(child)) return false;
                break;
            }
            default:
                return error("Not a valid top level declaration");
        }
        clearErrors();
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTopLevelDecsTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TopLevelDecsTail");
        
        if (!getCurrentType().equals(TokenType.t_eof) && getCurrentType() != t_rcurl) {
            if (!parseTopLevelDecsList(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseInIdentifier(CategoryNode parent) {
        CategoryNode child = new CategoryNode("InIdentifier");
        
        if (!consume(t_in)) return false;
        if (!consumeAndAddAsLeaf(t_id, child)) return error("Must be a valid identifier");
        if (getCurrentType() == t_lcurl) {
            getNext();
            if (!parseTopLevelDecsList(child)) return false;
            if (!consume(t_rcurl)) return error("Missing matching }");
        } else {
            if (!parseTopLevelDeclaration(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseImplement(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Implement");
        
        if (!consume(t_implement)) return false;
        if (!parseNamespacedType(child)) return error("Must be a valid typename");
        if (getCurrentType() == t_lcurl) {
            getNext();
            if (!parseImplementList(child)) return false;
            if (!consume(t_rcurl)) return error("Missing matching }");
        } else {
            if (!parseImplementation(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private String getInnerMost(CategoryNode namespace) {
        assert namespace.getCategory().equals("Namespace");
        
        if(namespace.hasChildCategory("Namespace")) {
            return getInnerMost(namespace.getCategoryNode("Namespace", 2));
        } else {
            return namespace.getLeafNode(t_id).getToken().getImage();
        }
    }
    
    private boolean parseUsing(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Using");
        
        if (!consume(t_using)) {
            return false;
        }
        if (!parseNamespace(child)) return false;
        String id = getInnerMost(child.getCategoryNode("Namespace"));
        typedefed.add(id);
        if (match(t_assign)) {
            if (!parseAlias(child)) return false;
        }
        if (!consume(t_semic)) return error("Missing semi-colon");
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseAlias(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Alias");
        
        if (!consume(t_assign)) return false;
        if (!consumeAndAddAsLeaf(t_id, child)) return error("Need if");
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseImplementList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ImplementList");
        
        if (!parseImplementation(child)) return false;
        if (!parseImplementListTail(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseImplementListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ImplementListTail");
        
        if (!match(t_rcurl)) {
            if (!parseImplementList(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseImplementation(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Implementation");
        
        if(!oneMustParse(child, this::parseFunctionDefinition, this::parseConstructorDefinition)) {
            return error("Illegal statement in implement section");
        }
        // if (!parseFunctionDefinition(child)) return error("Illegal statement in implement section");
        
        
        parent.addChild(child);
        return true;
    }
    
    
    private boolean parseFunctionDefinition(CategoryNode parent) {
        CategoryNode child = new CategoryNode("FunctionDefinition");
        
        
        switch (attemptParse(this::parseDeclarationSpecifiers, child)) {
            case PARSED:
            case ROLLBACK:
                break;
            case DESYNC:
                return false;
        }
        if (!parseDeclarator(child)) return false;
        if (!match(TokenType.t_lcurl)) {
            if (!parseDeclarationList(child)) return false;
        }
        if (!parseCompoundStatement(child)) return absorbErrors("Failed to compile compound statement");
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStatement(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Statement");
        
        switch (getCurrentType()) {
            case t_lcurl: {
                if (!parseCompoundStatement(child)) return false;
                break;
            }
            case t_if: {
                if (!parseSelectionStatement(child)) return false;
                break;
            }
            case t_while:
            case t_for:
            case t_do: {
                if (!parseIterationStatement(child)) return false;
                break;
            }
            case t_return: {
                if (!parseJumpStatement(child)) return false;
                break;
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
            case t_class:
            case t_const: {
                if (!parseDeclaration(child)) return false;
                break;
            }
            case t_id: {
                if (!oneMustParse(child, this::parseDeclaration, this::parseExpressionStatement)) return false;
                break;
            }
            default:
                if (!parseExpressionStatement(child)) return false;
                break;
            
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTopExpression(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TopExpression");
        
        if (!parseAssignmentExpression(child)) return false;
        if (consume(TokenType.t_comma)) {
            if (!parseTopExpression(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    
    private boolean parseExpressionStatement(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ExpressionStatement");
        
        switch (attemptParse(this::parseTopExpression, child)) {
            case PARSED:
            case ROLLBACK:
                break;
            case DESYNC:
                return false;
        }
        if (!consume(TokenType.t_semic)) {
            
            if (!recoverableMissingError("Missing semi-colon", t_semic, t_rcurl)) {
                return false;
            }
        }
        
        parent.addChild(child);
        return true;
    }
    
    
    private boolean parseCompoundStatement(CategoryNode parent) {
        CategoryNode child = new CategoryNode("CompoundStatement");
        
        
        if (!consume(TokenType.t_lcurl)) return error("missing { for compound statement");
        //attemptParse(this::parseDeclarationList, child);
        if(!match(t_rcurl)) {
            if(!parseStatementList(child)) return false;
        }
        if (!consume(TokenType.t_rcurl)) return error("missing matching } for compound statement");
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseIterationStatement(CategoryNode parent) {
        CategoryNode child = new CategoryNode("IterationStatement");
        
        switch (getCurrentType()) {
            case t_while: {
                consumeAndAddAsLeaf(child);
                if (!consume(TokenType.t_lpar)) return false;
                if (!parseTopExpression(child)) return false;
                if (!consume(TokenType.t_rpar)) return false;
                if (!parseStatement(child)) return false;
                break;
            }
            case t_do: {
                consumeAndAddAsLeaf(child);
                if (!parseStatement(child)) return false;
                if (!consume(TokenType.t_while)) return false;
                if (!consume(TokenType.t_lpar)) return false;
                if (!parseTopExpression(child)) return false;
                if (!consume(TokenType.t_rpar)) return false;
                if (!consume(TokenType.t_semic)) return missingError("Missing semi-colon");
                break;
            }
            case t_for: {
                consumeAndAddAsLeaf(child);
                if (!consume(TokenType.t_lpar)) return false;
                if (!oneMustParse(child, this::parseDeclaration, this::parseExpressionStatement)) {
                    return false;
                }
                /*
                if(!attemptParse(this::parseDeclaration, child)) {
                    if (!parseExpressionStatement(child)) return false;
                }
                */
                
                if (!parseExpressionStatement(child)) return false;
                if (!match(TokenType.t_rpar)) {
                    if (!parseTopExpression(child)) return false;
                }
                if (!consume(TokenType.t_rpar)) return false;
                if (!parseStatement(child)) return false;
                break;
            }
            default:
                return false;
        }
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseSelectionStatement(CategoryNode parent) {
        CategoryNode child = new CategoryNode("SelectionStatement");
        
        if (!consumeAndAddAsLeaf(TokenType.t_if, child)) return false;
        if (!consume(TokenType.t_lpar)) return false;
        if (!parseTopExpression(child)) return false;
        if (!consume(TokenType.t_rpar)) return false;
        if (!parseStatement(child)) return false;
        if (consumeAndAddAsLeaf(TokenType.t_else, child)) {
            if (!parseStatement(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseJumpStatement(CategoryNode parent) {
        CategoryNode child = new CategoryNode("JumpStatement");
        
        switch (getCurrentType()) {
            case t_return: {
                consumeAndAddAsLeaf(child);
                if (!match(TokenType.t_semic)) {
                    if (!parseTopExpression(child)) return false;
                }
                break;
            }
            default:
                return false;
        }
        
        if (!consume(TokenType.t_semic)) return missingError("Missing semi-colon");
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStatementList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StatementList");
        
        if (!parseStatement(child)) return false;
        // forceParse();
        if (!parseStatementListTail(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStatementListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StatementListTail");
        
        if(!match(t_rcurl)) {
            if(!parseStatementList(child)) return false;
        }
        /*
        switch (attemptParse(this::parseStatementList, child)) {
            case PARSED:
            case ROLLBACK:
                break;
            case DESYNC:
                return false;
        }
        */
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDeclarationList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DeclarationList");
        
        if (!parseDeclaration(child)) return false;
        if (!parseDeclarationListTail(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDeclarationListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DeclarationListTail");
        
        switch (attemptParse(this::parseDeclarationList, child)) {
            case PARSED:
            case ROLLBACK:
                break;
            case DESYNC:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    
    private boolean parseAssignment(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Assignment");
        
        if (!parseFactor(child)) return false;
        if (!parseAssignOperator(child)) return false;
        forceParse();
        if (!parseAssignmentExpression(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseAssignOperator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("AssignOperator");
        
        switch (getCurrentType()) {
            case t_operator_assign:
            case t_assign: {
                consumeAndAddAsLeaf(child);
                break;
            }
            default:
                return false;
        }
        
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
            case t_string:
            case t_sizeof:
            case t_new:
            case t_super:
            case t_typeid:
            case t_true:
            case t_false: {
                if (!parseDoubleOr(child)) return false;
                if (!parseDoubleOrTail(child)) return false;
                if (!parseExpressionTail(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseExpressionTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ExpressionTail");
        
        if (consume(TokenType.t_qmark)) {
            if (!parseExpression(child)) return false;
            if (!consume(TokenType.t_colon)) return false;
            if (!parseExpression(child)) return false;
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
            case t_string:
            case t_sizeof:
            case t_new:
            case t_super:
            case t_typeid:
            case t_true:
            case t_false:{
                if (!parseDoubleAnd(child)) return false;
                if (!parseDoubleAndTail(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDoubleOrTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DoubleOrTail");
        
        if (match(TokenType.t_dor)) {
            consumeAndAddAsLeaf(child);
            if (!parseDoubleOr(child)) return false;
            if (!parseDoubleOrTail(child)) return false;
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
            case t_super:
            case t_string:
            case t_sizeof:
            case t_new:
            case t_typeid:
            case t_true:
            case t_false:{
                if (!parseOr(child)) return false;
                if (!parseOrTail(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDoubleAndTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DoubleAndTail");
        
        if (match(TokenType.t_dand)) {
            consumeAndAddAsLeaf(child);
            if (!parseDoubleAnd(child)) return false;
            if (!parseDoubleAndTail(child)) return false;
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
            case t_string:
            case t_sizeof:
            case t_new:
            case t_super:
            case t_typeid:
            case t_true:
            case t_false:{
                if (!parseNot(child)) return false;
                if (!parseNotTail(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseOrTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("OrTail");
        
        if (match(TokenType.t_bar)) {
            consumeAndAddAsLeaf(child);
            if (!parseOr(child)) return false;
            if (!parseOrTail(child)) return false;
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
            case t_string:
            case t_sizeof:
            case t_new:
            case t_super:
            case t_typeid:
            case t_true:
            case t_false:{
                if (!parseAnd(child)) return false;
                if (!parseAndTail(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseNotTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("NotTail");
        
        if (match(TokenType.t_crt)) {
            consumeAndAddAsLeaf(child);
            if (!parseNot(child)) return false;
            if (!parseNotTail(child)) return false;
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
            case t_string:
            case t_sizeof:
            case t_new:
            case t_super:
            case t_typeid:
            case t_true:
            case t_false:{
                if (!parseEquation(child)) return false;
                if (!parseEquationTail(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseAndTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("AndTail");
        
        if (match(TokenType.t_and)) {
            consumeAndAddAsLeaf(child);
            if (!parseAnd(child)) return false;
            if (!parseAndTail(child)) return false;
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
            case t_string:
            case t_sizeof:
            case t_new:
            case t_super:
            case t_typeid:
            case t_true:
            case t_false:{
                if (!parseC(child)) return false;
                if (!parseCTail(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
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
                if (!parseEquation(child)) return false;
                if (!parseEquationTail(child)) return false;
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
            case t_string:
            case t_sizeof:
            case t_new:
            case t_super:
            case t_typeid:
            case t_true:
            case t_false:{
                if (!parseG(child)) return false;
                if (!parseGTail(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
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
                if (!parseC(child)) return false;
                if (!parseCTail(child)) return false;
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
            case t_string:
            case t_sizeof:
            case t_new:
            case t_super:
            case t_typeid:
            case t_true:
            case t_false:{
                if (!parseT(child)) return false;
                if (!parseTTail(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
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
                if (!parseG(child)) return false;
                if (!parseGTail(child)) return false;
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
            case t_string:
            case t_sizeof:
            case t_new:
            case t_super:
            case t_typeid:
            case t_true:
            case t_false:{
                if (!parseFactor(child)) return false;
                if (!parseFactorTail(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TTail");
        
        
        switch (getCurrentType()) {
            case t_add:
            case t_minus: {
                consumeAndAddAsLeaf(child);
                if (!parseT(child)) return false;
                if (!parseTTail(child)) return false;
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
                if (!parseFactor(child)) return false;
                break;
            }
            case t_string:
            case t_literal:
            case t_true:
            case t_false: {
                consumeAndAddAsLeaf(child);
                break;
            }
            case t_lpar: {
                AttemptStatus attemptStatus = attemptParse(this::parseCastExpression, child);
                if (attemptStatus == AttemptStatus.ROLLBACK) {
                    //forceParse();
                    if (!parseAtom(child)) return false;
                    if (!parseAtomTail(child)) return false;
                } else if (attemptStatus == AttemptStatus.DESYNC) {
                    return false;
                }
                break;
            }
            case t_id:
            case t_new:
            case t_super:
                {
                if (!parseAtom(child)) return false;
                if (!parseAtomTail(child)) return false;
                break;
            }
            case t_sizeof: {
                consumeAndAddAsLeaf(child);
                if (!consume(TokenType.t_lpar)) return false;
                if (!parseTypeName(child)) return false;
                if (!consume(TokenType.t_rpar)) return false;
                break;
            }
            case t_typeid: {
                consumeAndAddAsLeaf(child);
                if (!parseTypeName(child)) return false;
                break;
            }
            default:
                return error("unrecognized expression");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseCastExpression(CategoryNode parent) {
        CategoryNode child = new CategoryNode("CastExpression");
        
        if (!consume(TokenType.t_lpar)) return false;
        if (!parseTypeName(child)) return false;
        if (!consume(TokenType.t_rpar)) return false;
        if (!parseFactor(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseFactorTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("FactorTail");
        
        
        switch (getCurrentType()) {
            case t_star:
            case t_fwslash:
            case t_percent: {
                consumeAndAddAsLeaf(child);
                if (!parseFactor(child)) return false;
                if (!parseFactorTail(child)) return false;
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
                if (!parseExpression(child)) return false;
                if (!consume(TokenType.t_rpar)) return missingError("Missing matching )");
                break;
            }
            case t_id:
            case t_super: {
                consumeAndAddAsLeaf(child);
                if (!parseFunctionCall(child)) return false;
                break;
            }
            case t_typename: {
                consumeAndAddAsLeaf(child);
                if (!consume(TokenType.t_lpar)) return false;
                if (!parseArgsList(child)) return false;
                if (!consume(TokenType.t_rpar)) return false;
                break;
            }
            case t_new: {
                consumeAndAddAsLeaf(child);
                if (match(t_typename)) {
                    consumeAndAddAsLeaf(child);
                } else if (!parseNamespacedType(child)) return false;
                if (!consume(TokenType.t_lpar)) return false;
                if (!parseArgsList(child)) return false;
                if (!consume(TokenType.t_rpar)) return false;
                break;
            }
            default:
                return error("Not a valid expression");
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
                if (!match(TokenType.t_id)) return false;
                consumeAndAddAsLeaf(child);
                if (!parseFunctionCall(child)) return false;
                if (!parseAtomTail(child)) return false;
                break;
            }
            case t_lbrac: {
                consumeAndAddAsLeaf(child);
                if (!parseExpression(child)) ;
                if (!consume(TokenType.t_rbrac)) return missingError("Missing matching ]");
                if (!parseAtomTail(child)) return false;
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
        
        if (consume(TokenType.t_lpar)) {
            if (!parseArgsList(output)) return false;
            if (!consume(TokenType.t_rpar)) return false;
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
            case t_string:
            case t_sizeof:
            case t_new:
            case t_super:
            case t_typeid:
            case t_true:
            case t_false:{
                if (!parseExpression(output)) return false;
                if (!parseArgsListTail(output)) return false;
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
                if (!parseArgsList(output)) return false;
                break;
            }
            default:
                break;
        }
        
        parent.addChild(output);
        return true;
    }
    
    
    // TYPES
    
    protected boolean parseTypeName(CategoryNode parent) {
        CategoryNode output = new CategoryNode("TypeName");
        
        if (!parseSpecsAndQuals(output)) return false;
        if (!parseAbstractDeclarator(output)) return false;
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseSpecsAndQuals(CategoryNode parent) {
        CategoryNode output = new CategoryNode("SpecsAndQuals");
        
        switch (getCurrentType()) {
            case t_const: {
                if (!parseQualifier(output)) return false;
                if (!parseSpecsAndQualsTail(output)) return false;
                break;
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
            case t_class:
            case t_lpar:
                if (!parseSpecifier(output)) return false;
                if (!parseSpecsAndQualsTail(output)) return false;
                break;
            case t_id:
                if (!parseNamespacedType(output)) return false;
                if (!parseSpecsAndQualsTail(output)) return false;
                break;
            default:
                return false;
        }
        
        
        parent.addChild(output);
        return true;
    }
    
    protected boolean parseNamespacedType(CategoryNode parent) {
        CategoryNode child = new CategoryNode("NamespacedType");
        
        switch (getCurrentType()) {
            case t_id:
                consumeAndAddAsLeaf(child);
                if (!consume(t_namespace)) return error("Not a valid type");
                if (!parseNamespacedType(child)) return false;
                break;
            case t_typename:
                consumeAndAddAsLeaf(child);
                break;
            default:
                return error("Not a valid type");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseNamespace(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Namespace");
        
        if (!consumeAndAddAsLeaf(t_id, child)) return false;
        if (consumeAndAddAsLeaf(t_namespace, child)) {
            if (!parseNamespace(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    
    private boolean parseSpecsAndQualsTail(CategoryNode parent) {
        CategoryNode output = new CategoryNode("SpecsAndQualsTail");
        
        switch (getCurrentType()) {
            case t_id: {
                if (!isTypeName(getCurrent().getImage())) break;
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
                if (!parseSpecsAndQuals(output)) return false;
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
        
        if (!match(t_const)) return false;
        consumeAndAddAsLeaf(output);
        
        parent.addChild(output);
        return true;
    }
    
    
    private boolean parseQualifierList(CategoryNode parent) {
        CategoryNode output = new CategoryNode("QualifierList");
        
        if (!parseQualifier(output)) return false;
        if (!parseQualifierListTail(output)) return false;
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseQualifierListTail(CategoryNode parent) {
        CategoryNode output = new CategoryNode("QualifierListTail");
        
        if (match(t_const)) {
            if (!parseQualifierList(parent)) return false;
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
                if (!parseStructOrUnionSpecifier(output)) return false;
                break;
            }
            case t_class: {
                if (!parseClassSpecifier(output)) return false;
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
        
        if (!parseStructOrUnion(output)) return false;
        TokenType type = TokenType.t_id;
        Token current = getCurrent();
        switch (current.getType()) {
            case t_typename: {
                /*
                if(!isCompoundTypeName(getCurrent().getImage())) {
                    return error("Can't use typename in struct/union declaration");
                }
                */
                //type = TokenType.t_typename;
                current = undoTypeName(current);
            }
            case t_id: {
                addAsLeaf(output, current);
                getNext();
                
                if (consume(TokenType.t_lcurl)) {
                    String name = output.getLeafNode(type).getToken().getImage();
                    compoundTypeNames.add(name);
                    
                    if (!parseStructDeclarationList(output)) return false;
                    if (!consume(TokenType.t_rcurl)) return false;
                }
                break;
            }
            case t_lcurl: {
                getNext();
                if (!parseStructDeclarationList(output)) return false;
                if (!consume(TokenType.t_rcurl)) return false;
            }
            
        }
        
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseStructOrUnion(CategoryNode parent) {
        CategoryNode output = new CategoryNode("StructOrUnion");
        
        if (!(match(TokenType.t_struct) || match(TokenType.t_union))) return false;
        consumeAndAddAsLeaf(output);
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseClassSpecifier(CategoryNode parent) {
        CategoryNode output = new CategoryNode("ClassSpecifier");
        
        if (!consume(TokenType.t_class)) return false;
        if (!match(TokenType.t_id)) return false;
        String image = getCurrent().getImage();
        consumeAndAddAsLeaf(output);
        typedefed.add(image);
        
        
        parent.addChild(output);
        return true;
    }
    
    private boolean parseAbstractDeclarator(CategoryNode parent) {
        CategoryNode output = new CategoryNode("AbstractDeclarator");
        
        switch (getCurrentType()) {
            case t_star: {
                if (!parsePointer(output)) return false;
                if (!parseDirectAbstractDeclarator(output)) return false;
                break;
            }
            case t_lpar:
            case t_lbrac: {
                forceParse();
                if (!parseDirectAbstractDeclarator(output)) return false;
            }
            default:
                break;
        }
        
        parent.addChild(output);
        return true;
    }
    
    protected boolean parseDirectAbstractDeclarator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DirectAbstractDeclarator");
        
        switch (getCurrentType()) {
            case t_lbrac: {
                if (!consumeAndAddAsLeaf(TokenType.t_lbrac, child)) return false;
                if (!consumeAndAddAsLeaf(TokenType.t_rbrac, child)) return missingError("Missing matching ]");
                if (!parseDirectAbstractDeclarator(child)) return false;
                break;
            }
            case t_lpar: {
                if (!consumeAndAddAsLeaf(TokenType.t_lpar, child)) return false;
                if (!match(TokenType.t_rpar)) {
                    if (!parseParameterTypeList(child)) return false;
                }
                if (!consumeAndAddAsLeaf(TokenType.t_rpar, child)) return missingError("Missing matching )");
                
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
        
        if (!consume(TokenType.t_star)) return false;
        if (match(t_const)) {
            if (!parseQualifierList(child)) return false;
        }
        if (match(TokenType.t_star)) {
            if (!parsePointer(child)) return false;
        }
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseTypeDef(CategoryNode parent) {
        CategoryNode child = new CategoryNode("TypeDef");
        
        if (!consume(TokenType.t_typedef)) return false;
        forceParse();
        if (!parseTypeName(child)) return error("Can't typedef this");
        if (!match(TokenType.t_id)) {
            if (match(t_literal)) return error("Can't typedef a literal");
            if (match(t_typename)) return error("ID already exists as a type");
            return error("Can't typedef a " + getCurrentType());
        }
        String typename = getCurrent().getImage();
        consumeAndAddAsLeaf(child);
        
        typedefed.add(typename);
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDeclarator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Declarator");
        
        if (match(TokenType.t_star)) {
            if (!parsePointer(child)) return false;
        }
        
        if (!parseDirectDeclarator(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDirectDeclarator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DirectDeclarator");
        
        switch (getCurrentType()) {
            case t_id: {
                if (isTypeName(getCurrent().getImage())) return error("Can't use typename as identifier", true);
                consumeAndAddAsLeaf(child);
                break;
            }
            case t_lpar: {
                getNext();
                if (!parseDeclarator(child)) return false;
                if (!consume(TokenType.t_rpar)) return missingError("Missing matching )");
                break;
            }
            default:
                return false;
        }
        
        if (!parseDirectDeclaratorTail(child)) return false;
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDirectDeclaratorTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DirectDeclaratorTail");
        
        switch (getCurrentType()) {
            case t_lpar: {
                consumeAndAddAsLeaf(child);
                if (attemptParse(this::parseParameterTypeList, child) == AttemptStatus.ROLLBACK) {
                    if (attemptParse(this::parseIdentifierList, child) == AttemptStatus.DESYNC) {
                        return error("Error parsing parameter list", true);
                    }
                }
                if (!consume(TokenType.t_rpar)) {
                    return false;
                }
                if (!parseDirectDeclaratorTail(child)) return false;
                break;
            }
            case t_lbrac: {
                consumeAndAddAsLeaf(child);
                
                switch (attemptParse(this::parseExpression, child)) {
                    case PARSED:
                    case ROLLBACK:
                        break;
                    case DESYNC:
                        return false;
                }
                if (!consume(TokenType.t_rbrac)) {
                    forceParse();
                    return error("Missing matching ]");
                }
                if (!parseDirectDeclaratorTail(child)) return false;
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
        
        if (!consumeAndAddAsLeaf(TokenType.t_literal, child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclarator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclarator");
        
        if (consume(TokenType.t_colon)) {
            if (!parseConstantExpression(child)) return false;
        } else {
            
            if (!parseDeclarator(child)) return false;
            if (consume(TokenType.t_colon)) {
                if (!parseConstantExpression(child)) return false;
            }
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclaratorList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclaratorList");
        
        if (!parseStructDeclarator(child)) return false;
        if (!parseStructDeclaratorListTail(child)) return false;
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclaratorListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclaratorListTail");
        
        if (consume(TokenType.t_comma)) {
            if (!parseStructDeclaratorList(child)) return false;
        }
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclaration(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclaration");
        
        if (!parseSpecsAndQuals(child)) return false;
        if (!parseStructDeclaratorList(child)) return false;
        if (!consume(TokenType.t_semic)) return missingError("Missing ;");
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclarationList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclarationList");
        
        if (!parseStructDeclaration(child)) return false;
        if (!parseStructDeclarationListTail(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDeclaration(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Declaration");
        
        if (!parseDeclarationSpecifiers(child)) return false;
        if (!match(TokenType.t_semic)) {
            if (!parseInitDeclaratorList(child)) return false;
        }
        if (!consume(TokenType.t_semic)) {
            if (!recoverableMissingError("Missing semi-colon", t_semic, t_lcurl)) {
                return false;
            }
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseDeclarationSpecifiers(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DeclarationSpecifiers");
        
        if (!parseSpecsAndQuals(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseInitDeclaratorList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("InitDeclaratorList");
        
        if (!match(TokenType.t_semic)) {
            if (!parseInitDeclarator(child)) return false;
            if (!parseInitDeclaratorListTail(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseInitDeclaratorListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("InitDeclaratorListTail");
        
        if (consume(TokenType.t_comma)) {
            if (!parseInitDeclaratorList(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseInitDeclarator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("InitDeclarator");
        
        if (!parseDeclarator(child)) return false;
        if (consume(TokenType.t_assign)) {
            forceParse();
            if (!parseInitializer(child)) {
                return missingError("Missing initial value");
            }
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseInitializer(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Initializer");
        
        switch (getCurrentType()) {
            case t_lcurl:
                getNext();
                // TODO implement initializer lists
                break;
            default:
                if (!parseAssignmentExpression(child)) return false;
                break;
        }
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseAssignmentExpression(CategoryNode parent) {
        CategoryNode child = new CategoryNode("AssignmentExpression");
    
        /*
        if(!attemptParse(this::parseAssignment, child)) {
            if(!parseExpression(child)) return false;
        }
        */
        if (!oneMustParse(child, this::parseAssignment, this::parseExpression)) {
            return false;
        }
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseStructDeclarationListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("StructDeclarationListTail");
        
        if (!match(TokenType.t_rcurl)) {
            if (!parseStructDeclarationList(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    protected boolean parseParameterTypeList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ParameterTypeList");
        
        if (!parseParameterList(child)) return false;
        if (consume(TokenType.t_comma)) {
            if (!consume(TokenType.t_ellipsis)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseParameterList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ParameterList");
        
        if (!parseParameterDeclaration(child)) return false;
        if (!parseParameterListTail(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    protected boolean parseParameterDeclaration(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ParameterDeclaration");
        
        if (!parseDeclarationSpecifiers(child)) return false;
        switch (attemptParse(this::parseDeclarator, child)) {
            case PARSED:
                break;
            case ROLLBACK: {
                switch (attemptParse(this::parseAbstractDeclarator, child)) {
                    case PARSED:
                    case ROLLBACK:
                        break;
                    case DESYNC:
                        return error("Failure parsing parameter declaration", true);
                }
                break;
            }
            case DESYNC:
                return error("Failure parsing parameter declaration", true);
        }
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseParameterListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ParameterListTail");
        
        pushState();
        if (consume(TokenType.t_comma)) {
            if (attemptParse(this::parseParameterList, child) == AttemptStatus.PARSED) {
                popState();
            } else {
                applyState();
            }
            //if(!parseParameterList(child)) return false;
        } else popState();
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseIdentifierList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("IdentifierList");
        
        if (!consumeAndAddAsLeaf(TokenType.t_id, child)) return false;
        if (!parseIdentifierListTail(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseIdentifierListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("IdentifierListTail");
        
        if (consume(TokenType.t_comma)) {
            if (!parseIdentifierList(child)) return false;
        }
        
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseClassDeclaration(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ClassDeclaration");
        
        if (!consumeAndAddAsLeaf(TokenType.t_class, child)) return false;
        Token token = undoTypeName(getCurrent());
        if (token.getType() != TokenType.t_id) return false;
        child.addChild(new LeafNode(token));
        getNext();
        // if(!consumeAndAddAsLeaf(TokenType.t_id, child)) return false;
        
        
        String name = child.getLeafNode(TokenType.t_id).getToken().getImage();
        typedefed.add(name);
        boolean forced = false;
        if (match(TokenType.t_colon)) {
            forceParse();
            forced = true;
            if (!parseInherit(child)) return false;
        }
        if (!consume(TokenType.t_lcurl)) return false;
        if (!forced) {
            forceParse();
        }
        if (!parseClassDeclarationList(child)) return false;
        
        if (!consume(TokenType.t_rcurl)) return missingError("Missing matching }");
        if (!consume(TokenType.t_semic)) return missingError("Missing semi-colon");
        
        parent.addChild(child);
        return true;
    }
    
    
    private boolean parseInherit(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Inherit");
        
        if (!consume(TokenType.t_colon)) return false;
        if (!consumeAndAddAsLeaf(TokenType.t_typename, child)) {
            if (!parseTypeName(child))
                return error("Not a proper typename for inherit");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseClassDeclarationList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ClassDeclarationList");
        
        if (!parseClassTopLevelDeclaration(child)) return false;
        if (!parseClassDeclarationListTail(child)) return false;
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseClassDeclarationListTail(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ClassDeclarationListTail");
        
        if (!match(TokenType.t_rcurl)) {
            if (!parseClassDeclarationList(child)) return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseClassTopLevelDeclaration(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ClassTopLevelDeclaration");
        
        if (match(t_lbrac)) {
            if (!parseCompilationTagList(child)) return false;
        }
        
        consumeAndAddAsLeaf(TokenType.t_virtual, child);
        attemptParse(this::parseVisibility, child);
        /*
        if(!attemptParse(this::parseConstructorDefinition, child)) {
            if(!attemptParse(this::parseDeclaration, child)) {
                
                if(!attemptParse(this::parseFunctionDefinition, child)) {
                    return error("Could not parse class declaration", true);
                }
            }
        }
        
         */
        
        if (!oneMustParse(child, this::parseConstructorDefinition, this::parseDeclaration, this::parseFunctionDefinition)) {
            return error("Could not parse class declaration");
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseVisibility(CategoryNode parent) {
        CategoryNode child = new CategoryNode("Visibility");
        
        switch (getCurrentType()) {
            case t_public:
            case t_private:
            case t_internal:
                consumeAndAddAsLeaf(child);
                break;
            default:
                return false;
        }
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseConstructorDefinition(CategoryNode parent) {
        CategoryNode child = new CategoryNode("ConstructorDefinition");
        
        if (!consumeAndAddAsLeaf(TokenType.t_typename, child)) return false;
        if (!consume(TokenType.t_lpar)) return false;
        forceParse();
        if (!match(TokenType.t_rpar)) {
            if (!parseParameterList(child)) return error("Could not parse parameter list");
        } else {
            // add empty parameter list
            child.addChild(new CategoryNode("ParameterList"));
        }
        if (!consume(TokenType.t_rpar)) return missingError("Missing matching )");
        if (!consumeAndAddAsLeaf(t_semic, child)) {
            if (consume(TokenType.t_colon)) {
                if (match(TokenType.t_id)) {
                    if (!getCurrent().getImage().equals("this"))
                        return error("prior constructor must be either this or " +
                                "super");
                    
                } else if (!match(TokenType.t_super)) {
                    return false;
                }
                consumeAndAddAsLeaf(child);
                if (!consume(TokenType.t_lpar)) return false;
                
                if (!parseArgsList(child)) return error("Could not parse args list");
                
                if (!consume(TokenType.t_rpar)) return missingError("Missing matching )");
            }
            if (!parseCompoundStatement(child)) return false;
        }
        parent.addChild(child);
        return true;
    }
    
    private boolean parseCompilationTag(CategoryNode parent) {
        CategoryNode child = new CategoryNode("CompilationTag");
        
        if (!consume(t_lbrac)) return false;
        if (!consumeAndAddAsLeaf(t_id, child)) return false;
        if (consume(t_lpar)) {
            if (!parseArgsList(child)) return false;
            if (!consume(t_rpar)) return error("Missing matching )");
        }
        if (!consume(t_rbrac)) return error("Missing matching ]");
        
        parent.addChild(child);
        return true;
    }
    
    private boolean parseCompilationTagList(CategoryNode parent) {
        CategoryNode child = new CategoryNode("CompilationTagList");
        
        if (match(t_lbrac)) {
            if (!parseCompilationTag(child)) return false;
            if (!parseCompilationTagList(child)) return false;
        }
        
        
        parent.addChild(child);
        return true;
    }
}
