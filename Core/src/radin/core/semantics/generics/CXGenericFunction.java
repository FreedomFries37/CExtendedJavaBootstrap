package radin.core.semantics.generics;

import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.CXFunctionPointer;

import java.util.*;
import java.util.stream.Collectors;

public class CXGenericFunction extends CXFunctionPointer implements ICXGenericFactory<CXFunctionPointer> {
    
    private TypeEnvironment environment;
    private CXIdentifier name;
    private ArrayList<CXParameterizedType> parameterizedTypes;
    private AbstractSyntaxNode originalRelevantTree;
    private Token declarationToken;
    
    private HashMap<List<CXType>, AbstractSyntaxNode> createdTrees = new HashMap<>();
    
    public CXGenericFunction(CXIdentifier name, CXType returnType, List<CXType> parameterTypes, TypeEnvironment environment, List<CXParameterizedType> parameterizedTypes, AbstractSyntaxNode originalRelevantTree, Token declarationToken) {
        super(returnType, parameterTypes);
        this.name = name;
        this.environment = environment;
        this.parameterizedTypes = new ArrayList<>(parameterizedTypes);
        this.originalRelevantTree = originalRelevantTree;
        this.declarationToken = declarationToken;
    }
    
    public CXGenericFunction(CXType returnType, TypeEnvironment environment, CXIdentifier name, List<CXParameterizedType> parameterizedTypes, AbstractSyntaxNode originalRelevantTree, Token declarationToken) {
        super(returnType);
        this.environment = environment;
        this.name = name;
        this.parameterizedTypes = new ArrayList<>(parameterizedTypes);
        this.originalRelevantTree = originalRelevantTree;
        this.declarationToken = declarationToken;
    }
    
    public CXGenericFunction(CXType returnType, List<CXType> parameterTypes, TypeEnvironment environment, CXIdentifier name, List<CXParameterizedType> parameterizedTypes, Token declarationToken) {
        super(returnType, parameterTypes);
        this.environment = environment;
        this.name = name;
        this.parameterizedTypes = new ArrayList<>(parameterizedTypes);
        this.declarationToken = declarationToken;
    }
    
    public CXGenericFunction(CXType returnType, TypeEnvironment environment, CXIdentifier name, List<CXParameterizedType> parameterizedTypes, Token declarationToken) {
        super(returnType);
        this.environment = environment;
        this.name = name;
        this.parameterizedTypes =new ArrayList<>(parameterizedTypes);
        this.declarationToken = declarationToken;
    }
    
    @Override
    public void setOriginalRelevantTree(AbstractSyntaxNode originalRelevantTree) {
        this.originalRelevantTree = originalRelevantTree;
    }
    
    @Override
    public Collection<AbstractSyntaxNode> getCreatedTrees() {
        if (originalRelevantTree == null) throw new NoDefinitionForGenericDeclarationError(getDeclarationToken());
        
        for (List<CXType> key : createdTrees.keySet()) {
            if (createdTrees.get(key) == null && createdTrees.containsKey(key)) {
                createdTrees.put(key, createModifiedTree(originalRelevantTree, key));
            }
        }
        
        return createdTrees.values();
    }
    
    @Override
    public AbstractSyntaxNode getOriginalRelevantTree() {
        return originalRelevantTree;
    }
    
    @Override
    public TypeEnvironment getEnvironment() {
        return environment;
    }
    
    @Override
    public ArrayList<CXParameterizedType> getParameterizedTypes() {
        return parameterizedTypes;
    }
    
    @Override
    public Token getDeclarationToken() {
        return declarationToken;
    }
    
    @Override
    public GenericInstance<CXFunctionPointer> createInstance(List<CXType> types) {
        if (!typesValid(types)) return null;
        String alteredName = name.generateCDeclaration()
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
                createdTrees.put(types, modifiedTree);
            }
        } else {
            createdTrees.putIfAbsent(types, null);
        }
        
        CXType returnType = getFixedCXType(getReturnType(), types);
        List<CXType> parameterTypes = new ArrayList<>(getParameterTypes().size());
        
        for (CXType parameterType : getParameterTypes()) {
            parameterTypes.add(getFixedCXType(parameterType, types));
        }
        
        return new GenericInstance<>(alteredName, new CXFunctionPointer(returnType, parameterTypes));
    }
}

