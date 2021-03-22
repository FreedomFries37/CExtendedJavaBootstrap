package radin.input;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;

import java.util.LinkedList;
import java.util.List;


/**
 * The abstract class any Tokenizer must inherit in order to be used
 * @param <T> the type to output. If T extends {@link Token} then the {@link Tokenizer#getKeywordToken(String)} is
 *           usable
 */
public abstract class Tokenizer<T> implements ITokenizer<T> {
    
    private static ICompilationSettings<?,?,?> compilationSettings;
    protected String inputString;
    protected List<T> createdTokens;
    protected int tokenIndex;
    protected int currentIndex;
    protected int column;
    protected int lineNumber;
    protected String filename;
    
    protected final static char EOF = '\0';
    
    public Tokenizer(String inputString, String filename) {
        this.inputString = inputString;
        createdTokens = new LinkedList<>();
        tokenIndex = -1;
        column = 1;
        lineNumber = 1;
        setFilename(filename);
    }
    
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    @Override
    public T invoke() {
        return getNext();
    }
    
    @Override
    public <V> void setVariable(String variable, V value) {
        switch (variable) {
            case "filename": {
                setFilename((String) value);
                break;
            }
            case "inputString": {
                this.inputString = (String) value;
                break;
            }
        }
    }
    
    @Override
    public <V> V getVariable(String variable) {
        return null;
    }
    
    
    
    /**
     * Gets a token based on a image. If it's defined as a keyword, it returns a keyword token. Otherwise it returns
     * a {@link TokenType#t_id} token
     * @param image the input image
     * @return the appropriate token
     */
    public Token getKeywordToken(String image) {
        if(image.startsWith("__") && !(UniversalCompilerSettings.getInstance().getSettings().isInRuntimeCompilationMode() || image.equals(
                "__get_class"))) {
            return new Token(TokenType.t_reserved, image);
        }
        switch (image) {
            case "char":
                return new Token(TokenType.t_char);
            case "const":
                return new Token(TokenType.t_const);
            case "do":
                return new Token(TokenType.t_do);
            case "double":
                return new Token(TokenType.t_double);
            case "else":
                return new Token(TokenType.t_else);
            case "float":
                return new Token(TokenType.t_float);
            case "for":
                return new Token(TokenType.t_for);
            case "if":
                return new Token(TokenType.t_if);
            case "int":
                return new Token(TokenType.t_int);
            case "long":
                return new Token(TokenType.t_long);
            case "return":
                return new Token(TokenType.t_return);
            case "short":
                return new Token(TokenType.t_short);
            case "static":
                return new Token(TokenType.t_static);
            case "typedef":
                return new Token(TokenType.t_typedef);
            case "union":
                return new Token(TokenType.t_union);
            case "unsigned":
                return new Token(TokenType.t_unsigned);
            case "struct":
                return new Token(TokenType.t_struct);
            case "void":
                return new Token(TokenType.t_void);
            case "while":
                return new Token(TokenType.t_while);
            case "class":
                return new Token(TokenType.t_class);
            case "public":
                return new Token(TokenType.t_public);
            case "private":
                return new Token(TokenType.t_private);
            case "new":
                return new Token(TokenType.t_new);
            case "super":
                return new Token(TokenType.t_super);
            case "virtual":
                return new Token(TokenType.t_virtual);
            case "sizeof":
                return new Token(TokenType.t_sizeof);
            case "boolean":
                return new Token(TokenType.t_typename, image);
            case "in":
                return new Token(TokenType.t_in);
            case "implement":
                return new Token(TokenType.t_implement);
            case "internal":
                return new Token(TokenType.t_internal);
            case "using":
                    return new Token(TokenType.t_using);
            case "typeid":
                return new Token(TokenType.t_typeid);
            case "true":
                return new Token(TokenType.t_true);
            case "false":
                return new Token(TokenType.t_false);
            case "ast":
                return new Token(TokenType.t_ast);
            case "is":
                return new Token(TokenType.t_is);
            case "abstract":
                return new Token(TokenType.t_abstract);
            case "trait":
                return new Token(TokenType.t_trait);
            case "enum":
                return new Token(TokenType.t_enum);

            default:
                break;
        }
        return new Token(TokenType.t_id, image);
    }
    
    @Override
    public int getTokenIndex() {
        return tokenIndex;
    }
    
    @Override
    public void setTokenIndex(int tokenIndex) {
        if(tokenIndex < -1 || tokenIndex >= createdTokens.size()) return;
        this.tokenIndex = tokenIndex;
    }
    
    @Override
    public String getInputString() {
        return inputString;
    }
    
