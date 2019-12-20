package radin.lexing;

public class Token {

    private TokenType type;
    private String image;
    
    public Token(TokenType type) {
        this.type = type;
    }
    
    public Token(TokenType type, String image) {
        this.type = type;
        this.image = image;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getImage() {
        return image;
    }
    
    @Override
    public String toString() {
        if(image == null) return type.toString();
        return String.format("%s[%s]", type.toString(), image);
    }
}
