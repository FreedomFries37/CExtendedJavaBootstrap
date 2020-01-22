package radin.core.semantics.types;

import radin.core.lexical.Token;

/**
 * The visibility of a field in a {@link radin.core.semantics.types.compound.ICXClassType}
 * This is enforced statically in compilation time, and resulting code will have everything visible
 */
public enum Visibility {
    /**
     * Can be seen in any scope
     */
    _public("public"),
    /**
     * Can be seen only in the scope of the declaring class and it's inheriting types
     */
    _private("private"),
    /**
     * Can be seen only in the scope of the declaring class
     */
    internal("internal");
    
    String str;
    
    Visibility(String str) {
        this.str = str;
    }
    
    /**
     * Converts a token to its equivalent Visibility
     * @param token input token
     * @return a Visiblity object, or null if the token is invalid
     */
    public static Visibility getVisibility(Token token) {
        switch (token.getType()) {
            case t_public: return _public;
            case t_private: return _private;
            case t_internal: return internal;
            default:
                return null;
        }
    }
    
    @Override
    public String toString() {
        return str;
    }
}
