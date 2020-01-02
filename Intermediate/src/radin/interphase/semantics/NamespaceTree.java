package radin.interphase.semantics;

import radin.interphase.semantics.types.CXIdentifier;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.compound.CXCompoundType;

import java.util.LinkedList;
import java.util.List;

public class NamespaceTree {

    private class Node {
        private String shorthand;
        private CXIdentifier identifier;
        private List<Node> children;
        private List<CXCompoundType> relatedTypes;
    
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
    
        public boolean add(CXCompoundType type) {
            return relatedTypes.add(type);
        }
    
        public List<CXCompoundType> getRelatedTypes() {
            return relatedTypes;
        }
    }
    
    private List<Node> topNamespaces;
    
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
            if(child.shorthand.equals(path.getIdentifier())) return child.identifier;
        }
        return null;
    }
    
    /**
     * Assume base case is id::typename
     * @param identifier
     * @return
     */
    public CXType getTypeForIdentifier(CXIdentifier currentNamespace, CXIdentifier identifier) {
        CXIdentifier namespace = identifier.getParentNamespace();
        CXIdentifier absoluteNameSpace = getNamespace(currentNamespace, namespace);
        if(absoluteNameSpace == null) return null;
        for (CXCompoundType relatedType : getNode(absoluteNameSpace).getRelatedTypes()) {
            if(relatedType.getTypeNameIdentifier().getParentNamespace().equals(absoluteNameSpace)) return relatedType;
        }
        return null;
    }
    
    public List<CXCompoundType> getTypesForNamespace(CXIdentifier identifier) {
        Node node = getNode(identifier);
        if(node == null) return null;
        return node.getRelatedTypes();
    }
    
    private Node getNode(CXIdentifier currentNamespace) {
        if(currentNamespace.getParentNamespace() == null) {
            for (Node topNamespace : topNamespaces) {
                if(topNamespace.identifier.equals(currentNamespace)) return topNamespace;
            }
        } else {
            Node parent = getNode(currentNamespace.getParentNamespace());
            if(parent == null) return null;
            for (Node child : parent.children) {
                if(child.identifier == currentNamespace) return child;
            }
        }
        return null;
    }
    
    public boolean namespaceExists(CXIdentifier fullPath) {
        return getNode(fullPath) != null;
    }
    
    public void addNamespace(CXIdentifier namespace) {
        if(namespaceExists(namespace)) return;
        if(namespace.getParentNamespace() == null) {
            topNamespaces.add(new Node(namespace.getIdentifier(), namespace));
        } else {
            if(!namespaceExists(namespace.getParentNamespace())) {
                addNamespace(namespace.getParentNamespace());
            }
            getNode(namespace.getParentNamespace()).add(
                    new Node(namespace.getIdentifier(), namespace)
            );
        }
    }
}
