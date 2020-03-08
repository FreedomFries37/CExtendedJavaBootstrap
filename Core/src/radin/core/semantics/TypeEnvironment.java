package radin.core.semantics;

import radin.core.annotations.AnnotationManager;
import radin.core.lexical.Token;
import radin.core.semantics.exceptions.*;
import radin.core.semantics.types.*;
import radin.core.semantics.types.compound.*;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.CXParameter;
import radin.core.semantics.types.primitives.*;
import radin.core.semantics.types.wrapped.*;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.Pair;


import java.util.*;

import static radin.core.lexical.TokenType.t_class;
import static radin.core.lexical.TokenType.t_id;
import static radin.core.semantics.ASTNodeType.*;

public class TypeEnvironment {
    
    private final static HashSet<String> primitives;
    
    static {
        primitives = new HashSet<>();
        primitives.addAll(Arrays.asList("char",
                "short",
                "int",
                "long",
                "unsigned"));
    }
    
    private static int environmentsCreated = 0;
    
    private HashMap<String, CXType> typeDefinitions;
    private HashSet<CXCompoundType> namedCompoundTypes;
    private HashMap<String, CXCompoundType> namedCompoundTypesMap;
    private HashSet<CXClassType> createdClasses;
    private HashMap<CXIdentifier, CXMappedType> delayedTypeDefinitionHashMap;
    private HashSet<CXCompoundTypeNameIndirection> lateBoundReferences;
    private AnnotationManager<CXClassType> classTargetManger;
    private CXClassType defaultInheritance = null;
    private CXIdentifier currentNamespace = null;
    private NamespaceTree namespaceTree = new NamespaceTree();
    private int pointerSize = 8;
    private int charSize = 1;
    private int intSize = 4;
    private int floatSize = 4;
    private int doubleSize = 4;
    private int shortIntSize = 2;
    private int longIntSize = 8;
    private int longLongSize = 8;
    private int longDoubleSize = 10;
    private boolean standardBooleanDefined;
    
    private List<CXClassType> allCreated = new LinkedList<>(); // doesn't reset;
    
    public CXClassType getDefaultInheritance() {
        return defaultInheritance;
    }
    
    public TypeEnvironment() {
        ICompilationSettings.debugLog.info("Type Environment " + environmentsCreated++ + " Created!");
        // ICompilationSettings.debugLog.throwing("TypeEnvironment", "<init>", new Throwable());
        typeDefinitions = new HashMap<>();
        namedCompoundTypes = new HashSet<>();
        namedCompoundTypesMap = new HashMap<>();
        lateBoundReferences = new HashSet<>();
        createdClasses = new HashSet<>();
        
        standardBooleanDefined = false;
        delayedTypeDefinitionHashMap = new HashMap<>();
        classTargetManger = AnnotationManager.createTargeted(
                new Pair<String, AnnotationManager.TargetCommandNoArgs<CXClassType>>("setAsDefaultInheritance", this::setDefaultInheritance)
        );
    }
    
    public void resetToNone() {
        ICompilationSettings.debugLog.finer("Type Environment Reset");
        typeDefinitions = new HashMap<>();
        namedCompoundTypes = new HashSet<>();
        namedCompoundTypesMap = new HashMap<>();
        lateBoundReferences = new HashSet<>();
        createdClasses = new HashSet<>();
        
        delayedTypeDefinitionHashMap = new HashMap<>();
        classTargetManger = AnnotationManager.createTargeted(
                new Pair<String, AnnotationManager.TargetCommandNoArgs<CXClassType>>("setAsDefaultInheritance", this::setDefaultInheritance)
        );
        namespaceTree = new NamespaceTree();
        defaultInheritance = null;
        currentNamespace = null;
        if(standardBooleanDefined) {
            addTypeDefinition(
                    UnsignedPrimitive.createUnsignedShort(), "boolean"
            );
        }
    }
    
    public void setDefaultInheritance(CXClassType defaultInheritance) {
        this.defaultInheritance = defaultInheritance;
    }
    
    public static TypeEnvironment getStandardEnvironment() {
        TypeEnvironment environment = new TypeEnvironment();
        
        
        environment.addTypeDefinition(
                UnsignedPrimitive.createUnsignedShort(), "boolean"
        );
        
        environment.standardBooleanDefined = true;
        
        
        return environment;
    }
    
