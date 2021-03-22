package radin.core.lexical;

import java.util.Objects;

import static radin.core.lexical.TokenType.t_id;

public class Token implements Comparable<Token>, Cloneable {
    
    private TokenType type;
    private String image;
    private int virtualColumn = -1;
    private int virtualLineNumber = -1;
    private Token previous;
    private Token next;
    
    private int actualLineNumber;
    private String filename;
    
    
    public Token(TokenType type) {
        this.type = type;
    }
    
    public Token(TokenType type, String image) {
        this.type = type;
        this.image = image;
    }
    
    public Token changedType(TokenType tokenType) {
        Token output = new Token(tokenType, image);
        output.addColumnAndLineNumber(virtualColumn, virtualLineNumber);
        return output;
    }
    
    public Token getPrevious() {
        return previous;
    }
    
    public void setPrevious(Token previous) {
        this.previous = previous;
        if(previous != null)
            previous.next = this;
    }
    
    public Token getNext() {
        return next;
    }
    
    public void setNext(Token next) {
        this.next = next;
        if (next != null)
            next.previous = this;
    }
    
    /**
     * The column in the Pre Processor Output
     * @return
     */
    public int getVirtualColumn() {
        return virtualColumn;
    }
    
    /**
     * The line number in the Pre Processe rOutput
     * @return
     */
    public int getVirtualLineNumber() {
        return virtualLineNumber;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getImage() {
        return image;
    }
    
    public Token addColumnAndLineNumber(int column, int lineNumber) {
        this.virtualColumn = column;
        this.virtualLineNumber = lineNumber;
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
    
    public int getActualLineNumber() {
        return actualLineNumber;
    }
    
    public void setActualLineNumber(int actualLineNumber) {
        this.actualLineNumber = actualLineNumber;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
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
        if(getVirtualLineNumber() != o.getVirtualLineNumber()) return getVirtualLineNumber() - o.getVirtualLineNumber();
        return getVirtualColumn() - o.getVirtualColumn();
    }
    
    public String info() {
        if(virtualLineNumber > 0 && virtualColumn >= 0) {
            return toString() + String.format(" at ln: %d (c: %d)", virtualLineNumber, virtualColumn);
        }
        return toString();
    }
}
