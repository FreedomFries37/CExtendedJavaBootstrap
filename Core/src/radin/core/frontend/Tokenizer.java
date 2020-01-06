package radin.core.frontend;

import radin.core.lexical.Token;
import radin.utility.ICompilationSettings;

import java.util.LinkedList;
import java.util.List;

public abstract class Tokenizer<T> implements ITokenizer<T> {
    
    private static ICompilationSettings compilationSettings;
    protected String inputString;
    protected List<T> createdTokens;
    protected int tokenIndex;
    protected int currentIndex;
    protected int column;
    protected int lineNumber;
    protected String filename;
    
    public Tokenizer(String inputString, String filename) {
        this.inputString = inputString;
        createdTokens = new LinkedList<>();
        tokenIndex = -1;
        column = 1;
        lineNumber = 1;
        this.filename = filename;
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
    
    @Override
    public void run() {
        for (T token : this);
    }
    
    @Override
    public T getFirst() {
        return createdTokens.get(0);
    }
    
    @Override
    public T getLast() {
        return createdTokens.get(createdTokens.size() - 1);
    }
    
    @Override
    public T getPrevious() {
        if(createdTokens.size() < 1) return null;
        return createdTokens.get(tokenIndex - 1);
    }
    
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
    
    protected char getChar() {
        if(currentIndex == getInputString().length()) return '\0';
        return getInputString().charAt(currentIndex);
    }
    
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
    
    protected String getNextChars(int count) {
        int min = Math.min(getInputString().length(), currentIndex + count);
        return getInputString().substring(currentIndex, min);
    }
    
    protected String consumeNextChars(int count) {
        int min = Math.min(getInputString().length(), currentIndex + count);
        String substring = getInputString().substring(currentIndex, min);
        for (int i = currentIndex; i < min; i++) {
            consumeChar();
        }
        return substring;
    }
    
    protected boolean consume(String str) {
        if(match(str)) {
            consumeNextChars(str.length());
            return true;
        }
        return false;
    }
    
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
    
    protected boolean match(String str) {
        return getNextChars(str.length()).equals(str);
    }
    
    @Override
    public abstract T getNext();
    
    @Override
    public void reset() {
        tokenIndex = 0;
    }
}
