package radin.frontend.directastparsing;

import radin.core.errorhandling.AbstractCompilationError;
import radin.input.ITokenizer;
import radin.frontend.v1.parsing.CategoryNode;
import radin.frontend.v1.parsing.LeafNode;
import radin.frontend.v1.parsing.Parser;
import radin.frontend.v1.semantics.ActionRoutineApplier;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.exceptions.InvalidPrimitiveException;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;

import java.util.List;

import static radin.core.lexical.TokenType.*;

/**
 * Parses an Abstract Syntax Tree in tokens, then outputs it as a CategoryNode("AST")
 * with a preset synthesized from the parse
 */
public class ASTParser extends Parser {
    
    private TypeEnvironment environment;
    private ActionRoutineApplier actionRoutineApplier;
    
    
    private static class InnerTokenizer implements ITokenizer<Token> {
        private List<? extends Token> tokens;
        private int index = 0;
    
        public InnerTokenizer(List<? extends Token> tokens) {
            this.tokens = tokens;
        }
    
        @Override
        public int getTokenIndex() {
            return index;
        }
        
        @Override
        public void setTokenIndex(int tokenIndex) {
            index = tokenIndex;
        }
        
        @Override
        public String getInputString() {
            return null;
        }
        
        @Override
        public int run() {
            return tokens.size();
        }
        
        @Override
        public Token getFirst() {
            return tokens.get(0);
        }
        
        @Override
        public Token getLast() {
            return tokens.get(tokens.size() - 1);
        }
        
        @Override
        public Token getPrevious() {
            if(index == 0) return null;
            return tokens.get(index - 1);
        }
        
        @Override
        public Token getCurrent() {
            return tokens.get(index);
        }
        
        @Override
        public Token getNext() {
            if(index == tokens.size() - 1) return null;
            return tokens.get(++index);
        }
        
        @Override
        public void reset() {
            index = 0;
        }
        
        @Override
        public Token invoke() {
            return getNext();
        }
        
        @Override
        public List<AbstractCompilationError> getErrors() {
            return null;
        }
    }
    
    public ASTParser(TypeEnvironment environment, List<? extends Token> tokens) {
        setEnvironment(environment);
        setTokenizer(new InnerTokenizer(tokens));
    }
    
    @Override
    public void setTokenizer(ITokenizer<? extends Token> t) {
        this.lexer = t;
    }
    
    public  void setEnvironment(TypeEnvironment environment) {
        this.environment = environment;
        actionRoutineApplier = new ActionRoutineApplier(environment);
    }
    
    private static class ASTConsumeError extends RuntimeException { }
    public static class NotRecognizedASTType extends AbstractCompilationError {
        NotRecognizedASTType(Token t) {
            super(t, "This is not a recognized AST type");
        }
    }
    
    public Token consume() {
        Token output = getCurrent();
        getNext();
        return output;
    }
    
    public Token consumeType(TokenType t) throws ASTConsumeError {
        if(!match(t)) throw new ASTConsumeError();
        return consume();
    }
    
    @Override
    public CategoryNode parse() {
        CategoryNode output = new CategoryNode("AST");
        
        try {
            AbstractSyntaxNode abstractSyntaxNode = parseAbstractSyntaxNodeString();
            if (abstractSyntaxNode == null) {
                return null;
            }
            
            output.setSynthesized(abstractSyntaxNode);
            return output;
        }catch (ASTConsumeError ignore) {
            return null;
        }
    }
    
    private AbstractSyntaxNode parseAbstractSyntaxNodeString() {
        Token typeToken = consume();
        ASTNodeType astNodeType = AbstractSyntaxNode.cleanNameToType.get(typeToken.getRepresentation());
        if(astNodeType == null) {
            throw new NotRecognizedASTType(typeToken);
        }
        AbstractSyntaxNode output;
        Token associated= null;
        if(consume(TokenType.t_dollar)) {
            associated = consume();
        }else if(consume(TokenType.t_namespace)) {
            consume(t_at);
            associated = consume();
        }
        
        if(consume(TokenType.t_lbrac)) {
            CategoryNode temp = new CategoryNode("");
            if(!parseParameterDeclaration(temp)) {
                return null;
            }
            if(!consume(t_rbrac)) {
                return null;
            }
            CategoryNode type = temp.getCategoryNode("ParameterDeclaration");
            AbstractSyntaxNode syntaxNode = actionRoutineApplier.invoke(type);
            CXType cxType;
            if(syntaxNode instanceof TypedAbstractSyntaxNode) {
                cxType = ((TypedAbstractSyntaxNode) syntaxNode).getCxType();
            } else {
                try {
                    cxType = environment.getType(syntaxNode);
                } catch (InvalidPrimitiveException e) {
                    throw new ASTConsumeError();
                }
            }
            output = new TypedAbstractSyntaxNode(astNodeType, associated, cxType);
        } else {
            output = new AbstractSyntaxNode(astNodeType, associated);
        }
        
        
        if(consume(TokenType.t_lcurl)) {
            do {
                if(!parseAbstractSyntaxNodeString(output)) {
                    return null;
                }
            } while (!match(TokenType.t_rcurl));
            
            if(!consume(TokenType.t_rcurl)) {
                return null;
            }
            
        } else if(!consume(t_semic)) {
            return null;
        }
        return output;
    }
    
    
    
    private boolean parseAbstractSyntaxNodeString(AbstractSyntaxNode astParent) {
        AbstractSyntaxNode abstractSyntaxNode = parseAbstractSyntaxNodeString();
        if(abstractSyntaxNode == null) return false;
        astParent.addChild(abstractSyntaxNode);
        return true;
    }
    
    @Override
    protected boolean parseDirectAbstractDeclarator(CategoryNode parent) {
        CategoryNode child = new CategoryNode("DirectAbstractDeclarator");
        
        switch (getCurrentType()) {
            case t_lbrac: {
                if (!consumeAndAddAsLeaf(TokenType.t_lbrac, child)) return false;
                if (!match(t_rbrac)) {
                    AbstractSyntaxNode arrayInner = parseAbstractSyntaxNodeString();
                    CategoryNode expression = new CategoryNode("Expression");
                    expression.setSynthesized(arrayInner);
                    child.addChild(expression);
                }
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
    
    protected boolean parseNamespacedType(CategoryNode parent) {
        CategoryNode child = new CategoryNode("NamespacedType");
        
        switch (getCurrentType()) {
            case t_id:
                consumeAndAddAsLeaf(child);
                if (!consume(t_namespace)) return error("Not a valid type");
                switch (attemptParse(this::parseNamespacedType, child)) {
                    case PARSED:
                        break;
                    case ROLLBACK:
                        Token type = consumeType(t_id);
                        CategoryNode next = new CategoryNode("NamespacedType");
                        next.addChild(new LeafNode(type.changedType(t_typename)));
                        child.addChild(next);
                        break;
                    case DESYNC:
                        return false;
                }
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
}
