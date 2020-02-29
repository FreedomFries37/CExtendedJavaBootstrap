package radin.core.lexical;

import java.util.Objects;

import static radin.core.lexical.TokenType.t_id;

public class Token implements Comparable<Token> {
    
    private TokenType type;
    private String image;
    private int column = -1;
    private int lineNumber = -1;
    private Token previous;
    
    
    
    public Token(TokenType type) {
        this.type = type;
    }
    
    public Token(TokenType type, String image) {
        this.type = type;
        this.image = image;
    }
    
    public Token changedType(TokenType tokenType) {
        Token output = new Token(tokenType, image);
        output.addColumnAndLineNumber(column, lineNumber);
        return output;
    }
    
    public Token getPrevious() {
        return previous;
    }
    
    public void setPrevious(Token previous) {
        this.previous = previous;
    }
    
    public int getColumn() {
        return column;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getImage() {
        return image;
    }
    
    public Token addColumnAndLineNumber(int column, int lineNumber) {
        this.column = column;
        this.lineNumber = lineNumber;
        return this;
    }
    
    @Override
    public String toString() {
        if(image == null) return "@" + type.toString();
        return String.format("@%s[%s]", type.toString(), image);
    }
    
    public String getRepresentation() {
        if(image != null) return image;
        return type.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return (type == token.type ||
                        (
                                (type == t_id || type == TokenType.t_typename) &&
                                        (token.type == t_id || token.type == TokenType.t_typename)
                        )
                ) &&
                image.equals(token.image);
    }
    
    @Override
    public int hashCode() {
        if(type == TokenType.t_typename) return Objects.hash(t_id, image);
        return Objects.hash(type, image);
    }
    
    @Override
    public int compareTo(Token o) {
        if(getLineNumber() != o.getLineNumber()) return getLineNumber() - o.getLineNumber();
        return getColumn() - o.getColumn();
    }
    
    public String info() {
        if(lineNumber > 0 && column >= 0) {
            return toString() + String.format(" at ln: %d (c: %d)", lineNumber, column);
        }
        return toString();
    }
}
