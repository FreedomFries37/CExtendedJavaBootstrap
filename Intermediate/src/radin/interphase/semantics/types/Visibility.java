package radin.interphase.semantics.types;

import radin.interphase.lexical.Token;

public enum Visibility {
    _public,
    _private,
    internal;
    
    public static Visibility getVisibility(Token token) {
        switch (token.getType()) {
            case t_public: return _public;
            case t_private: return _private;
            case t_internal: return internal;
            default:
                return null;
        }
    }
}