    /**
     * Fully runs through the tokenization of a file
     * Although a {@link IParser<T>} can be used such that tokens are lexed at parse time, this ensures there are no
     * lexical errors before running the parser
     * @return the amount of tokens created
     */
    @Override
    public int run() {
        int count = 0;
        for (T token : this){
            if(token == null) break;
            ++count;
        }
        return count;
    }
    
    /**
     * Gets the first token created
     * @return such a token
     */
    @Override
    public T getFirst() {
        return createdTokens.get(0);
    }
    
    /**
     * Gets the last token created
     * @return such a token
     */
    @Override
    public T getLast() {
        return createdTokens.get(createdTokens.size() - 1);
    }
    
    /**
     * Gets the previously generated token
     * @return such a token, or null if a token doesn't exist
     */
    @Override
    public T getPrevious() {
        if(createdTokens.size() < 1) return null;
        int prevIndex = Math.min(tokenIndex - 1, createdTokens.size() - 1);
        return createdTokens.get(prevIndex);
    }
    
    /**
     * Gets the current token
     * @return such a token, or null if a token wasn't generated
     */
    @Override
    public T getCurrent() {
        if(createdTokens.size() == 0) return getNext();
        return createdTokens.get(tokenIndex);
    }
    
    public static ICompilationSettings getCompilationSettings() {
        return compilationSettings;
    }
    
    public static void setCompilationSettings(ICompilationSettings compilationSettings) {
        Tokenizer.compilationSettings = compilationSettings;
    }
    
    /**
     * Gets the current character the lexer is on
     * @return the char. If it's the EOF, returns {@link TokenType#t_eof}
     */
    protected char getChar() {
        if(currentIndex == getInputString().length()) return EOF;
        return getInputString().charAt(currentIndex);
    }
    
    /**
     * Consumes the current character, making the current character the next available character
     * @return the current character before this method was ran.
     */
    protected char consumeChar() {
        if(getChar() == '\n') {
            ++lineNumber;
            column = 1;
        } else if(getChar() == '\t' ) {
            column += getCompilationSettings().getTabSize();
        } else {
            column++;
        }
        return getInputString().charAt(currentIndex++);
    }
    
    /**
     * Returns the next {@code count} number of characters
     * @param count the number of characters max in the output string
     * @return a string containing the next n characters, where n is <= {@code count} depending on the amount of
     * characters left in the input string
     */
    protected String getNextChars(int count) {
        int min = Math.min(getInputString().length(), currentIndex + count);
        return getInputString().substring(currentIndex, min);
    }
   
    /**
     * Returns the next {@code count} number of characters, then setss the current character to be one past the end of
     * the outputted string
     * @param count the number of characters max in the output string
     * @return a string containing the next n characters, where n is <= {@code count} depending on the amount of
     * characters left in the input string
     */
    protected String consumeNextChars(int count) {
        int min = Math.min(getInputString().length(), currentIndex + count);
        String substring = getInputString().substring(currentIndex, min);
        for (int i = currentIndex; i < min; i++) {
            consumeChar();
        }
        return substring;
    }
    
    /**
     * Consumes a string if the string matches.
     * This is implemented using {@link Tokenizer#match(String)} and {@link Tokenizer#consumeNextChars(int)}
     * @param str the string to test
     * @return whether it is consumed
     */
    protected boolean consume(String str) {
        if(match(str)) {
            consumeNextChars(str.length());
            return true;
        }
        return false;
    }
    
    /**
     * Consumes a character if and only if the character is the current character
     * @param c the character to match with
     * @return if the character was consumed
     */
    protected boolean consume(char c) {
        if(match(c)) {
            consumeChar();
            return true;
        }
        return false;
    }
    
    protected int getColumn() {
        int output = 1;
        int fakeIndex = currentIndex- 1;
        while(fakeIndex >= 0 && getInputString().charAt(fakeIndex) != '\n') {
            fakeIndex--;
            output++;
        }
        return output;
    }
    
    protected boolean match(char c) {
        return getChar() == c;
    }
    
    /**
     * Checks to see if the next characters in the input string matches {@code str}.
     * @param str the string to test
     * @return whether it's an exact match
     */
    protected boolean match(String str) {
        return getNextChars(str.length()).equals(str);
    }
    
    protected abstract T singleLex();
    
    @Override
    public T getNext() {
        if(++tokenIndex == createdTokens.size()) {
            T tok;
            try {
                 tok = singleLex();
                 
            }catch (AbstractCompilationError e) {
                getErrors().add(e);
                //finishedIndex = getTokenIndex();
                tok = null;
            }
            if(tok == null) return null;
            createdTokens.add(tok);
            return tok;
        }
        return createdTokens.get(getTokenIndex());
    }
    
    @Override
    public void reset() {
        tokenIndex = 0;
        createdTokens.clear();
        tokenIndex = -1;
        column = 1;
        lineNumber = 1;
        currentIndex = 0;
    }
}
