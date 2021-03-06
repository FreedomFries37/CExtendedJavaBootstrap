package radin.output.typeanalysis;

import radin.core.lexical.Token;
import radin.core.semantics.NamespaceTree;
import radin.core.semantics.types.AmbiguousIdentifierError;
import radin.core.semantics.types.CXIdentifier;
import radin.core.utility.Option;
import radin.output.typeanalysis.errors.IdentifierDoesNotExistError;

import java.util.*;
import java.util.function.Consumer;

public class IdentifierResolver {

    private final NamespaceTree<CXIdentifier> tree = new NamespaceTree<>();
    private Option<CXIdentifier> currentNamespace;
    private List<CXIdentifier> usingNamespaces = new LinkedList<>();

    public IdentifierResolver() {
        currentNamespace = Option.None();
    }

    public IdentifierResolver(CXIdentifier initialNamespace) {
        currentNamespace = Option.Some(initialNamespace);
        tree.addNamespace(initialNamespace);
    }

    public void pushNamespace(CXIdentifier path) {
        CXIdentifier fullPath = currentNamespace.isSome() ? CXIdentifier.from(currentNamespace.unwrap(), path) : path;
        tree.addNamespace(fullPath);
        currentNamespace = Option.Some(fullPath);
    }

    public void useNamespace(CXIdentifier namespace) {
        Set<CXIdentifier> resolvedSet = tree.getNamespaces(currentNamespace.thisOrElse(null), namespace);
        if(resolvedSet.size() == 0) {
            throw new IdentifierDoesNotExistError(namespace);
        } else if (resolvedSet.size() > 1) {
            throw new AmbiguousIdentifierError(namespace.getBase(), new ArrayList<>(resolvedSet));
        }
        CXIdentifier resolvedNamespace = resolvedSet.iterator().next();
        usingNamespaces.add(resolvedNamespace);
    }

    public void popNamespace() {
        CXIdentifier parent = currentNamespace.expect("Can't pop the empty namespace").getParentNamespace();
        currentNamespace = Option.Some(parent);
    }

    public CXIdentifier createIdentity(CXIdentifier id) {
        if(currentNamespace.isNone()) return id;
        CXIdentifier fullPath = CXIdentifier.from(currentNamespace.unwrap(), id);
        CXIdentifier parentPath = fullPath.getParentNamespace();
        tree.addNamespace(parentPath);
        List<CXIdentifier> objects = tree.getObjectsForNamespace(parentPath);
        if(!objects.contains(fullPath)) {
            objects.add(fullPath);
        }

        return fullPath;
    }

    public CXIdentifier createIdentity(Token id) {
        return createIdentity(new CXIdentifier(id));
    }

    public Option<CXIdentifier> resolvePath(CXIdentifier path) {
        CXIdentifier parent = currentNamespace.thisOrElse(null);
        Option<CXIdentifier> output = Option.None();
        CXIdentifier fromIdentifier = tree.getFromIdentifier(parent, path);
        if (fromIdentifier != null) {
            output = Option.Some(fromIdentifier);
        }
        for (CXIdentifier using : usingNamespaces) {
            CXIdentifier id = tree.getFromIdentifier(using, path);
            if(id != null) {
                if (output.isNone()) {
                    output = Option.Some(id);
                } else {
                    throw new AmbiguousIdentifierError(id.getBase(), Collections.singletonList(output.unwrap()));
                }
            }
        }
        return output;
    }

    public CXIdentifier toAbsolutePath(CXIdentifier path) {
        CXIdentifier parent = path.getParentNamespace();
        if(parent == null) return createIdentity(path);
        Set<CXIdentifier> resolvedSet = tree.getNamespaces(currentNamespace.thisOrElse(null), parent);
        if(resolvedSet.size() == 0) {
            throw new IdentifierDoesNotExistError(parent);
        } else if (resolvedSet.size() > 1) {
            throw new AmbiguousIdentifierError(parent.getBase(), new ArrayList<>(resolvedSet));
        }
        CXIdentifier resolvedNamespace = resolvedSet.iterator().next();
        return new CXIdentifier(resolvedNamespace, path.getBase());
    }

}
