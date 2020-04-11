package radin.core.semantics.generics;

import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.exceptions.InvalidPrimitiveException;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.AbstractCXClassType;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.utility.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CXGenericClassFactory implements ICXGenericFactory<AbstractCXClassType> {
    
    private TypeEnvironment environment;
    private CXIdentifier identifier;
    private ArrayList<CXParameterizedType> parameterizedTypes;
    private AbstractSyntaxNode originalRelevantTree;
    private Token declarationToken;
    private HashMap<List<CXType>, Pair<AbstractSyntaxNode, CXClassType>> createdTrees = new HashMap<>();
    
    private CXClassType baseType;
    
    /*
    public CXGenericClass(CXIdentifier identifier, List<AbstractCXClassType.ClassFieldDeclaration> declarations,
                          List<CXMethod> methods, List<CXConstructor> constructors, TypeEnvironment environment,
                          ArrayList<CXParameterizedType> parameterizedTypes, AbstractSyntaxNode originalRelevantTree,
                          Token declarationToken) {
        baseType = new CXClassType(identifier, declarations,
                methods, constructors, environment);
        this.environment = environment;
        this.identifier = identifier;
        this.parameterizedTypes = parameterizedTypes;
        this.originalRelevantTree = originalRelevantTree;
        this.declarationToken = declarationToken;
    }
    
     */
    
    public CXGenericClassFactory(TypeEnvironment environment, CXIdentifier identifier, ArrayList<CXParameterizedType> parameterizedTypes, AbstractSyntaxNode originalRelevantTree, Token declarationToken) {
        this.environment = environment;
        this.identifier = identifier;
        this.parameterizedTypes = parameterizedTypes;
        this.originalRelevantTree = originalRelevantTree;
        this.declarationToken = declarationToken;
    }
    
    @Override
    public Collection<AbstractSyntaxNode> getCreatedTrees() {
        return createdTrees.values().stream().map(Pair::getVal1).collect(Collectors.toList());
    }
    
    @Override
    public void setOriginalRelevantTree(AbstractSyntaxNode originalRelevantTree) {
        this.originalRelevantTree = originalRelevantTree;
    }
    
    @Override
    public Token getDeclarationToken() {
        return declarationToken;
    }
    
    @Override
    public TypeEnvironment getEnvironment() {
        return environment;
    }
    
    @Override
    public List<CXParameterizedType> getParameterizedTypes() {
        return parameterizedTypes;
    }
    
    @Override
    public GenericInstance<AbstractCXClassType> createInstance(List<CXType> types) {
        if (!typesValid(types)) return null;
        String alteredName = identifier.getIdentifier().getImage()
                + "_"
                + types.stream().map(CXType::getSafeTypeString).collect(Collectors.joining("_"));
        if(originalRelevantTree != null) {
            if (!createdTrees.containsKey(types) || createdTrees.get(types) == null) {
                AbstractSyntaxNode modifiedTree = createModifiedTree(originalRelevantTree, types);
                AbstractSyntaxNode oldId = modifiedTree.getDirectChildren().remove(1);
                Token idToken = oldId.getToken();
                Token newIdToken = new Token(TokenType.t_id, alteredName).addColumnAndLineNumber(idToken.getVirtualColumn(),
                        idToken.getVirtualLineNumber());
                modifiedTree.getDirectChildren().add(1, new AbstractSyntaxNode(ASTNodeType.id, newIdToken));
                try {
                CXClassType type = (CXClassType) environment.getType(modifiedTree);
                createdTrees.put(types, new Pair<>(modifiedTree, type));
                } catch (InvalidPrimitiveException e) {
                    throw new Error("Primitive Error");
                }
            }
            
            new GenericInstance<>(alteredName, createdTrees.get(types));
        } else {
            createdTrees.putIfAbsent(types, null);
        }
        
        
    
        return null;
    }
    
    @Override
    public AbstractSyntaxNode getOriginalRelevantTree() {
        return originalRelevantTree;
    }
}
