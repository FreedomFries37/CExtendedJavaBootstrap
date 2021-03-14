package radin.core;

import radin.core.IdentifierResolver;
import radin.core.Namespaced;
import radin.core.lexical.Token;
import radin.core.semantics.types.CXIdentifier;
import radin.core.utility.Option;

import java.util.HashMap;
import java.util.HashSet;

public class IdentifierResolverMapper<T extends Namespaced> {
    private HashMap<CXIdentifier, T> map;
    private IdentifierResolver resolver;

    public IdentifierResolverMapper() {
        map = new HashMap<>();
        resolver = new IdentifierResolver();
    }

    public CXIdentifier add(T object) {
        CXIdentifier id = resolver.createIdentity(object.getIdentifier());
        map.put(id, object);
        return id;
    }

    public T get(CXIdentifier id) {
        CXIdentifier resolved = resolvePath(id).unwrap();
        return map.get(resolved);
    }

    public void pushNamespace(CXIdentifier path) {
        resolver.pushNamespace(path);
    }

    public void useNamespace(CXIdentifier namespace) {
        resolver.useNamespace(namespace);
    }

    public void stopUseNamespace(CXIdentifier namespace) {
        resolver.stopUseNamespace(namespace);
    }

    public void popNamespace() {
        resolver.popNamespace();
    }

    public Option<CXIdentifier> resolvePath(CXIdentifier path) {
        return resolver.resolvePath(path);
    }

    public CXIdentifier toAbsolutePath(CXIdentifier path) {
        return resolver.toAbsolutePath(path);
    }
}
