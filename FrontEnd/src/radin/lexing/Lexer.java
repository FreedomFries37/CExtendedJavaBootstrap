package radin.lexing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
    private int currentIndex;
    private List<Token> createdTokens;
    
    
    public Lexer(String inputString) {
        this.inputString = inputString.trim();
        createdTokens = new LinkedList<>();
    }
    
    private char getChar() {
        return inputString.charAt(currentIndex);
    }
    
    private char consumeChar() {
        return inputString.charAt(currentIndex++);
    }
    
    private String getNextChars(int count) {
        int min = Math.min(inputString.length(), currentIndex + count);
        return inputString.substring(currentIndex, min);
    }
    
    private String consumeNextChars(int count) {
        int min = Math.min(inputString.length(), currentIndex + count);
        String substring = inputString.substring(currentIndex, min);
        currentIndex += count;
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
        }
        if(getNextChars(2).equals("//")) {
            consumeNextChars(2);
            while (!consume(System.lineSeparator())) {
                consumeChar();
            }
        }else if(consume("/*")) {
            while (!consume("*/")) consumeChar();
        }
        while(getChar() == ' ' || getChar() == '\n' || getChar() == '\t' || getChar() == '\r') {
            consumeChar();
        }
        
        if(consume("char ")) {
            return new Token(TokenType.t_char);
        }else if(consume("const ")) {
            return new Token(TokenType.t_const);
        }else if(consume("do ")) {
            return new Token(TokenType.t_do);
        }else if(consume("double ")) {
            return new Token(TokenType.t_double);
        }else if(consume("else ")) {
            return new Token(TokenType.t_else);
        }else if(consume("float ")) {
            return new Token(TokenType.t_float);
        }else if(consume("for ")) {
            return new Token(TokenType.t_for);
        }else if(consume("if ")) {
            return new Token(TokenType.t_if);
        }else if(consume("int ")) {
            return new Token(TokenType.t_int);
        }else if(consume("long ")) {
            return new Token(TokenType.t_long);
        }else if(consume("return ")) {
            return new Token(TokenType.t_return);
        }else if(consume("short ")) {
            return new Token(TokenType.t_short);
        }else if(consume("static ")) {
            return new Token(TokenType.t_static);
        }else if(consume("typedef ")) {
            return new Token(TokenType.t_typedef);
        }else if(consume("union ")) {
            return new Token(TokenType.t_union);
        }else if(consume("unsigned ")) {
            return new Token(TokenType.t_unsigned);
        }else if(consume("struct ")) {
            return new Token(TokenType.t_struct);
        }else if(consume("void ")) {
            return new Token(TokenType.t_void);
        }else if(consume("while ")) {
            return new Token(TokenType.t_while);
        }else if(consume("class ")) {
            return new Token(TokenType.t_class);
        }else if(consume("public ")) {
            return new Token(TokenType.t_public);
        }else if(consume("private ")) {
            return new Token(TokenType.t_private);
        }else if(consume("new ")) {
            return new Token(TokenType.t_new);
        }else if(consume("super ")) {
            return new Token(TokenType.t_super);
        }else if(consume("virtual ")) {
            return new Token(TokenType.t_virtual);
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
        }else if(Character.isAlphabetic(getChar()) || getChar() == '_') {
            image += consumeChar();
            while(Character.isAlphabetic(getChar()) || getChar() == '_' || Character.isDigit(getChar())) {
                image += consumeChar();
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
        return createdTokens.get(createdTokens.size() - 1);
    }
    
    public Token getNext() {
        Token t = singleLex();
        createdTokens.add(t);
        return t;
    }
    
    @Override
    public Iterator<Token> iterator() {
        return new LexIterator(this.createdTokens);
    }
    
    
}