    public CXType addTypeDefinition(CXType type, String name) {
        
        typeDefinitions.put(name, type);
        return type;
    }
    
    public void resetNamespace() {
        currentNamespace = null;
    }
    
    public AnnotationManager<CXClassType> getClassTargetManger() {
        return classTargetManger;
    }
    
    public int getShortIntSize() {
        return shortIntSize;
    }
    
    public void setShortIntSize(int shortIntSize) {
        this.shortIntSize = shortIntSize;
    }
    
    public int getLongIntSize() {
        return longIntSize;
    }
    
    public void setLongIntSize(int longIntSize) {
        this.longIntSize = longIntSize;
    }
    
    public int getLongLongSize() {
        return longLongSize;
    }
    
    public void setLongLongSize(int longLongSize) {
        this.longLongSize = longLongSize;
    }
    
    public int getLongDoubleSize() {
        return longDoubleSize;
    }
    
    public void setLongDoubleSize(int longDoubleSize) {
        this.longDoubleSize = longDoubleSize;
    }
    
    public void pushNamespace(Token identifier) {
        this.currentNamespace = new CXIdentifier(this.currentNamespace, identifier);
        namespaceTree.addNamespace(this.currentNamespace);
    }
    
    public CXType addTemp(Token tok) {
        String identifier = tok.getImage();
        CXIdentifier cxIdentifier = new CXIdentifier(currentNamespace, tok);
        if(delayedTypeDefinitionHashMap.containsKey(cxIdentifier)) return getTempType(cxIdentifier);
        CXDelayedTypeDefinition delayedTypeDefinition = new CXDelayedTypeDefinition(cxIdentifier, tok, this);
        delayedTypeDefinitionHashMap.put(cxIdentifier, delayedTypeDefinition);
        return getTempType(cxIdentifier);
    }
    
    public CXMappedType getTempType(CXIdentifier identifier) {
        CXIdentifier parent;
        
        if(identifier.getParentNamespace() != null)
            parent = namespaceTree.getNamespace(currentNamespace, identifier.getParentNamespace());
        else parent = currentNamespace;
        ICompilationSettings.debugLog.finest("Getting temp type " + identifier);
        CXIdentifier actual = new CXIdentifier(parent, identifier.getIdentifier());
        ICompilationSettings.debugLog.finest("Rectified to " + actual);
        
        return delayedTypeDefinitionHashMap.getOrDefault(actual, null);
    }
    
    public CXType addDeferred(Token tok) {
        String identifier = tok.getImage();
        CXIdentifier cxIdentifier = new CXIdentifier(currentNamespace, tok);
        if(delayedTypeDefinitionHashMap.containsKey(cxIdentifier)) return getTempType(cxIdentifier);
        CXDeferredClassDefinition delayedTypeDefinition = new CXDeferredClassDefinition(tok, this, cxIdentifier);
        delayedTypeDefinitionHashMap.put(cxIdentifier, delayedTypeDefinition);
        ICompilationSettings.debugLog.finest("Added deferred type " + delayedTypeDefinition);
        return getTempType(cxIdentifier);
    }
    
    public CXMappedType getTempType(String identifier) {
        ICompilationSettings.debugLog.finest("Getting temp type " + identifier);
        return getTempType(new CXIdentifier(currentNamespace, new Token(t_id, identifier)));
    }
    
    public CXMappedType getTempType(CXIdentifier namespace, Token identifier) {
        
        CXIdentifier actual = new CXIdentifier(namespace,  identifier);
        ICompilationSettings.debugLog.finest("Getting temp type " + actual);
        
        return delayedTypeDefinitionHashMap.getOrDefault(actual, null);
    }
    
    public void removeTempType(CXIdentifier identifier) {
        delayedTypeDefinitionHashMap.remove(identifier);
    }
    
    public void popNamespace() {
        if(currentNamespace != null) {
            currentNamespace = currentNamespace.getParentNamespace();
        }
    }
    
    public int getPointerSize() {
        return pointerSize;
    }
    
    public void setPointerSize(int pointerSize) {
        this.pointerSize = pointerSize;
    }
    
    public int getCharSize() {
        return charSize;
    }
    
    public void setCharSize(int charSize) {
        this.charSize = charSize;
    }
    
    public int getIntSize() {
        return intSize;
    }
    
    public void setIntSize(int intSize) {
        this.intSize = intSize;
    }
    
    public int getFloatSize() {
        return floatSize;
    }
    
