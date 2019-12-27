package radin.interphase.semantics;

import radin.interphase.AbstractTree;
import radin.interphase.lexical.Token;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;

import java.util.*;

public class AbstractSyntaxNode extends AbstractTree<AbstractSyntaxNode> implements Iterable<AbstractSyntaxNode>{
    
    private ASTNodeType type;
    private Token token;
    private List<AbstractSyntaxNode> childList;
    private List<String> hints;
    
    public static final AbstractSyntaxNode EMPTY;
    
    
    private static HashMap<ASTNodeType, List<String>> typeToHints;
    
    static {
        typeToHints = new HashMap<>();
        typeToHints.put(ASTNodeType.method_call, Arrays.asList("Object", "Method Name"));
        EMPTY = new AbstractSyntaxNode(ASTNodeType.empty);
    }
    
    public AbstractSyntaxNode(ASTNodeType type, AbstractSyntaxNode... children) {
        this.type = type;
        childList = new ArrayList<>();
        childList.addAll(Arrays.asList(children));
        hints = typeToHints.getOrDefault(type, new LinkedList<>());
    }
    
    public AbstractSyntaxNode(ASTNodeType type, List<AbstractSyntaxNode> childList) {
        this.type = type;
        this.childList = childList;
        hints = typeToHints.getOrDefault(type, new LinkedList<>());
    }
    
    public TypeAbstractSyntaxNode addType(CXType type) {
        return new TypeAbstractSyntaxNode(this.getType(), type, childList);
    }
    
    public static AbstractSyntaxNode unroll(ASTNodeType type, AbstractSyntaxNode first, AbstractSyntaxNode unrolled) {
        AbstractSyntaxNode[] arr = new AbstractSyntaxNode[1 + unrolled.childList.size()];
        arr[0] = first;
        for (int i = 1; i < arr.length; i++) {
            arr[i] = unrolled.childList.get(i-1);
        }
        return new AbstractSyntaxNode(type, arr);
    }
    
    public static AbstractSyntaxNode bringUpChildren(AbstractSyntaxNode node) {
        List<AbstractSyntaxNode> childrensChildren = new LinkedList<>();
        for (AbstractSyntaxNode abstractSyntaxNode : node.getChildList()) {
            childrensChildren.addAll(abstractSyntaxNode.getChildList());
        }
        return new AbstractSyntaxNode(node.getType(), childrensChildren);
    }
    
    public AbstractSyntaxNode(AbstractSyntaxNode other,
                              AbstractSyntaxNode add, AbstractSyntaxNode... additionalChildren) {
        this(other, false, add, additionalChildren);
    }
    
    public AbstractSyntaxNode(AbstractSyntaxNode other,
                              boolean addFirst, AbstractSyntaxNode add, AbstractSyntaxNode... additionalChildren) {
        this(other.type);
        if(!addFirst) {
            childList.addAll(other.getChildList());
            if(add != EMPTY) childList.add(add);
            childList.addAll(Arrays.asList(additionalChildren));
        }else {
            if(add != EMPTY) childList.add(add);
            childList.addAll(Arrays.asList(additionalChildren));
            childList.addAll(other.getChildList());
        }
    }
    
    public AbstractSyntaxNode(ASTNodeType type, Token token) {
        this(type);
        this.token = token;
        childList = new ArrayList<>();
    }
    
    public List<AbstractSyntaxNode> getChildList() {
        return new ArrayList<>(childList);
    }
    
    
    
    public ASTNodeType getType() {
        return type;
    }
    
    public Token getToken() {
        return token;
    }
    
    public boolean hasToken() {
        return token != null;
    }
    
    public AbstractSyntaxNode getChild(int index) {
        return childList.get(index);
    }
    
    public boolean hasChild(ASTNodeType type) {
        return getChild(type) != null;
    }
    
    public AbstractSyntaxNode getChild(ASTNodeType type) {
        for (AbstractSyntaxNode abstractSyntaxNode : childList) {
            if(abstractSyntaxNode.getType().equals(type)) return abstractSyntaxNode;
        }
        return null;
    }
    
    public List<AbstractSyntaxNode> getChildren(ASTNodeType type){
        List<AbstractSyntaxNode> output = new LinkedList<>();
        for (AbstractSyntaxNode abstractSyntaxNode : childList) {
            if(abstractSyntaxNode.getType().equals(type)) output.add(abstractSyntaxNode);
        }
        return output;
    }
    
    @Override
    protected String toTreeForm(int indent) {
        StringBuilder output = new StringBuilder(indentString(indent));
        return treeFormHelper(indent, output);
    }
    
    
    protected String toTreeForm(int indent, String hint) {
        StringBuilder output = new StringBuilder(String.format("%s%15s", indentString(indent),  "(" + hint + ")"));
        
        return treeFormHelper(indent, output);
    }
    
    private String treeFormHelper(int indent, StringBuilder output) {
        Iterator<String> hints = this.hints.iterator();
        for (AbstractSyntaxNode child : childList) {
            output.append("\n");
            if(hints.hasNext()) {
                output.append(child.toTreeForm(indent + 1, hints.next()));
            } else {
                output.append(child.toTreeForm(indent + 1));
            }
        }
        return output.toString();
    }
    
    @Override
    public String toString() {
        if(token == null) return type.toString();
        if(token.getImage() != null) return type.toString() + " \"" + token.getImage() + "\"";
        return type.toString() + "::" + token.toString();
    }
    
    @Override
    public List<AbstractSyntaxNode> postfix() {
        List<AbstractSyntaxNode> output = new LinkedList<>();
        for (AbstractSyntaxNode child : childList) {
            output.addAll(child.postfix());
        }
        output.add(this);
        return output;
    }
    
    @Override
    public Iterator<AbstractSyntaxNode> iterator() {
        return childList.iterator();
    }
    
    public String getRepresentation() {
        if(token != null)  return token.toString();
        StringBuilder output = new StringBuilder();
        output.append(type.toString());
        if(!getChildList().isEmpty()) {
            output.append(" (");
            boolean first = true;
            for (AbstractSyntaxNode abstractSyntaxNode : childList) {
                if(first) {
                    first = false;
                }else output.append(", ");
                output.append(abstractSyntaxNode.getRepresentation());
            }
            output.append(")");
        }
        return output.toString();
    }
    
    @Override
    public List<? extends AbstractTree<AbstractSyntaxNode>> getDirectChildren() {
        return childList;
    }
}
