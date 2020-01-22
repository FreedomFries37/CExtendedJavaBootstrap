package radin.core.input.frontend.v2.lexing;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.input.frontend.v1.lexing.TokenizationError;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.input.Tokenizer;

import java.util.LinkedList;
import java.util.List;

public class BasicLexer extends Tokenizer<Token> {
    
    private List<AbstractCompilationError> errors;
    
    public BasicLexer(String inputString, String filename) {
        super(inputString, filename);
        errors = new LinkedList<>();
    }
    // char+const+do+double+els+float+for+if+int+long+return+short+static+typedef+union+unsigned+struct+void+while+class+public+private+new+super+virtual+sizeof+(boolean)+(in)+(implement)+(internal)+(using)
    @Override
    public Token singleLex() {
        do {
            if (Character.isWhitespace(getChar())) consumeChar();
            else if (consume("//")) {
                while (!consume("\n") && !consume(System.lineSeparator())) consumeChar();
            } else if (consume("/*")) {
                while (!consume("*/")) consumeChar();
            } else break;
        } while (true);
    
        if (match(EOF)) return new Token(TokenType.t_eof);
        String image = "";
        if (match('"')) {
            consumeChar();
            boolean inString = true;
            while (inString) {
                if (match('\\')) {
                    image += consumeNextChars(2);
                } else if (match('\n')) {
                    throw new TokenizationError("Incomplete String", getPrevious());
                } else {
                    char nextChar = consumeChar();
                    if (nextChar == '"') {
                        inString = false;
                    } else {
                        image += nextChar;
                    }
                }
            }
            return new Token(TokenType.t_string, "\"" + image + "\"");
        } else if (Character.isDigit(getChar()) || (match('.') && Character.isDigit(getNextChars(2).charAt(1)))) {
            if (match("0x") || match("0X")) {
                image += consumeNextChars(2);
                while ((getChar() >= 'a' && getChar() <= 'f') ||
                        (getChar() >= 'A' && getChar() <= 'F')) {
                    image += consumeChar();
                }
            } else {
                boolean decimalFound = false;
                while (Character.isDigit(getChar()) || match('.')) {
                    char nextChar = consumeChar();
                    if (nextChar == '.') {
                        if (!decimalFound) {
                            decimalFound = true;
                        } else {
                            return null;
                        }
                    }
                    image += nextChar;
                }
            }
        
            return new Token(TokenType.t_literal, image);
        } else if (Character.isLetter(getChar()) || match('_')) {
            image += consumeChar();
            while (Character.isLetter(getChar()) || match('_') || Character.isDigit(getChar())) {
                image += consumeChar();
            }
    
            return getKeywordToken(image);
    
        }
    
        switch (consumeChar()) {
            case '<': {
                switch (getChar()) {
                    case '<': {
                        consumeChar();
                        if (match('=')) {
                            consumeChar();
                            return new Token(TokenType.t_operator_assign, "<<=");
                        }
                        return new Token(TokenType.t_lshift);
                    }
                    case '=': {
                        consumeChar();
                        return new Token(TokenType.t_lte);
                    }
                    default:
                        return new Token(TokenType.t_lt);
                }
            }
            case '>': {
                switch (getChar()) {
                    case '>': {
                        consumeChar();
                        if (match('=')) {
                            consumeChar();
                            return new Token(TokenType.t_operator_assign, ">>=");
                        }
                        return new Token(TokenType.t_rshift);
                    }
                    case '=': {
                        consumeChar();
                        return new Token(TokenType.t_gte);
                    }
                    default:
                        return new Token(TokenType.t_gt);
                }
            }
            case '+': {
                if (match('=')) {
                    consumeChar();
                    return new Token(TokenType.t_operator_assign, "+=");
                } else if (consume("+")) {
                    return new Token(TokenType.t_inc);
                }
                return new Token(TokenType.t_add);
            }
            case '-': {
                if (match('=')) {
                    consumeChar();
                    return new Token(TokenType.t_operator_assign, "-=");
                } else if (consume("-")) {
                    return new Token(TokenType.t_dec);
                } else if (consume(">")) {
                    return new Token(TokenType.t_arrow);
                }
                return new Token(TokenType.t_minus);
            }
            case '&': {
                if (getChar() != '&') {
                    return new Token(TokenType.t_and);
                } else if (consume('&')) {
                    if (consume('=')) {
                        return new Token(TokenType.t_operator_assign, "&&=");
                    }
                    return new Token(TokenType.t_dand);
                }
            }
            case '|': {
                if (getChar() != '|') {
                    return new Token(TokenType.t_bar);
                } else if (consume('|')) {
                    if (consume('=')) {
                        return new Token(TokenType.t_operator_assign, "||=");
                    }
                    return new Token(TokenType.t_dor);
                }
            }
            case '=': {
                if (consume('=')) {
                    return new Token(TokenType.t_eq);
                }
                return new Token(TokenType.t_assign);
            }
            case '!': {
                if (consume('=')) {
                    return new Token(TokenType.t_neq);
                }
                return new Token(TokenType.t_bang);
            }
            case ';': {
                return new Token(TokenType.t_semic);
            }
            case '{': {
                return new Token(TokenType.t_lcurl);
            }
            case '}': {
                return new Token(TokenType.t_rcurl);
            }
            case ',': {
                return new Token(TokenType.t_comma);
            }
            case ':': {
                if (consume(':')) {
                    return new Token(TokenType.t_namespace);
                }
                return new Token(TokenType.t_colon);
            }
            case '(': {
                return new Token(TokenType.t_lpar);
            }
            case ')': {
                return new Token(TokenType.t_rpar);
            }
            case '[':
                return new Token(TokenType.t_lbrac);
            case ']':
                return new Token(TokenType.t_rbrac);
            case '.': {
                if (consume("..")) return new Token(TokenType.t_ellipsis);
                return new Token(TokenType.t_dot);
            }
            case '~': {
                if (consume('=')) {
                    return new Token(TokenType.t_operator_assign, "~=");
                }
                return new Token(TokenType.t_not);
            }
            case '*': {
                if (consume('=')) {
                    return new Token(TokenType.t_operator_assign, "*=");
                }
                return new Token(TokenType.t_star);
            }
            case '/':
                return new Token(TokenType.t_fwslash);
            case '%': {
                if (consume('=')) {
                    return new Token(TokenType.t_operator_assign, "%=");
                }
                return new Token(TokenType.t_percent);
            }
            case '^': {
                if (consume('=')) {
                    return new Token(TokenType.t_operator_assign, "^=");
                }
                return new Token(TokenType.t_crt);
            }
            case '?':
                return new Token(TokenType.t_qmark);
            case '\'': {
                String str = getNextChars(2);
                if (str.length() != 2 || str.charAt(1) != '\'') {
                    if (str.charAt(0) == '\\') {
                        str = getNextChars(3);
                        if (str.length() != 3 || str.charAt(2) != '\'') return null;
                        consumeNextChars(3);
                        return new Token(TokenType.t_literal, "'" + str);
                    }
                
                    return null;
                }
                consumeNextChars(2);
                return new Token(TokenType.t_literal, "'" + str);
            }
        }
        
        return null;
    }
    
    public Token getNext() {
        if (++tokenIndex == createdTokens.size()) {
            Token tok;
            try {
                tok = singleLex();
                
            } catch (AbstractCompilationError e) {
                getErrors().add(e);
                //finishedIndex = getTokenIndex();
                tok = null;
            }
            if (tok == null) return null;
            tok.setPrevious(getPrevious());
            String representation = tok.getRepresentation();
            tok.addColumnAndLineNumber(column - representation.length(), lineNumber);
            //prevLineNumber = lineNumber;
            //prevColumn = column;
            createdTokens.add(tok);
            return tok;
        }
        return createdTokens.get(getTokenIndex());
    }
    
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return errors;
    }
}