    public void setFloatSize(int floatSize) {
        this.floatSize = floatSize;
    }
    
    public int getDoubleSize() {
        return doubleSize;
    }
    
    public void setDoubleSize(int doubleSize) {
        this.doubleSize = doubleSize;
    }
    
    public int getVoidSize() {
        return 0;
    }
    
    public CXType addTypeDefinition(AbstractSyntaxNode typeAST, String name) throws InvalidPrimitiveException {
        if(primitives.contains(name)) throw new PrimitiveTypeDefinitionError(name);
        if(name.equals("void")) throw new VoidTypeError();
        
        CXType type = getType(typeAST);
        if(typeDefinitions.containsKey(name) && !type.isExact(typeDefinitions.get(name), this)) throw new TypeDefinitionAlreadyExistsError(name);
        
        typeDefinitions.put(name, new CXDynamicTypeDefinition(name, type));
        return type;
    }
    
    public void removeTypeDefinition(String name) throws InvalidPrimitiveException {
        if(primitives.contains(name)) throw new PrimitiveTypeDefinitionError(name);
        if(name.equals("void")) throw new VoidTypeError();
        
        
        typeDefinitions.remove(name);
        
        //typeDefinitions.put(name, new CXDynamicTypeDefinition(name, type));
    }
    
    public CXType getTypeDefinition(String name) {
        return typeDefinitions.get(name);
    }
    
    /**
     * Gets the type given a CXIdentifier
     * @param namespacedTypename a CXIdentifier. If the parent of the CXIdentifier is null, its treated as if its a call
     *                           to {@link TypeEnvironment#getType(Token, Token)}
     * @param corresponding
     * @return the CXType
     * @throws TypeDoesNotExist
     */
    public CXType getType(CXIdentifier namespacedTypename, Token corresponding) {
        if(namespacedTypename.getParentNamespace() == null) return getType(namespacedTypename.getIdentifier(),
                corresponding);
        
        List<CXType> output = new LinkedList<>();
        
        for (CXIdentifier namespace : namespaceTree.getNamespaces(currentNamespace, namespacedTypename.getParentNamespace())) {
            for (CXCompoundType cxCompoundType : namespaceTree.getTypesForNamespace(namespace)) {
                if(cxCompoundType.getTypeNameIdentifier().getIdentifierString().equals(namespacedTypename.getIdentifierString())) {
                    output.add(cxCompoundType);
                }
            }
        }
        
        /*
        CXIdentifier certainNamespace = namespaceTree.getNamespace(currentNamespace, namespacedTypename.getParentNamespace());
        for (CXCompoundType cxCompoundType : namespaceTree.getTypesForNamespace(certainNamespace)) {
            if(cxCompoundType.getTypeNameIdentifier().getIdentifier().equals(namespacedTypename.getIdentifier())) {
                return cxCompoundType;
            }
        }
  
         */
        
        if(output.size() > 1) throw new AmbiguousIdentifierError(corresponding, output);
        else if(output.size() == 1) return new PointerType(output.get(0));
        
        throw new TypeDoesNotExist(namespacedTypename.toString());
    }
    
    public CXType getType(Token typenameImage, Token tok) {
        ICompilationSettings.debugLog.entering("TypeEnvironment", String.format("getType(%s, %s)", typenameImage, tok));
        CXType output = null;
        if(typeDefinitions.containsKey(typenameImage.getImage())) {
            output = typeDefinitions.get(typenameImage.getImage());
        }
        CXType temp;
        if((temp = getTempType(currentNamespace, typenameImage)) != null) {
            if(output != null) throw new AmbiguousIdentifierError(tok, Arrays.asList(temp, output));
            output = temp;
        }
        List<CXCompoundType> typesForNamespace = namespaceTree.getTypesForNamespace(currentNamespace);
        if(typesForNamespace == null) {
            throw new TypeDoesNotExist(typenameImage.getImage());
        }
        List<CXType> possibilities = new LinkedList<>();
        for (CXCompoundType cxCompoundType : typesForNamespace) {
            
            if(cxCompoundType.getTypeNameIdentifier().getIdentifier().equals(typenameImage))
                possibilities.add(cxCompoundType);
        }
        
        
        if(output != null && possibilities.size() > 0) {
            if(output instanceof CXDelayedTypeDefinition && possibilities.size() == 1 && possibilities.get(0) instanceof CXClassType) {
                if(((CXDelayedTypeDefinition) output).getIdentifier().equals(((CXClassType) possibilities.get(0)).getTypeNameIdentifier())) {
                    return new PointerType(possibilities.get(0));
                }
            }
            
            possibilities.add(output);
            throw new AmbiguousIdentifierError(tok, possibilities);
        } else if(possibilities.size() > 1) {
            throw new AmbiguousIdentifierError(tok, possibilities);
        } else if(possibilities.size() == 1) {
            output = new PointerType(possibilities.get(0));
        }
        
        
        if(output == null)
            throw new TypeDoesNotExist(typenameImage.getImage());
        
        if(output instanceof ICXClassType) {
            output = output.toPointer();
        }
        return output;
    }
    
