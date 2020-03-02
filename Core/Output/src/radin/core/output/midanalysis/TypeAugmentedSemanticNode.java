package radin.core.output.midanalysis;

import radin.core.lexical.Token;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.ASTMeaningfulNode;
import radin.core.semantics.types.CXCompoundTypeNameIndirection;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.ICXWrapper;
import radin.core.output.tags.ICompilationTag;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class TypeAugmentedSemanticNode extends ASTMeaningfulNode<TypeAugmentedSemanticNode> {
    
    private AbstractSyntaxNode astNode;
    
    private TypeAugmentedSemanticNode parent;
    private List<TypeAugmentedSemanticNode> children;
    
    private boolean isTypedExpression = false;
    private CXType type;
    
    private boolean isLValue = false;
    private boolean isFailurePoint = false;
    
    private HashSet<ICompilationTag> compilationTags;
    
    
    public TypeAugmentedSemanticNode(AbstractSyntaxNode base) {
        this.astNode = base;
        parent = null;
        children = new LinkedList<>();
        compilationTags = new HashSet<>();
    }
    
    public boolean isLValue() {
        return isLValue;
    }
    
    public void setLValue(boolean LValue) {
        isLValue = LValue;
    }
    
    public boolean isFailurePoint() {
        return isFailurePoint;
    }
    
    public void setFailurePoint(boolean failurePoint) {
        isFailurePoint = failurePoint;
    }
    
    public TypeAugmentedSemanticNode(AbstractSyntaxNode astNode, TypeAugmentedSemanticNode parent, List<TypeAugmentedSemanticNode> children) {
        this.astNode = astNode;
        setParent(parent);
        addAllChildren(children);
        compilationTags = new HashSet<>();
    }
    
    public TypeAugmentedSemanticNode(AbstractSyntaxNode astNode, List<TypeAugmentedSemanticNode> children) {
        this.astNode = astNode;
        parent = null;
        this.children = new LinkedList<>();
        /*
        for (TypeAugmentedSemanticNode child : children) {
            this.children.add(child);
            child.parent = this;
        }
       
         */
        addAllChildren(children);
        compilationTags = new HashSet<>();
    }
    
    public TypeAugmentedSemanticNode(AbstractSyntaxNode astNode, TypeAugmentedSemanticNode parent) {
        this.astNode = astNode;
        setParent(parent);
        children = new LinkedList<>();
        compilationTags = new HashSet<>();
    }
    
    public TypeAugmentedSemanticNode getParent() {
        return parent;
    }
    
    public List<TypeAugmentedSemanticNode> getChildren() {
        return children;
    }
    
    public void setParent(TypeAugmentedSemanticNode parent) {
        if(this.parent != null) {
            parent.getChildren().remove(this);
        }
        this.parent = parent;
        parent.getChildren().add(this);
    }
    
    public void addChild(TypeAugmentedSemanticNode child) {
        child.setParent(this);
    }
    
    public void addAllChildren(List<? extends TypeAugmentedSemanticNode> children) {
        children.forEach(this::addChild);
    }
    
    public boolean isTypedExpression() {
        return isTypedExpression;
    }
    
    public AbstractSyntaxNode getSyntaxNode() {
        return astNode;
    }
    
    public ASTNodeType getASTType() {
        return astNode.getType();
    }
    
    public CXType getCXType() {
        //if(type instanceof CXDynamicTypeDefinition) return ((CXDynamicTypeDefinition) type).getWrappedType();
        if(type instanceof CXCompoundTypeNameIndirection) {
            return type.getCTypeIndirection();
        }
        if(type instanceof ICXWrapper) {
            return ((ICXWrapper) type).getWrappedType();
        }
        return type;
    }
    
    public Token getToken() {
        return astNode.getToken();
    }
    
    public TypeAugmentedSemanticNode getChild(int index) {
        return getChild(index, ASTNodeType.syntax);
    }
    
    public TypeAugmentedSemanticNode getChild(int index, ASTNodeType ignoreType) {
        int virtualIndex = 0;
        for (TypeAugmentedSemanticNode child : children) {
            if (child.getASTType() != ignoreType) {
                if(virtualIndex == index) return child;
                
                virtualIndex++;
            }
        }
        return children.get(index);
    }
    
    public boolean hasASTChild(ASTNodeType type) {
        return astNode.hasChild(type);
    }
    
    public List<TypeAugmentedSemanticNode> getAllChildren(ASTNodeType type) {
        List<TypeAugmentedSemanticNode> output = new LinkedList<>();
        if(this.astNode.getType() == type) output.add(this);
        for (TypeAugmentedSemanticNode child : children) {
            output.addAll(child.getAllChildren(type));
        }
        return output;
    }
    
    public TypeAugmentedSemanticNode getASTChild(ASTNodeType type) {
        for (TypeAugmentedSemanticNode typeAugmentedSemanticNode : children) {
            if(typeAugmentedSemanticNode.astNode.getType() == type) return typeAugmentedSemanticNode;
        }
        return null;
    }
    
    
    public TypeAugmentedSemanticNode getASTChild(ASTNodeType type, String image) {
        for (TypeAugmentedSemanticNode typeAugmentedSemanticNode : children) {
            if(typeAugmentedSemanticNode.astNode.getType() == type &&
                    typeAugmentedSemanticNode.astNode.hasToken() &&
                    typeAugmentedSemanticNode.astNode.getToken().getImage().equals(image)) {
                return typeAugmentedSemanticNode;
            }
        }
        return null;
    }
    
    public TypeAugmentedSemanticNode getASTChild(ASTNodeType type, int count) {
        AbstractSyntaxNode child = astNode.getChild(type);
        int found = 0;
        for (TypeAugmentedSemanticNode typeAugmentedSemanticNode : children) {
            if(typeAugmentedSemanticNode.astNode == child) {
                if(++found == count)
                    return typeAugmentedSemanticNode;
            }
        }
        return null;
    }
    
    public AbstractSyntaxNode getASTNode() {
        return astNode;
    }
    
    public void setType(CXType type) {
        if(type == null) clearType();
        else {
            isTypedExpression = true;
            this.type = type;
        }
    }
    
    public void clearType() {
        isTypedExpression = false;
        type = null;
    }
    
    @Override
    public String toString() {
        if(isFailurePoint()) {
            return astNode.toString() + " -- FAILURE POINT";
        }
        String output;
        if(!isTypedExpression()) output = astNode.toString();
        else {
            if(isLValue()) output = astNode.toString() + " -> " + type.toString() + " [L]";
            else output = astNode.toString() + " -> " + type.toString() + " [R]";
        }
        if(!compilationTags.isEmpty())
            output += "  compilation tags: " + compilationTags;
        return output;
    }
    
    @Override
    public List<TypeAugmentedSemanticNode> getDirectChildren() {
        return getChildren();
    }
    
    @Override
    public ASTNodeType getType() {
        return getASTType();
    }
    
    public void addCompilationTag(ICompilationTag tag) {
        if(!tag.canAttachTo(this)) throw new IllegalArgumentException();
        compilationTags.add(tag);
    }
    
    
    public void addCompilationTags(ICompilationTag... tag) {
        for (ICompilationTag iCompilationTag : tag) {
            addCompilationTag(iCompilationTag);
        }
    }
    public boolean containsCompilationTag(ICompilationTag tag) {
        for (ICompilationTag compilationTag : compilationTags) {
            if(tag.equals(compilationTag)) return true;
        }
        return false;
    }
    
    public boolean containsCompilationTags(ICompilationTag... tag) {
        for (ICompilationTag iCompilationTag : tag) {
            if(!containsCompilationTag(iCompilationTag)) return false;
        }
        return true;
    }
    
    public boolean containsCompilationTag(Class<? extends ICompilationTag> clazz) {
        for (ICompilationTag compilationTag : compilationTags) {
            if(clazz.isInstance(compilationTag)) return true;
        }
        return false;
    }
    
    public <T extends ICompilationTag> T getCompilationTag(Class<T> clazz) {
        if(!containsCompilationTag(clazz)) return null;
        for (ICompilationTag compilationTag : compilationTags) {
            if(clazz.isInstance(compilationTag)) return (T) compilationTag;
        }
        return null;
    }
    
    @Override
    public List<TypeAugmentedSemanticNode> postfix() {
        List<TypeAugmentedSemanticNode> output = new LinkedList<>();
        for (TypeAugmentedSemanticNode child : children) {
            output.addAll(child.postfix());
        }
        output.add(this);
        return output;
    }
    
    public Token findFirstToken() {
        if(this.getASTNode().hasToken()) return this.getASTNode().getToken();
        for (TypeAugmentedSemanticNode child : getChildren()) {
            Token childFirst = child.findFirstToken();
            if(childFirst != null) return childFirst;
        }
        return null;
    }
    
    public TypeAugmentedSemanticNode getDeepestFailureNode() {
        TypeAugmentedSemanticNode output = null;
        for (TypeAugmentedSemanticNode child : children) {
            TypeAugmentedSemanticNode deepestFailureNode = child.getDeepestFailureNode();
            if(deepestFailureNode != null) {
                output = deepestFailureNode;
                break;
            }
        }
        if(output == null && this.isFailurePoint()) {
            output = this;
        }
        return output;
    }
    
    public Token findFailureToken() {
        TypeAugmentedSemanticNode deepestFailureNode = getDeepestFailureNode();
        if(deepestFailureNode == null) return null;
        return deepestFailureNode.findFirstToken();
    }
    
    public TypeAugmentedSemanticNode findFromASTNode(AbstractSyntaxNode node) {
        if(this.getASTNode().equals(node)) return this;
        for (TypeAugmentedSemanticNode child : children) {
            TypeAugmentedSemanticNode output = child.findFromASTNode(node);
            if(output != null) return output;
        }
        
        return null;
    }
}
