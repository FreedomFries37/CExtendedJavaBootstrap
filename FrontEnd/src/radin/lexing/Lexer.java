package radin.lexing;

import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;

import java.io.EOFException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Lexer implements Iterable<Token> {
    
    class LexIterator implements Iterator<Token> {
        private List<Token> createdTokensInitial;
        private int index = 0;
    
        public LexIterator(List<Token> createdTokensInitial) {
            this.createdTokensInitial = new LinkedList<>(createdTokensInitial);
        }
    
        @Override
        public boolean hasNext() {
            if(index < createdTokensInitial.size()) return true;
            return currentIndex < inputString.length();
        }
    
        @Override
        public Token next() {
            if(index < createdTokensInitial.size()) {
                return createdTokensInitial.get(index++);
            }
            return getNext();
        }
    }
    
    private String inputString;
    private final int maxIndex;
    private int currentIndex;
    private int prevColumn;
    private int prevLineNumber;
    private int column;
    private int lineNumber;
    private List<Token> createdTokens;
    private int tokenIndex;
    
    public int getTokenIndex() {
        return tokenIndex;
    }
    
    public void setTokenIndex(int tokenIndex) {
        if(tokenIndex < 0 || tokenIndex >= createdTokens.size()) return;
        this.tokenIndex = tokenIndex;
    }
    
    public Lexer(String inputString) {
        this.inputString = inputString.trim();
        createdTokens = new LinkedList<>();
        tokenIndex = -1;
        column = 0;
        lineNumber = 1;
        prevColumn = 0;
        prevLineNumber = 1;
        maxIndex = this.inputString.length();
    }
    
    private char getChar() {
        if(currentIndex == inputString.length()) return '\0';
        return inputString.charAt(currentIndex);
    }
    
    private char consumeChar() {
        if(getChar() == '\n') {
            ++lineNumber;
            column = 0;
        } else {
            column++;
        }
        return inputString.charAt(currentIndex++);
    }
    
    private String getNextChars(int count) {
        int min = Math.min(inputString.length(), currentIndex + count);
        return inputString.substring(currentIndex, min);
    }
    
    private String consumeNextChars(int count) {
        int min = Math.min(inputString.length(), currentIndex + count);
        String substring = inputString.substring(currentIndex, min);
        for (int i = currentIndex; i < min; i++) {
            consumeChar();
        }
        return substring;
    }
    
    private boolean consume(String str) {
       if(match(str)) {
           consumeNextChars(str.length());
           return true;
       }
       return false;
    }
    
    private boolean consume(char c) {
        if(match(c)) {
            consumeChar();
            return true;
        }
        return false;
    }
    
    private boolean match(char c) {
        return getChar() == c;
    }
    
    private boolean match(String str) {
        return getNextChars(str.length()).equals(str);
    }
    
    private Token singleLex() {
        String image = "";
        
       
        
        while(getChar() == ' ' || getChar() == '\n' || getChar() == '\t' || getChar() == '\r') {
            consumeChar();
            if(consume("//") || consume("#")) {
                while (!consume(System.lineSeparator())) {
                    consumeChar();
                }
            }else if(consume("/*")) {
                while (!consume("*/")) consumeChar();
            }
        }
        
        while(getChar() == ' ' || getChar() == '\n' || getChar() == '\t' || getChar() == '\r') {
            consumeChar();
        }
    
        if(getChar() == '\0') {
            return new Token(TokenType.t_eof);
        }
        
       
        
        if(getChar() == '"') {
            consumeChar();
            boolean inString = true;
            while(inString) {
                if (getChar() == '\\') {
                    image += consumeNextChars(2);
                } else if (getChar() == '\n') {
                    return null;
                }else {
                    char nextChar = consumeChar();
                    if (nextChar == '"') {
                        inString = false;
                    }else {
                        image += nextChar;
                    }
                }
            }
            return new Token(TokenType.t_string, "\"" + image + "\"");
        }else if(Character.isDigit(getChar()) || (getChar() == '.' && Character.isDigit(getNextChars(2).charAt(1)))) {
            if(match("0x") || match("0X")) {
                image += consumeNextChars(2);
                while((getChar() >= 'a' && getChar() <= 'f') ||
                        (getChar() >= 'A' && getChar() <= 'F')) {
                    image += consumeChar();
                }
            }else {
                boolean decimalFound = false;
                while(Character.isDigit(getChar()) || getChar() == '.') {
                    char nextChar = consumeChar();
                    if(nextChar == '.') {
                        if(!decimalFound) {
                            decimalFound = true;
                        }else{
                            return null;
                        }
                    }
                    image += nextChar;
                }
            }
            
            return new Token(TokenType.t_literal, image);
        }else if(Character.isLetter(getChar()) || getChar() == '_') {
            image += consumeChar();
            while(Character.isLetter(getChar()) || getChar() == '_' || Character.isDigit(getChar())) {
                image += consumeChar();
            }
    
            if(image.equals("char")) {
                return new Token(TokenType.t_char);
            }else if(image.equals("const")) {
                return new Token(TokenType.t_const);
            }else if(image.equals("do")) {
                return new Token(TokenType.t_do);
            }else if(image.equals("double")) {
                return new Token(TokenType.t_double);
            }else if(image.equals("else")) {
                return new Token(TokenType.t_else);
            }else if(image.equals("float")) {
                return new Token(TokenType.t_float);
            }else if(image.equals("for")) {
                return new Token(TokenType.t_for);
            }else if(image.equals("if")) {
                return new Token(TokenType.t_if);
            }else if(image.equals("int")) {
                return new Token(TokenType.t_int);
            }else if(image.equals("long")) {
                return new Token(TokenType.t_long);
            }else if(image.equals("return")) {
                return new Token(TokenType.t_return);
            }else if(image.equals("short")) {
                return new Token(TokenType.t_short);
            }else if(image.equals("static")) {
                return new Token(TokenType.t_static);
            }else if(image.equals("typedef")) {
                return new Token(TokenType.t_typedef);
            }else if(image.equals("union")) {
                return new Token(TokenType.t_union);
            }else if(image.equals("unsigned")) {
                return new Token(TokenType.t_unsigned);
            }else if(image.equals("struct")) {
                return new Token(TokenType.t_struct);
            }else if(image.equals("void")) {
                return new Token(TokenType.t_void);
            }else if(image.equals("while")) {
                return new Token(TokenType.t_while);
            }else if(image.equals("class")) {
                return new Token(TokenType.t_class);
            }else if(image.equals("public")) {
                return new Token(TokenType.t_public);
            }else if(image.equals("private")) {
                return new Token(TokenType.t_private);
            }else if(image.equals("new")) {
                return new Token(TokenType.t_new);
            }else if(image.equals("super")) {
                return new Token(TokenType.t_super);
            }else if(image.equals("virtual")) {
                return new Token(TokenType.t_virtual);
            }else if(image.equals("sizeof")) {
                return new Token(TokenType.t_sizeof);
            }else if(image.equals("boolean")) {
                return new Token(TokenType.t_typename, image);
            }
            
            return new Token(TokenType.t_id, image);
        }
        
        switch (consumeChar()) {
            case '<': {
                switch (getChar()) {
                    case '<': {
                        consumeChar();
                        if(getChar() == '=') {
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
                        if(getChar() == '=') {
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
                if(getChar() == '=') {
                    consumeChar();
                    return new Token(TokenType.t_operator_assign, "+=");
                }else if(consume("+")) {
                    return new Token(TokenType.t_inc);
                }
                return new Token(TokenType.t_add);
            }
            case '-': {
                if(getChar() == '=') {
                    consumeChar();
                    return new Token(TokenType.t_operator_assign, "-=");
                }else if(consume("-")) {
                    return new Token(TokenType.t_dec);
                }else if(consume(">")) {
                    return new Token(TokenType.t_arrow);
                }
                return new Token(TokenType.t_minus);
            }
            case '&': {
                if(getChar() != '&') {
                    return new Token(TokenType.t_and);
                }else if(consume('&')) {
                    if(consume('=')) {
                        return new Token(TokenType.t_operator_assign, "&&=");
                    }
                    return new Token(TokenType.t_dand);
                }
            }
            case '|': {
                if(getChar() != '|') {
                    return new Token(TokenType.t_bar);
                }else if(consume('|')) {
                    if(consume('=')) {
                        return new Token(TokenType.t_operator_assign, "||=");
                    }
                    return new Token(TokenType.t_dor);
                }
            }
            case '=': {
                if(consume('=')) {
                    return new Token(TokenType.t_eq);
                }
                return new Token(TokenType.t_assign);
            }
            case '!': {
                if(consume('=')) {
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
                return new Token(TokenType.t_colon);
            }
            case '(': {
                return new Token(TokenType.t_lpar);
            }
            case ')': {
                return new Token(TokenType.t_rpar);
            }
            case '[': return new Token(TokenType.t_lbrac);
            case ']': return new Token(TokenType.t_rbrac);
            case '.': return new Token(TokenType.t_dot);
            case '~': {
                if(consume('=')){
                    return new Token(TokenType.t_operator_assign, "~=");
                }
                return new Token(TokenType.t_not);
            }
            case '*': {
                if(consume('=')){
                    return new Token(TokenType.t_operator_assign, "*=");
                }
                return new Token(TokenType.t_star);
            }
            case '/': return new Token(TokenType.t_fwslash);
            case '%': {
                if(consume('=')){
                    return new Token(TokenType.t_operator_assign, "%=");
                }
                return new Token(TokenType.t_percent);
            }
            case '^': {
                if(consume('=')){
                    return new Token(TokenType.t_operator_assign, "^=");
                }
                return new Token(TokenType.t_crt);
            }
            case '?': return new Token(TokenType.t_qmark);
            case '\'': {
                String str = getNextChars(2);
                if(str.length() != 2 || str.charAt(1) != '\''){
                    if(str.charAt(0) == '\\') {
                        str = getNextChars(3);
                        if(str.length() != 3 || str.charAt(2) != '\'') return null;
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
    
    public Token getCurrent() {
        if(createdTokens.size() == 0) return getNext();
        return createdTokens.get(tokenIndex);
    }
    
    public Token getNext() {
        if(++tokenIndex == createdTokens.size()) {
            Token t = Objects.requireNonNull(singleLex()).addColumnAndLineNumber(prevColumn, prevLineNumber);
            prevLineNumber = lineNumber;
            prevColumn = column;
            createdTokens.add(t);
            return t;
        }
        return createdTokens.get(tokenIndex);
    }
    
    public void reset() {
        tokenIndex = 0;
    }
    
    
    @Override
    public Iterator<Token> iterator() {
        return new LexIterator(this.createdTokens);
    }
    
    
}
