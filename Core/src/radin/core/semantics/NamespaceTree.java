package radin.core.semantics;

import radin.core.Namespaced;
import radin.core.semantics.types.AmbiguousIdentifierError;
import radin.core.semantics.types.CXIdentifier;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class NamespaceTree<T extends Namespaced> {

    private class Node {
        private String shorthand;
        private CXIdentifier identifier;
        private List<Node> children;
        private List<T> relatedTypes;
    
        public Node(String shorthand, CXIdentifier identifier) {
            this.shorthand = shorthand;
            this.identifier = identifier;
            children = new LinkedList<>();
            relatedTypes = new LinkedList<>();
        }
    
        public boolean add(Node node) {
            return children.add(node);
        }
    
        public List<Node> getChildren() {
            return children;
        }
    
        public String getShorthand() {
            return shorthand;
        }
    
        public CXIdentifier getIdentifier() {
            return identifier;
        }
    
        public boolean add(T type) {
            return relatedTypes.add(type);
        }
    
        public List<T> getRelatedTypes() {
            return relatedTypes;
        }
    }
    
    private List<Node> topNamespaces;
    private List<T> baseObjects = new LinkedList<>();
    
    public NamespaceTree() {
        this.topNamespaces = new LinkedList<>();
    }
    
    public CXIdentifier getNamespace(CXIdentifier currentNamespace, CXIdentifier path) {
        if(namespaceExists(path)) return path;
        if(!namespaceExists(currentNamespace)) return null;
        if(path == null) return currentNamespace;
        CXIdentifier parent = getNamespace(currentNamespace, path.getParentNamespace());
        if(parent == null) return null;
        for (Node child : getNode(parent).children) {
            if(child.shorthand.equals(path.getIdentifierString())) return child.identifier;
        }
        return null;
    }


    public Set<CXIdentifier> getNamespaces(CXIdentifier currentNamespace, CXIdentifier path) {
        Set<CXIdentifier> output = new HashSet<>();
        if(namespaceExists(path)) output.add(path);
        if(namespaceExists(currentNamespace)) {
            if (path == null) output.add(currentNamespace);
            else {
                Set<CXIdentifier> parents = getNamespaces(currentNamespace, path.getParentNamespace());
                for (CXIdentifier parent : parents) {
                    for (Node child : getNode(parent).children) {
                        if (child.shorthand.equals(path.getIdentifierString())) output.add(child.identifier);
                    }
                }
                
            }
        }
        return output;
    }
    

    
    public List<T> getObjectsForNamespace(CXIdentifier identifier) {
        if(identifier == null) return baseObjects;
        Node node = getNode(identifier);
        if(node == null) return null;
        return node.getRelatedTypes();
    }
    
    public List<T> getBaseObjects() {
        return baseObjects;
    }
    
    private Node getNode(CXIdentifier currentNamespace) {
        if(currentNamespace == null) return null;
        if(currentNamespace.getParentNamespace() == null) {
            for (Node topNamespace : topNamespaces) {
                if(topNamespace.identifier.equals(currentNamespace)) return topNamespace;
            }
        } else {
            Node parent = getNode(currentNamespace.getParentNamespace());
            if(parent == null) return null;
            for (Node child : parent.children) {
                if(child.identifier.equals(currentNamespace)) return child;
            }
        }
        return null;
    }
    
    public boolean namespaceExists(CXIdentifier namespacePath) {
        return getNode(namespacePath) != null;
    }

    public T getFromIdentifier(CXIdentifier currentNamespace, CXIdentifier path) {
        if(path.getParentNamespace() == null) {
            for(T o : baseObjects) {
                if (o.getIdentifier().equals(path)) {
                    return o;
                }
            }
        }

        List<T> output = new LinkedList<>();

        for (CXIdentifier namespace : getNamespaces(currentNamespace, path.getParentNamespace())) {
            for (T object : getObjectsForNamespace(namespace)) {
                if(object.getIdentifier().getIdentifierString().equals(path.getIdentifierString())) {
                    output.add(object);
                }
            }
        }


        if(output.size() > 1) throw new AmbiguousIdentifierError(path.getBase(), output);
        else if(output.size() == 1) return output.get(0);

        return null;
    }
    
    public void addNamespace(CXIdentifier namespace) {
        if(namespaceExists(namespace)) return;
        if(namespace.getParentNamespace() == null) {
            topNamespaces.add(new Node(namespace.getIdentifierString(), namespace));
        } else {
            if(!namespaceExists(namespace.getParentNamespace())) {
                addNamespace(namespace.getParentNamespace());
            }
            getNode(namespace.getParentNamespace()).add(
                    new Node(namespace.getIdentifierString(), namespace)
            );
        }
    }
}