    public CXType getType(AbstractSyntaxNode ast) throws InvalidPrimitiveException {
        // System.out.println("Getting type for:");
        
        
        // ast.printTreeForm();
        if(ast instanceof TypedAbstractSyntaxNode) {
            return ((TypedAbstractSyntaxNode) ast).getCxType();
        }
        
        if(ast.getTreeType().equals(ASTNodeType.namespaced)) {
            AbstractSyntaxNode node = ast;
            CXIdentifier namespace = null;
            while (node.getTreeType() == ASTNodeType.namespaced) {
                namespace = new CXIdentifier(namespace, node.getChild(0).getToken());
                node = node.getChild(1);
            }
            CXType output;
            Token image = node.getToken();
            if((output = getTempType(namespace, image)) != null) {
                return output;
            }
            /*
            CXIdentifier certainNamespace = namespaceTree.getNamespace(currentNamespace, namespace);
    
            
            for (CXCompoundType cxCompoundType : namespaceTree.getTypesForNamespace(certainNamespace)) {
                if(cxCompoundType.getTypeNameIdentifier().getIdentifier().equals(image)) {
                    return cxCompoundType;
                }
            }
            
             */
            CXIdentifier objectIdentifier = new CXIdentifier(namespace, image);
            return getType(objectIdentifier, node.getToken());
            
            //throw new TypeDoesNotExist(new CXIdentifier(namespace, image).toString());
        }
        
        if(ast.getTreeType().equals(ASTNodeType.typename)) {
            String image = ast.getToken().getImage();
            return getType(ast.getToken(), ast.getToken());
        }
        
        if(ast.getTreeType().equals(ASTNodeType.pointer_type)) {
            return new PointerType(getType(ast.getChild(0)));
        }
        
        if(ast.getTreeType().equals(ASTNodeType.qualifiers_and_specifiers)) {
            if(ast.hasChild(ASTNodeType.namespaced)) {
                return getType(ast.getChild(ASTNodeType.namespaced));
            }
            
            List<AbstractSyntaxNode> specifiers = ast.getChildren(ASTNodeType.specifier);
            specifiers.sort(new SpecifierComparator());
            CXType type = null;
            
            if(specifiers.get(0).getToken() != null  ) {
                boolean isCompoundReference = false;
                CXCompoundTypeNameIndirection.CompoundType ctype = null;
                switch (specifiers.get(0).getToken().getType()) {
                    case t_struct: {
                        ctype = CXCompoundTypeNameIndirection.CompoundType.struct;
                        break;
                    }
                    case t_union: {
                        ctype = CXCompoundTypeNameIndirection.CompoundType.union;
                        break;
                    }
                    case t_class: {
                        return addDeferred(specifiers.get(1).getToken());
                    }
                    default:
                        break;
                }
                if(isCompoundReference) {
                
                }
                
            }
            for (AbstractSyntaxNode specifier : specifiers) {
                if(type == null) {
                    type = getType(specifier);
                }else if(isModifier(getSpecifier(specifier))) {
                    switch (getSpecifier(specifier)) {
                        case "unsigned": {
                            assert type instanceof AbstractCXPrimitiveType;
                            type = new UnsignedPrimitive((AbstractCXPrimitiveType) type);
                            break;
                        }
                        case "long": {
                            assert type instanceof AbstractCXPrimitiveType;
                            type = LongPrimitive.create(((AbstractCXPrimitiveType) type));
                            break;
                        }
                        default:
                            throw new UnsupportedOperationException();
                    }
                } else {
                    throw new PrimitiveTypeDefinitionError(specifier.getToken().getImage());
                }
            }
            for (AbstractSyntaxNode qualifier : ast.getChildren(ASTNodeType.qualifier)) {
                switch (qualifier.getToken().toString()) {
                    case "@const": {
                        type = new ConstantType(type);
                        break;
                    }
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            return type;
        }
        
        
        if(ast.getTreeType().equals(ASTNodeType.specifier)) {
            
            if(ast.hasChild(ASTNodeType.basic_compound_type_dec)) {
                return createType(ast.getChild(ASTNodeType.basic_compound_type_dec), null);
            } else if(ast.hasChild(ASTNodeType.compound_type_reference)) {
                AbstractSyntaxNode name = ast.getChild(ASTNodeType.compound_type_reference).getChild(ASTNodeType.id);
                Token image = name.getToken();
                if(namedCompoundTypeExists(image.getImage())) {
                    return getNamedCompoundType(image.getImage());
                } else {
                    CXCompoundTypeNameIndirection.CompoundType type;
                    boolean addTypeDef = false;
                    switch (ast.getChild(ASTNodeType.compound_type_reference).getChild(0).getTreeType()) {
                        case struct: {
                            type = CXCompoundTypeNameIndirection.CompoundType.struct;
                            break;
                        }
                        case union: {
                            type = CXCompoundTypeNameIndirection.CompoundType.union;
                            break;
                        }
                        case _class: {
                            return addDeferred(name.getToken());
                        }
                        default:
                            throw new UnsupportedOperationException();
                    }
                    CXCompoundTypeNameIndirection CXCompoundTypeNameIndirection = new CXCompoundTypeNameIndirection(type, image);
                    lateBoundReferences.add(CXCompoundTypeNameIndirection);
                    if(addTypeDef) {
                        addTypeDefinition(CXCompoundTypeNameIndirection, image.getImage());
                    }
                    return CXCompoundTypeNameIndirection;
                }
            } else switch (getSpecifier(ast)) {
                case "unsigned": {
                    return new UnsignedPrimitive();
                }
                case "long": {
                    return new LongPrimitive();
                }
                default: {
                    return CXPrimitiveType.get(getSpecifier(ast));
                }
                
            }
            
            
        }
        
        if(ast.getTreeType().equals(ASTNodeType.class_type_definition)) {
            return createType(ast, currentNamespace);
        }
        
        if(ast.getTreeType() == ASTNodeType.array_type) {
            return new ArrayType(((TypedAbstractSyntaxNode) ast).getCxType());
        }
        
        if(ast.getTreeType() == ASTNodeType.abstract_declarator) {
            return getType(ast.getChild(0));
        }
        
        throw new UnsupportedOperationException(ast.getTreeType().toString());
    }
    
    private String getSpecifier(AbstractSyntaxNode node) {
        String o1Specifier = node.getToken().getImage();
        if(o1Specifier == null) o1Specifier = node.getToken().getType().toString();
        return o1Specifier;
    }
    
    private CXCompoundType createType(AbstractSyntaxNode ast, CXIdentifier namespace) {
        ICompilationSettings.debugLog.finest("in " + namespace + " creating compound type from ");
        // ICompilationSettings.debugLog.finest("\n" + ast.toTreeForm());
        AbstractSyntaxNode nameAST = ast.getChild(ASTNodeType.id);
        Token name = nameAST != null? nameAST.getToken() : null;
        boolean isAnonymous = name == null;
        CXCompoundType output;
        if(ast.getTreeType().equals(ASTNodeType.basic_compound_type_dec)) {
            boolean isUnion = ast.hasChild(union);
            AbstractSyntaxNode fields = ast.getChild(ASTNodeType.basic_compound_type_fields);
            List<CXCompoundType.FieldDeclaration> fieldDeclarations = createFieldDeclarations(fields);
            
            CXCompoundType type;
            if(isUnion) {
                if(isAnonymous)
                    type = new CXUnionType(fieldDeclarations);
                else
                    type = new CXUnionType(name, fieldDeclarations);
            } else {
                if(isAnonymous)
                    type = new CXStructType(fieldDeclarations);
                else
                    type = new CXStructType(name, fieldDeclarations);
            }
            
            if(!isAnonymous) {
                addNamedCompoundType(type);
            }
            
            output = type;
        } else {
            ICompilationSettings.debugLog.finest("Is a class definition");
            
            CXIdentifier identifier = new CXIdentifier(namespace, name);
            List<CXMethod> methods = new LinkedList<>();
            List<CXConstructor> constructors = new LinkedList<>();
            List<CXClassType.ClassFieldDeclaration> fieldDeclarations = new LinkedList<>();
            
            List<AbstractSyntaxNode> constructorDefinitions = new LinkedList<>();
            List<Visibility> constructorVisibilities = new LinkedList<>();
            
            for (AbstractSyntaxNode abstractSyntaxNode : ast.getChild(ASTNodeType.class_level_decs)) {
                Visibility visibility = getVisibility(abstractSyntaxNode.getChild(ASTNodeType.visibility));
                
                AbstractSyntaxNode dec = abstractSyntaxNode.getChild(1);
                switch (dec.getTreeType()) {
                    case declarations: {
                        if(dec.getTreeType() != ASTNodeType.function_description) {
                            fieldDeclarations.addAll(
                                    createClassFieldDeclarations(visibility, dec)
                            );
                        }
                        break;
                    }
                    case function_description:
                    case function_definition: {
                        boolean isVirtual = dec.hasChild(ASTNodeType._virtual);
                        ICompilationSettings.debugLog.finest("Creating method in " + identifier + " with tree:\n" + dec.toTreeForm() + "...");
                        
                        methods.add(
                                createMethod(visibility, isVirtual, dec)
                        );
                        ICompilationSettings.debugLog.finest("Done.");
                        break;
                    }
                    case constructor_definition: {
                        constructorDefinitions.add(dec);
                        constructorVisibilities.add(visibility);
                        break;
                    }
                    default:
                        throw new UnsupportedOperationException(dec.getTreeType().toString());
                }
                
            }
            
            
            
            CXClassType cxClassType;
            if(ast.hasChild(ASTNodeType.inherit)) {
                ICompilationSettings.debugLog.finest("Determining parent type...");
                
                CXClassType parent;
                try {
                    parent = (CXClassType) ((PointerType) getType(ast.getChild(ASTNodeType.inherit).getChild(0))).getSubType();
                } catch (InvalidPrimitiveException e) {
                    return null;
                }
                ICompilationSettings.debugLog.finest("Parent type found: " + parent);
                
                cxClassType = new CXClassType(identifier, parent, fieldDeclarations, methods, new LinkedList<>(), this);
                
                
            }
            else {
                cxClassType = new CXClassType(identifier, defaultInheritance, fieldDeclarations, methods,
                        new LinkedList<>(), this);
            }
            
            Iterator<Visibility> visibilityIterator = constructorVisibilities.iterator();
            for (AbstractSyntaxNode dec: constructorDefinitions) {
                AbstractSyntaxNode params = dec.getChild(ASTNodeType.parameter_list);
                AbstractSyntaxNode compound = dec.getChild(ASTNodeType.compound_statement);
                if(dec.hasChild(ASTNodeType.sequence)) {
                    
                    AbstractSyntaxNode priorAST = dec.getChild(2);
                    
                    
                    constructors.add(
                            createConstructor(visibilityIterator.next(), cxClassType, params, compound, dec)
                    );
                } else {
                    constructors.add(
                            createConstructor(visibilityIterator.next(), cxClassType, params, compound, dec)
                    );
                }
            }
            
            for (CXConstructor constructor : constructors) {
                constructor.setParent(cxClassType);
            }
            cxClassType.addConstructors(constructors);
            createdClasses.add(cxClassType);
            cxClassType.setEnvironment(this);
            
            ICompilationSettings.debugLog.info("Created new class " + cxClassType.getTypeNameIdentifier()
                    + (cxClassType.getParent() != null ? " : " + cxClassType.getParent().getTypeNameIdentifier() : ""));
            
            
            List<CXCompoundType> typesForNamespace = namespaceTree.getTypesForNamespace(namespace);
            typesForNamespace.add(cxClassType);
            
            addNamedCompoundType(cxClassType);
            allCreated.add(cxClassType);
            return cxClassType;
            
        }
        
        lateBoundReferences.removeIf(
                compoundTypeReference ->
                        compoundTypeReference.is(output, this)
        
        );
        
        return output;
    }
    
    public NamespaceTree getNamespaceTree() {
        return namespaceTree;
    }
    
    
    
    private Visibility getVisibility(AbstractSyntaxNode ast) {
        if(ast.getTreeType() != ASTNodeType.visibility) return null;
        switch (ast.getToken().getType()) {
            case t_public: return Visibility._public;
            case t_private: return Visibility._private;
            case t_internal: return Visibility.internal;
            default: return null;
        }
    }
    
    public boolean noTypeErrors() {
        return lateBoundReferences.isEmpty();
    }
    
    /**
     * Creates all of the field declarations
     * @param ast must be type {@link ASTNodeType#basic_compound_type_fields}
     * @return a list of field declarations
     */
    private List<CXCompoundType.FieldDeclaration> createFieldDeclarations(AbstractSyntaxNode ast) {
        List<CXCompoundType.FieldDeclaration> output = new LinkedList<>();
        for (AbstractSyntaxNode abstractSyntaxNode : ast.getChildList()) {
            output.add(
                    createFieldDeclaration(abstractSyntaxNode)
            );
        }
        return output;
    }
    
    public List<CXClassType> getAllCreated() {
        return allCreated;
    }
    
    public int getTypeId(CXClassType type) {
        return allCreated.indexOf(type);
    }
    
    /**
     * Creates a field declaration
     * @param ast must be type {@link ASTNodeType#basic_compound_type_field}
     * @return a field declaration object
     */
    private CXCompoundType.FieldDeclaration createFieldDeclaration(AbstractSyntaxNode ast) {
        CXType type;
        if(ast instanceof TypedAbstractSyntaxNode) {
            type = ((TypedAbstractSyntaxNode) ast).getCxType();
        } else {
            throw new UnsupportedOperationException();
        }
        
        AbstractSyntaxNode idAST = ast.getChild(ASTNodeType.id);
        String name = idAST.getToken().getImage();
        return new CXCompoundType.FieldDeclaration(type, name);
    }
    
    private List<CXClassType.ClassFieldDeclaration> createClassFieldDeclarations(Visibility visibility,
                                                                                 AbstractSyntaxNode ast) {
        
        List<CXClassType.ClassFieldDeclaration> output = new LinkedList<>();
        for (AbstractSyntaxNode abstractSyntaxNode : ast.getChildList()) {
            output.add(
                    createClassFieldDeclaration(visibility, abstractSyntaxNode)
            );
        }
        return output;
        
    }
    
    private CXClassType.ClassFieldDeclaration createClassFieldDeclaration(Visibility visibility,
                                                                          AbstractSyntaxNode ast) {
        CXType type;
        if(ast instanceof TypedAbstractSyntaxNode) {
            type = ((TypedAbstractSyntaxNode) ast).getCxType();
        } else {
            throw new UnsupportedOperationException();
        }
        
        AbstractSyntaxNode idAST = ast.getChild(ASTNodeType.id);
        String name = idAST.getToken().getImage();
        return new CXClassType.ClassFieldDeclaration(type, name, visibility);
    }
    
    private CXMethod createMethod(Visibility visibility, boolean isVirtual, AbstractSyntaxNode ast) {
        if(!(ast.getTreeType().equals(ASTNodeType.function_definition) || ast.getTreeType() == ASTNodeType.function_description)) {
            throw new UnsupportedOperationException();
        }
        
        assert ast instanceof TypedAbstractSyntaxNode;
        TypedAbstractSyntaxNode typedAST = (TypedAbstractSyntaxNode) ast;
        
        CXType returnType = typedAST.getCxType();
        Token name = ast.getChild(ASTNodeType.id).getToken();
        AbstractSyntaxNode after = ast.getChild(ASTNodeType.compound_statement);
        
        List<CXParameter> parameters = createParameters(ast.getChild(ASTNodeType.parameter_list));
        
        return new CXMethod(null, visibility, name, isVirtual, returnType, parameters, after);
    }
    
    private CXConstructor createConstructor(Visibility visibility, CXClassType parent, AbstractSyntaxNode params,
                                            AbstractSyntaxNode compound, AbstractSyntaxNode corresponding) {
        List<CXParameter> parameters = createParameters(params);
        
        return new CXConstructor(parent, visibility, parameters, compound, corresponding);
    }
    
    private List<CXParameter> createParameters(AbstractSyntaxNode ast) {
        List<CXParameter> output = new LinkedList<>();
        for (AbstractSyntaxNode abstractSyntaxNode : ast.getChildList()) {
            CXType type = ((TypedAbstractSyntaxNode) abstractSyntaxNode).getCxType();
            String name = abstractSyntaxNode.getChild(ASTNodeType.id).getToken().getImage();
            output.add(new CXParameter(type, name));
        }
        return output;
    }
    
    /**
     * Checks if two types are equivalent, with const stripping for going from non-const to const
     * checks if type1 <= type2
     * @param o1 type1
     * @param o2 type2
     * @return whether they can be used
     */
    public boolean is(CXType o1, CXType o2) {
        /*
        if(!(o1 instanceof ConstantType) && o2 instanceof ConstantType) {
            return is(o1, ((ConstantType) o2).getSubtype());
        }
        if(o2 instanceof CXDynamicTypeDefinition) {
            return is(o1, ((CXDynamicTypeDefinition) o2).getOriginal());
        }
  
         */
        if(!(o1 instanceof ICXWrapper) && o2 instanceof ICXWrapper) {
            return is(o1, ((ICXWrapper) o2).getWrappedType());
        } else if(o1 instanceof ICXWrapper && o2 instanceof ICXWrapper) {
            return is(((ICXWrapper) o1).getWrappedType(), o2);
        }
        
        return o1.is(o2,this);
    }
    
    /**
     * Checks if two types are equivalent, with const stripping for going from non-const to const
     * checks if type1 <= type2
     * Strict primitive type checking
     * @param o1 type1
     * @param o2 type2
     * @return whether they can be used
     */
    public boolean isStrict(CXType o1, CXType o2) {
        if(o1 == null || o2 == null) {
            throw new TypeDoesNotExist("Null");
        }
        if(!(o1 instanceof ConstantType) && o2 instanceof ConstantType) {
            return isStrict(o1, ((ConstantType) o2).getSubtype());
        }
        if(!(o1 instanceof ICXWrapper) && o2 instanceof ICXWrapper) {
            CXType wrappedType = ((ICXWrapper) o2).getWrappedType();
            if(wrappedType == null) {
                throw new TypeDoesNotExist(o1.toString());
            }
            return isStrict(o1, wrappedType);
        } else if(o1 instanceof ICXWrapper && o2 instanceof ICXWrapper) {
            CXType wrappedType = ((ICXWrapper) o1).getWrappedType();
            if(wrappedType == null) {
                throw new TypeDoesNotExist(o1.toString());
            }
            return isStrict(wrappedType, o2);
        }
        return o1.is(o2,this, true);
    }
    
    
    public HashSet<CXClassType> getCreatedClasses() {
        return createdClasses;
    }
    
    private boolean isPrimitive(String name) {
        return name.equals("char") || name.equals("int") || name.equals("float") || name.equals("double") || name.equals("void");
    }
    
    private boolean isModifier(String name) {
        return name.equals("unsigned") || name.equals("long");
    }
    
    
    
    public boolean typedefExists(String name) {
        return typeDefinitions.containsKey(name);
    }
    
    public void addNamedCompoundType(CXCompoundType type) {
        if(type.isAnonymous()) return;
        if(namedCompoundTypes.contains(type)) throw new TypeDefinitionAlreadyExistsError(type.getTypeName());
        namedCompoundTypes.add(type);
        namedCompoundTypesMap.put(type.getTypeName(), type);
    }
    
    public boolean namedCompoundTypeExists(String name) {
        return namedCompoundTypesMap.containsKey(name);
    }
    
    public CXCompoundType getNamedCompoundType(String name) {
        return namedCompoundTypesMap.getOrDefault(name, null);
    }
    
    public boolean isStandardBooleanDefined() {
        return standardBooleanDefined;
    }
    
    private class SpecifierComparator implements Comparator<AbstractSyntaxNode> {
        
        @Override
        public int compare(AbstractSyntaxNode o1, AbstractSyntaxNode o2) {
            String o1Specifier = o1.getToken().getImage(), o2Specifier = o2.getToken().getImage();
            if(o1Specifier == null) o1Specifier = o1.getToken().getType().toString();
            if(o2Specifier == null) o2Specifier = o2.getToken().getType().toString();
            return value(o1Specifier) - value(o2Specifier);
        }
        
        private int value(String name) {
            if(isPrimitive(name)) return 1;
            if(isModifier(name)) return modifierValue(name);
            return 0;
        }
        private int modifierValue(String name) {
            switch (name) {
                case "long": return 2;
                case "unsigned": return 3;
                default: return 4;
            }
        }
        
    }
}
