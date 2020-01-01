package radin.interphase;

import radin.interphase.lexical.Token;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCompilationError extends Error {
    
    /**
     * A class representing error information
     */
    public static class ErrorInformation implements Comparable<ErrorInformation> {
        private String info;
        private Token token;
    
        public ErrorInformation(String info, Token token) {
            this.info = info;
            this.token = token;
        }
    
        public String getInfo() {
            return info;
        }
    
        public Token getToken() {
            return token;
        }
    
        @Override
        public int compareTo(ErrorInformation o) {
            return getToken().compareTo(o.getToken());
        }
    }
    
    
    private String[] errorInformation;
    private List<Token> correspondingTokens;
    
    
    /**
     * Create a compilation error object for useful error messages
     * The size of the error information array must be <= the size of the correspond tokens
     * Anything after that will be ignored.
     *
     * @param error error string
     * @param correspondingTokens the corresponding tokens to point to
     * @param errorInformation helpful information strings
     */
    public AbstractCompilationError(String error, List<Token> correspondingTokens, String... errorInformation) {
        super(error);
        this.correspondingTokens = correspondingTokens;
        this.errorInformation = errorInformation;
    }
    
    /**
     * Create a compilation error object for useful error messages
     * The size of the error information array must be <= the size of the correspond tokens
     * Anything after that will be ignored.
     *
     * @param error error string
     * @param correspondingTokens the corresponding tokens to point to
     * @param errorInformation helpful information strings
     */
    public AbstractCompilationError(Throwable error, List<Token> correspondingTokens, String... errorInformation) {
        super(error);
        this.correspondingTokens = correspondingTokens;
        this.errorInformation = errorInformation;
    }
    
    /**
     * Create a compilation error object for useful error messages
     * The size of the error information array must be <= the size of the correspond tokens
     * Anything after that will be ignored.
     *
     * @param correspondingTokens the corresponding tokens to point to
     * @param errorInformation helpful information strings
     */
    public AbstractCompilationError(List<Token> correspondingTokens, String... errorInformation) {
        super();
        this.correspondingTokens = correspondingTokens;
        this.errorInformation = errorInformation;
    }
    
    
    /**
     * Get the info at an index
     * If the token does not have a corresponding error information, a null string is passed;
     * @param index the index of a token
     * @return an Error information token;
     */
    public ErrorInformation getInfo(int index) {
        if(index < 0 || index >= correspondingTokens.size()) throw new IndexOutOfBoundsException(index);
        String info = null;
        if(index < errorInformation.length) {
            info = errorInformation[index];
        }
        return new ErrorInformation(info, correspondingTokens.get(index));
    }
    
    /**
     * Returns a list of all ErrorInformation objects for this error.
     * @param sort whether to sort the output based on line number and column
     * @return the resulting list
     */
    public List<ErrorInformation> getInfo(boolean sort) {
        List<ErrorInformation> output = new ArrayList<>(correspondingTokens.size());
        for (int i = 0; i < correspondingTokens.size(); i++) {
            output.add(getInfo(i));
        }
        if(sort) {
            output.sort(ErrorInformation::compareTo);
        }
        return output;
    }
    
}
