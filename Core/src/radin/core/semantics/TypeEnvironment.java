package radin.core.semantics;

import radin.core.semantics.exceptions.*;
import radin.core.semantics.types.*;
import radin.core.semantics.types.compound.*;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.CXParameter;
import radin.core.semantics.types.primitives.AbstractCXPrimitiveType;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.core.semantics.types.primitives.LongPrimitive;
import radin.core.semantics.types.primitives.UnsignedPrimitive;
import radin.core.semantics.types.wrapped.CXDelayedTypeDefinition;
import radin.core.semantics.types.wrapped.CXDynamicTypeDefinition;
import radin.core.semantics.types.wrapped.ConstantType;
import radin.core.semantics.types.wrapped.PointerType;

import java.util.*;

public class TypeEnvironment {
    
    private HashMap<String, CXType> typeDefinitions;
    private HashSet<CXCompoundType> namedCompoundTypes;
    private HashMap<String, CXCompoundType> namedCompoundTypesMap;
    
    private HashSet<CXClassType> createdClasses;
    private HashMap<CXIdentifier, CXDelayedTypeDefinition> delayedTypeDefinitionHashMap;
    
    private HashSet<CXCompoundTypeNameIndirection> lateBoundReferences;
    
    
    private CXIdentifier currentNamespace = null;
    private NamespaceTree namespaceTree = new NamespaceTree();
    
    private final static HashSet<String> primitives;
    private int pointerSize = 8;
    
    private int charSize = 1;
    private int intSize = 4;
    private int floatSize = 4;
    private int doubleSize = 4;
    private int shortIntSize = 2;
    
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
    
    private int longIntSize = 8;
    private int longLongSize = 8;
    private int longDoubleSize = 10;
    
    private boolean standardBooleanDefined;
    
    static {
        primitives = new HashSet<>();
        primitives.addAll(Arrays.asList("char",
                "short",
                "int",
                "long",
                "unsigned"));
    }
    
    public TypeEnvironment() {
        typeDefinitions = new HashMap<>();
        namedCompoundTypes = new HashSet<>();
        namedCompoundTypesMap = new HashMap<>();
        lateBoundReferences = new HashSet<>();
        createdClasses = new HashSet<>();
        
        standardBooleanDefined = false;
        delayedTypeDefinitionHashMap = new HashMap<>();
    }
    
    public void pushNamespace(String identifier) {
        this.currentNamespace = new CXIdentifier(this.currentNamespace, identifier);
        namespaceTree.addNamespace(this.currentNamespace);
    }
    
    public void addTemp(String identifier) {
        
        CXIdentifier cxIdentifier = new CXIdentifier(currentNamespace, identifier);
        if(delayedTypeDefinitionHashMap.containsKey(cxIdentifier)) return;
        CXDelayedTypeDefinition delayedTypeDefinition = new CXDelayedTypeDefinition(cxIdentifier, this);
        delayedTypeDefinitionHashMap.put(cxIdentifier, delayedTypeDefinition);
    }
    
    public CXDelayedTypeDefinition getTempType(String identifier) {
        return getTempType(new CXIdentifier(currentNamespace, identifier));
    }
    
    public CXDelayedTypeDefinition getTempType(CXIdentifier identifier) {
        CXIdentifier parent;
        
        if(identifier.getParentNamespace() != null)
            parent = namespaceTree.getNamespace(currentNamespace, identifier.getParentNamespace());
        else parent = currentNamespace;
        CXIdentifier actual = new CXIdentifier(parent, identifier.getIdentifier());
        return delayedTypeDefinitionHashMap.getOrDefault(actual, null);
    }
    
    public CXDelayedTypeDefinition getTempType(CXIdentifier namespace, String identifier) {
        
        CXIdentifier actual = new CXIdentifier(namespace, identifier);
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
    
    public CXType addTypeDefinition(AbstractSyntaxNode typeAST, String name) throws InvalidPrimitiveException{
        if(primitives.contains(name)) throw new PrimitiveTypeDefinitionError(name);
        if(name.equals("void")) throw new VoidTypeError();
        if(typeDefinitions.containsKey(name)) throw new TypeDefinitionAlreadyExistsError(name);
        
        CXType type = getType(typeAST);
        typeDefinitions.put(name, new CXDynamicTypeDefinition(name, type));
        return type;
    }
    
    public CXType getTypeDefinition(String name) {
        return typeDefinitions.get(name);
    }
    
    public CXType addTypeDefinition(CXType type, String name) {
        
        typeDefinitions.put(name, type);
        return type;
    }
    
    public CXType getType(CXIdentifier namespacedTypename) {
        if(namespacedTypename.getParentNamespace() == null) return getType(namespacedTypename.getIdentifier());
        CXIdentifier certainNamespace = namespaceTree.getNamespace(currentNamespace, namespacedTypename.getParentNamespace());
        for (CXCompoundType cxCompoundType : namespaceTree.getTypesForNamespace(certainNamespace)) {
            if(cxCompoundType.getTypeNameIdentifier().getIdentifier().equals(namespacedTypename.getIdentifier())) {
                return cxCompoundType;
            }
        }
        
        throw new TypeDoesNotExist(new CXIdentifier(certainNamespace, namespacedTypename.getIdentifier()).toString());
    }
    
    public CXType getType(String typenameImage) {
        if(typeDefinitions.containsKey(typenameImage)) {
            return typeDefinitions.get(typenameImage);
        }
        CXType output;
        if((output = getTempType(currentNamespace, typenameImage)) != null) {
            return output;
        }
        List<CXCompoundType> typesForNamespace = namespaceTree.getTypesForNamespace(currentNamespace);
        if(typesForNamespace == null) {
            throw new TypeDoesNotExist(typenameImage);
        }
        for (CXCompoundType cxCompoundType : typesForNamespace) {
            if(cxCompoundType.getTypeNameIdentifier().getIdentifier().equals(typenameImage))
                return cxCompoundType;
        }
        if(typedefExists(typenameImage)) {
            return typeDefinitions.get(typenameImage);
        }
        throw new TypeDoesNotExist(typenameImage);
    }
    
    public CXType getType(AbstractSyntaxNode ast) throws InvalidPrimitiveException {
        // System.out.println("Getting type for:");
        
        
        // ast.printTreeForm();
        if(ast instanceof TypeAbstractSyntaxNode) {
            return ((TypeAbstractSyntaxNode) ast).getCxType();
        }
        
        if(ast.getType().equals(ASTNodeType.namespaced)) {
            AbstractSyntaxNode node = ast;
            CXIdentifier namespace = null;
            while (node.getType() == ASTNodeType.namespaced) {
                namespace = new CXIdentifier(namespace, node.getChild(0).getToken().getImage());
                node = node.getChild(1);
            }
            CXType output;
            String image = node.getToken().getImage();
            if((output = getTempType(namespace, image)) != null) {
                return output;
            }
            CXIdentifier certainNamespace = namespaceTree.getNamespace(currentNamespace, namespace);
    
            
            for (CXCompoundType cxCompoundType : namespaceTree.getTypesForNamespace(certainNamespace)) {
                if(cxCompoundType.getTypeNameIdentifier().getIdentifier().equals(image)) {
                    return cxCompoundType;
                }
            }
            
            throw new TypeDoesNotExist(new CXIdentifier(namespace, image).toString());
        }
        
        if(ast.getType().equals(ASTNodeType.typename)) {
            String image = ast.getToken().getImage();
            return getType(image);
        }
        
        if(ast.getType().equals(ASTNodeType.pointer_type)) {
            return new PointerType(getType(ast.getChild(0)));
        }
        
        if(ast.getType().equals(ASTNodeType.qualifiers_and_specifiers)) {
            if(ast.hasChild(ASTNodeType.namespaced)) {
                return getType(ast.getChild(ASTNodeType.namespaced));
            }
            
            List<AbstractSyntaxNode> specifiers = ast.getChildren(ASTNodeType.specifier);
            specifiers.sort(new SpecifierComparator());
            CXType type = null;
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
                    case "const": {
                        type = new ConstantType(type);
                        break;
                    }
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            return type;
        }
        
        
        if(ast.getType().equals(ASTNodeType.specifier)) {
            
            if(ast.hasChild(ASTNodeType.basic_compound_type_dec)) {
                return createType(ast.getChild(ASTNodeType.basic_compound_type_dec), null);
            } else if(ast.hasChild(ASTNodeType.compound_type_reference)) {
                AbstractSyntaxNode name = ast.getChild(ASTNodeType.compound_type_reference).getChild(ASTNodeType.id);
                String image = name.getToken().getImage();
                if(namedCompoundTypeExists(image)) {
                    return getNamedCompoundType(image);
                } else {
                    CXCompoundTypeNameIndirection.CompoundType type;
                    boolean addTypeDef = false;
                    switch (ast.getChild(ASTNodeType.compound_type_reference).getChild(0).getType()) {
                        case struct: {
                            type = CXCompoundTypeNameIndirection.CompoundType.struct;
                            break;
                        }
                        case union: {
                            type = CXCompoundTypeNameIndirection.CompoundType.union;
                            break;
                        }
                        case _class: {
                            type = CXCompoundTypeNameIndirection.CompoundType._class;
                            addTypeDef = true;
                            break;
                        }
                        default:
                            throw new UnsupportedOperationException();
                    }
                    CXCompoundTypeNameIndirection CXCompoundTypeNameIndirection = new CXCompoundTypeNameIndirection(type, image);
                    lateBoundReferences.add(CXCompoundTypeNameIndirection);
                    if(addTypeDef) {
                        addTypeDefinition(CXCompoundTypeNameIndirection, image);
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
        
        if(ast.getType().equals(ASTNodeType.class_type_definition)) {
            return createType(ast, currentNamespace);
        }
        
        throw new UnsupportedOperationException(ast.getType().toString());
    }
    
    
    
    
    private String getSpecifier(AbstractSyntaxNode node) {
        String o1Specifier = node.getToken().getImage();
        if(o1Specifier == null) o1Specifier = node.getToken().getType().toString();
        return o1Specifier;
    }
    
    private CXCompoundType createType(AbstractSyntaxNode ast, CXIdentifier namespace) {
        
        AbstractSyntaxNode nameAST = ast.getChild(ASTNodeType.id);
        String name = nameAST != null? nameAST.getToken().getImage() : null;
        boolean isAnonymous = name == null;
        CXCompoundType output;
        if(ast.getType().equals(ASTNodeType.basic_compound_type_dec)) {
            boolean isUnion = ast.hasChild(ASTNodeType.union);
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
            CXIdentifier identifier = new CXIdentifier(namespace, name);
            List<CXMethod> methods = new LinkedList<>();
            List<CXConstructor> constructors = new LinkedList<>();
            List<CXClassType.ClassFieldDeclaration> fieldDeclarations = new LinkedList<>();
            
            List<AbstractSyntaxNode> constructorDefinitions = new LinkedList<>();
            List<Visibility> constructorVisibilities = new LinkedList<>();
            
            for (AbstractSyntaxNode abstractSyntaxNode : ast.getChild(ASTNodeType.class_level_decs)) {
                Visibility visibility = getVisibility(abstractSyntaxNode.getChild(ASTNodeType.visibility));
                
                AbstractSyntaxNode dec = abstractSyntaxNode.getChild(1);
                switch (dec.getType()) {
                    case declarations: {
                        if(dec.getType() != ASTNodeType.function_description) {
                            fieldDeclarations.addAll(
                                    createClassFieldDeclarations(visibility, dec)
                            );
                        }
                        break;
                    }
                    case function_description:
                    case function_definition: {
                        boolean isVirtual = dec.hasChild(ASTNodeType._virtual);
                        methods.add(
                                createMethod(visibility, isVirtual, dec)
                        );
                        break;
                    }
                    case constructor_definition: {
                        constructorDefinitions.add(dec);
                        constructorVisibilities.add(visibility);
                        break;
                    }
                    default:
                        throw new UnsupportedOperationException(dec.getType().toString());
                }
                
            }
            
            
            
            CXClassType cxClassType;
            if(ast.hasChild(ASTNodeType.inherit)) {
                
                String image = ast.getChild(ASTNodeType.inherit)
                        .getChild(ASTNodeType.typename).getToken().getImage();
                CXClassType parent = ((CXClassType) getNamedCompoundType(image));
                
                
                cxClassType = new CXClassType(identifier, parent, fieldDeclarations, methods, new LinkedList<>(), this);
                
                
            }
            else cxClassType = new CXClassType(identifier, fieldDeclarations, methods, new LinkedList<>(), this);
            
            Iterator<Visibility> visibilityIterator = constructorVisibilities.iterator();
            for (AbstractSyntaxNode dec: constructorDefinitions) {
                AbstractSyntaxNode params = dec.getChild(ASTNodeType.parameter_list);
                AbstractSyntaxNode compound = dec.getChild(ASTNodeType.compound_statement);
                if(dec.hasChild(ASTNodeType.sequence)) {
                    
                    AbstractSyntaxNode priorAST = dec.getChild(2);
                    CXConstructor prior;
                    if(priorAST.getType().equals(ASTNodeType._super)) {
                        
                        prior = cxClassType.getParent().getConstructor(params.getChildList().size());
                    } else if(priorAST.getType().equals(ASTNodeType.id)) {
                        prior = cxClassType.getConstructor(params.getChildList().size());
                    } else throw new UnsupportedOperationException();
                    
                    constructors.add(
                            createConstructor(visibilityIterator.next(), cxClassType, params, compound, prior)
                    );
                } else {
                    constructors.add(
                            createConstructor(visibilityIterator.next(), cxClassType, params, compound, null)
                    );
                }
            }
            
            for (CXConstructor constructor : constructors) {
                constructor.setParent(cxClassType);
            }
            cxClassType.addConstructors(constructors);
            createdClasses.add(cxClassType);
            cxClassType.setEnvironment(this);
            
            
            List<CXCompoundType> typesForNamespace = namespaceTree.getTypesForNamespace(namespace);
            typesForNamespace.add(cxClassType);
            
            addNamedCompoundType(cxClassType);
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
        if(ast.getType() != ASTNodeType.visibility) return null;
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
    
    /**
     * Creates a field declaration
     * @param ast must be type {@link ASTNodeType#basic_compound_type_field}
     * @return a field declaration object
     */
    private CXCompoundType.FieldDeclaration createFieldDeclaration(AbstractSyntaxNode ast) {
        CXType type;
        if(ast instanceof TypeAbstractSyntaxNode) {
            type = ((TypeAbstractSyntaxNode) ast).getCxType();
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
        if(ast instanceof TypeAbstractSyntaxNode) {
            type = ((TypeAbstractSyntaxNode) ast).getCxType();
        } else {
            throw new UnsupportedOperationException();
        }
        
        AbstractSyntaxNode idAST = ast.getChild(ASTNodeType.id);
        String name = idAST.getToken().getImage();
        return new CXClassType.ClassFieldDeclaration(type, name, visibility);
    }
    
    private CXMethod createMethod(Visibility visibility, boolean isVirtual, AbstractSyntaxNode ast) {
        if(!(ast.getType().equals(ASTNodeType.function_definition) || ast.getType() == ASTNodeType.function_description)) {
            throw new UnsupportedOperationException();
        }
        
        assert ast instanceof TypeAbstractSyntaxNode;
        TypeAbstractSyntaxNode typedAST = (TypeAbstractSyntaxNode) ast;
        
        CXType returnType = typedAST.getCxType();
        String name = ast.getChild(ASTNodeType.id).getToken().getImage();
        AbstractSyntaxNode after = ast.getChild(ASTNodeType.compound_statement);
        
        List<CXParameter> parameters = createParameters(ast.getChild(ASTNodeType.parameter_list));
        
        return new CXMethod(null, visibility, name, isVirtual, returnType, parameters, after);
    }
    
    private CXConstructor createConstructor(Visibility visibility, CXClassType parent, AbstractSyntaxNode params,
                                            AbstractSyntaxNode compound, CXConstructor prior) {
        List<CXParameter> parameters = createParameters(params);
        
        return new CXConstructor(parent, visibility, parameters, compound);
    }
    
    private List<CXParameter> createParameters(AbstractSyntaxNode ast) {
        List<CXParameter> output = new LinkedList<>();
        for (AbstractSyntaxNode abstractSyntaxNode : ast.getChildList()) {
            CXType type = ((TypeAbstractSyntaxNode) abstractSyntaxNode).getCxType();
            String name = abstractSyntaxNode.getChild(ASTNodeType.id).getToken().getImage();
            output.add(new CXParameter(type, name));
        }
        return output;
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
    
    /**
     * Checks if two types are equivalent, with const stripping for going from non-const to const
     * checks if type1 <= type2
     * @param o1 type1
     * @param o2 type2
     * @return whether they can be used
     */
    public boolean is(CXType o1, CXType o2) {
        
        if(!(o1 instanceof ConstantType) && o2 instanceof ConstantType) {
            return is(o1, ((ConstantType) o2).getSubtype());
        }
        if(o2 instanceof CXDynamicTypeDefinition) {
            return is(o1, ((CXDynamicTypeDefinition) o2).getOriginal());
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
        
        if(!(o1 instanceof ConstantType) && o2 instanceof ConstantType) {
            return is(o1, ((ConstantType) o2).getSubtype());
        }
        if(o2 instanceof CXDynamicTypeDefinition) {
            return is(o1, ((CXDynamicTypeDefinition) o2).getOriginal());
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
    
    public static TypeEnvironment getStandardEnvironment() {
        TypeEnvironment environment = new TypeEnvironment();
        
        
        environment.addTypeDefinition(
                UnsignedPrimitive.createUnsignedShort(), "boolean"
        );
        
        environment.standardBooleanDefined = true;
        
        
        return environment;
    }
}
