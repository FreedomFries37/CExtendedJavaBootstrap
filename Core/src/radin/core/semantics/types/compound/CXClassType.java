package radin.core.semantics.types.compound;

import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.exceptions.IncorrectParameterTypesError;
import radin.core.semantics.exceptions.MismatchedTypeEnvironmentException;
import radin.core.semantics.exceptions.RedeclareError;
import radin.core.semantics.types.*;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.Pair;
import radin.core.utility.Reference;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.primitives.PointerType;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.core.utility.UniversalCompilerSettings;

import java.util.*;

public class CXClassType extends CXCompoundType implements ICXClassType {
    
    private CXClassType parent;
    
    private HashMap<String, Visibility> visibilityMap;
    
    private List<CXMethod> virtualMethodOrder;
    private List<CXMethod> concreteMethodsOrder;
    /**
     * Methods created for this class
     */
    private List<CXMethod> instanceMethods;
    private List<CXConstructor> constructors;
    
    private List<Pair<CXMethod, CXMethod>> supersToCreate;
    private List<CXMethod> generatedSupers;
    
    
    private CXMethod initMethod;
    
    private boolean sealed;
    private TypeEnvironment environment;
    private List<ClassFieldDeclaration> classFields;
    
    public CXClassType(CXIdentifier identifier, List<ClassFieldDeclaration> declarations,
                       List<CXMethod> methods, List<CXConstructor> constructors, TypeEnvironment environment) {
        this(identifier, null, declarations, methods, constructors, environment);
        
    }
    
    public CXClassType(CXIdentifier typename, CXClassType parent, List<ClassFieldDeclaration> declarations,
                       List<CXMethod> methods, List<CXConstructor> constructors, TypeEnvironment e) {
        super(typename, new LinkedList<>(declarations));
        classFields = declarations;
        ICompilationSettings.debugLog.finest("Creating class " + typename);
        this.environment = e;
        sealed = false;
        this.parent = parent;
        instanceMethods = new LinkedList<>();
        instanceMethods.addAll(methods);
        if(parent != null) {
            this.virtualMethodOrder = new LinkedList<>(parent.virtualMethodOrder);
            visibilityMap = new HashMap<>(parent.visibilityMap);
        } else {
            this.virtualMethodOrder = new LinkedList<>();
            visibilityMap = new HashMap<>();
        }
        concreteMethodsOrder = new LinkedList<>();
        /*if(parent != null) {
            concreteMethodsOrder.addAll(parent.getConcreteMethodsOrder());
        }*/
        this.constructors = constructors;
        supersToCreate = new LinkedList<>();
        
        for (CXMethod method : methods) {
            method.setParent(this);
        }
        
        for (CXConstructor constructor : constructors) {
            constructor.setParent(this);
        }
        
        for (ClassFieldDeclaration field : declarations) {
            if(isAlreadyDefined(field.getName())) throw new RedeclareError(field.getName());
            visibilityMap.put(field.getName(), field.getVisibility());
        }
        List<Pair<String, ParameterTypeList>> virtualMethodsAlreadyExplored = new LinkedList<>();
        for (CXMethod method : methods) {
            
            
            Token identifier = method.getName().getBase();
            method.setIdentifier(new CXIdentifier(typename, identifier));
            
            
            if(method.isVirtual()) {
                if(virtualMethodsAlreadyExplored.contains(new Pair<>(method.getIdentifierName(), method.getParameterTypeList()))) {
                    throw new RedeclareError(method.getIdentifierName());
                }
                
                if(isVirtualStrict(method.getIdentifierName(), method.getParameterTypeList())) {
                    boolean changed = false;
                    for (int i = 0; i < virtualMethodOrder.size(); i++) {
                        CXMethod oldMethod = virtualMethodOrder.get(i);
                        if(oldMethod.getIdentifierName().equals(method.getIdentifierName()) &&
                                oldMethod.getReturnType().is(method.getReturnType(), environment) &&
                                oldMethod.getParameterTypeList().equalsExact(method.getParameterTypeList(), environment)) {
                            supersToCreate.add(new Pair<>(oldMethod, method));
                            virtualMethodOrder.set(i, method);
                            if(oldMethod.getVisibility() != method.getVisibility()) {
                                visibilityMap.replace(method.getIdentifierName(), method.getVisibility());
                            }
                            changed = true;
                            break;
                        }
                    }
                    if(!changed) {
                        CXMethod virtualMethod = getVirtualMethod(identifier, method.getParameterTypeList());
                        if(virtualMethod == null) throw new AmbiguousMethodCallError(identifier, new LinkedList<>(),
                                new LinkedList<>());
                        CXType returnType =
                                virtualMethod.getReturnType();
                        throw new IncompatibleReturnTypeError(method.getIdentifierName(), returnType, method.getReturnType());
                    }
                } else {
                    if(isAlreadyDefined(method.getIdentifierName(), method.getParameterTypes())) {
                        throw new RedeclareError(method.getIdentifierName());
                    }
                    virtualMethodOrder.add(method);
                    visibilityMap.put(method.getIdentifierName(), method.getVisibility());
                }
                virtualMethodsAlreadyExplored.add(new Pair(method.getIdentifierName(), method.getParameterTypeList()));
                
            } else {
                if(isAlreadyDefined(method.getIdentifierName(), method.getParameterTypes())) {
                    throw new RedeclareError(method.getIdentifierName());
                }
                
                concreteMethodsOrder.add(method);
            }
        }
    }
    
    public List<ClassFieldDeclaration> getClassFields() {
        return classFields;
    }
    
    @Override
    public TypeEnvironment getEnvironment() {
        return environment;
    }
    
    @Override
    public void addConstructors(List<CXConstructor> constructors) {
        this.constructors.addAll(constructors);
        for (CXConstructor constructor : constructors) {
            visibilityMap.put(constructor.getIdentifierName(), constructor.getVisibility());
        }
    }
    
    @Override
    public void setEnvironment(TypeEnvironment environment) {
        this.environment = environment;
    }
    
    @Override
    public CXStructType getVTable() {
        String name = getVTableName();
        List<FieldDeclaration> methods = new LinkedList<>();
        methods.add(new FieldDeclaration(CXPrimitiveType.INTEGER, "offset"));
        for (CXMethod cxMethod : virtualMethodOrder) {
            methods.add(convertToFieldDeclaration(cxMethod));
        }
        
        return new CXStructType(new Token(TokenType.t_id, name), methods);
    }
    
    @Override
    public CXMethod getInitMethod() {
        if(initMethod == null) {
            List<AbstractSyntaxNode> children = new LinkedList<>();
    
            String vtableName = UniversalCompilerSettings.getInstance().getSettings().getvTableName();
            AbstractSyntaxNode vtable = new AbstractSyntaxNode(
                    ASTNodeType.indirection,
                    CXMethod.variableAST(vtableName)
            );
            
            AbstractSyntaxNode outputDec = new AbstractSyntaxNode(ASTNodeType.declarations,
                    new TypedAbstractSyntaxNode(
                            ASTNodeType.declaration,
                            new PointerType(this),
                            CXMethod.variableAST("output")
                    ));
            children.add(outputDec);
            AbstractSyntaxNode outputSet = assign(
                    CXMethod.variableAST("output"),
                    new AbstractSyntaxNode(ASTNodeType.id,
                            new Token(TokenType.t_id, "calloc" + "(1, sizeof(struct " + this.getCTypeName() + "))"))
            );
            children.add(outputSet);
            
            
            AbstractSyntaxNode vtableDec = new AbstractSyntaxNode(ASTNodeType.declarations,
                    new TypedAbstractSyntaxNode(
                            ASTNodeType.declaration,
                            new PointerType(getVTable().getTypeIndirection()),
                            CXMethod.variableAST(vtableName)
                    ));
            children.add(vtableDec);
            AbstractSyntaxNode vtableSet = assign(
                    CXMethod.variableAST(vtableName),
                    new AbstractSyntaxNode(ASTNodeType.id,
                            new Token(TokenType.t_id, "malloc" + "(sizeof(struct " + getVTableName() + "))"))
            );
            children.add(vtableSet);
            
            children.add(
                    assign(
                            fieldGet(
                                    new AbstractSyntaxNode(
                                            ASTNodeType.indirection,
                                            CXMethod.variableAST("output")),
                                    vtableName),
                            CXMethod.variableAST(vtableName)
                    )
            );
            children.add(assign(fieldGet(vtable, "offset"), new AbstractSyntaxNode(ASTNodeType.literal,
                    new Token(TokenType.t_literal, "0"))));
            
            for (CXMethod cxMethod : virtualMethodOrder) {
                AbstractSyntaxNode method = CXMethod.variableAST(cxMethod.getCMethodName());
                AbstractSyntaxNode function = CXMethod.variableAST(cxMethod.getCFunctionName());
                AbstractSyntaxNode fieldGet = new AbstractSyntaxNode(ASTNodeType.field_get,
                        vtable,
                        method);
                
                children.add(assign(fieldGet, function));
                
                
                
                // initMethodBody.append("vtable->").append(cxMethod.getCFunctionName()).append(" = ").append(cxMethod
                // .getCFunctionName()).append(";");
            }
            AbstractSyntaxNode output = new AbstractSyntaxNode(ASTNodeType.indirection, CXMethod.variableAST("output"));
            for (CXMethod cxMethod : getAllConcreteMethods()) {
                AbstractSyntaxNode method = CXMethod.variableAST(cxMethod.getCMethodName());
                AbstractSyntaxNode function = CXMethod.variableAST(cxMethod.getCFunctionName());
                
                AbstractSyntaxNode fieldGet = new AbstractSyntaxNode(ASTNodeType.field_get,
                        output,
                        method);
                
                children.add(assign(fieldGet, function));
            }
            
            
            for (FieldDeclaration field : getAllFields()) {
                AbstractSyntaxNode var = CXMethod.variableAST(field.getName());
                AbstractSyntaxNode fieldGet = new AbstractSyntaxNode(ASTNodeType.field_get,
                        output,
                        var);
                AbstractSyntaxNode assignment;
                CXType type = field.getType();
                if(type instanceof ICXWrapper) {
                    type = ((ICXWrapper) type).getWrappedType();
                }
                
                if (field.getName().equals("info") && type instanceof PointerType &&
                        ((PointerType) type).getSubType() instanceof CXClassType &&
                        ((CXClassType) ((PointerType) type).getSubType()).getTypeName().equals("std::ClassInfo")) {
                    assignment = new TypedAbstractSyntaxNode(
                            ASTNodeType.cast,
                            field.getType(),
                            new AbstractSyntaxNode(ASTNodeType.id, new Token(TokenType.t_id,
                                    "__get_class(" + environment.getTypeId(this) + ")")
                            )
                    );
                } else {
                    assignment = new TypedAbstractSyntaxNode(
                            ASTNodeType.cast,
                            field.getType(),
                            new AbstractSyntaxNode(ASTNodeType.id, new Token(TokenType.t_id,
                                    "{0}")
                            )
                    );
                }
                
                
                children.add(assign(fieldGet, assignment));
                // initMethodBody.append("output->").append(field.getName()).append(" = {0};");
            }
            
            
            
            children.add(new AbstractSyntaxNode(ASTNodeType._return, CXMethod.variableAST("output")));
            
            
            
            
            AbstractSyntaxNode compound = new AbstractSyntaxNode(ASTNodeType.compound_statement, children);
            //compound.printTreeForm();
            initMethod =  new CXMethod(null, Visibility._public, new Token(TokenType.t_id, getCTypeName() + "_init"),
                    false,
                    new PointerType(this),
                    new LinkedList<>(), compound);
        }
        
        return initMethod;
    }
    
    public List<CXMethod> getAllMethods() {
        List<CXMethod> output = getConcreteMethodsOrder();
        output.addAll(getVirtualMethodsOrder());
        return output;
    }
    
    @Override
    public List<CXMethod> getVirtualMethodsOrder() {
        return virtualMethodOrder;
    }
    
    @Override
    public List<CXMethod> getConcreteMethodsOrder() {
        return concreteMethodsOrder;
    }
    
    @Override
    public List<CXConstructor> getConstructors() {
        return constructors;
    }
    
    @Override
    public CXConstructor getConstructor(List<CXType> parameters, TypeEnvironment environment) {
        for (CXConstructor constructor : constructors) {
            
            List<CXType> parameterTypes = constructor.getParameterTypes();
            if(parameterTypes.size() == parameters.size()) {
                boolean allTrue = true;
                for (int i = 0; i < parameters.size(); i++) {
                    if(!parameters.get(i).is(parameterTypes.get(i), environment)) {
                        allTrue = false;
                        break;
                    }
                }
                if(allTrue) return constructor;
            }
        }
        throw new IncorrectParameterTypesError();
    }
    
    @Override
    public CXConstructor getConstructor(int length) {
        for (CXConstructor constructor : constructors) {
            if(constructor.getParameterTypes().size() == length + 1) return constructor;
        }
        return null;
    }
    
    @Override
    public CXConstructor getConstructor(ParameterTypeList parameterTypeList) {
        for (CXConstructor constructor : constructors) {
            if(parameterTypeList.equals(constructor.getParameterTypeList(), environment)) return constructor;
        }
        return null;
    }
    
    @Override
    public CXMethod getMethod(Token name, ParameterTypeList parameterTypeList, Reference<Boolean> isVirtual) {
        CXMethod output = getVirtualMethod(name, parameterTypeList);
        if(output != null) {
            if(isVirtual != null) isVirtual.setValue(true);
            return output;
        }
        CXMethod concreteMethod = getConcreteMethod(name, parameterTypeList);
        if(concreteMethod != null) {
            if(isVirtual != null) isVirtual.setValue(false);
            return concreteMethod;
        }
        return null;
    }
    
    @Override
    public void generateSuperMethods(String vtablename) {
        generatedSupers = new LinkedList<>();
        for (Pair<CXMethod,CXMethod> methods : supersToCreate) {
            generatedSupers.add(methods.getVal1().createSuperMethod(this, vtablename, methods.getVal2()));
        }
    }
    
    @Override
    public CXMethod getSuperMethod(String name, ParameterTypeList typeList) {
        if(generatedSupers == null) return null;
        for (CXMethod generatedSuper : generatedSupers) {
            if(generatedSuper.getIdentifierName().contains(name) && typeList.equals(generatedSuper.getParameterTypeList(),
                    environment)) return generatedSuper;
        }
        
        return null;
    }
    
    @Override
    public boolean isVirtual(Token name, ParameterTypeList typeList) {
        Reference<Boolean> output = new Reference<>();
        CXMethod method = getMethod(name, typeList, output);
        return method != null && output.getValue();
    }
    
    @Override
    public CXStructType getStructEquivalent() {
        return getStructEquivalent(environment);
    }
    
    public CXStructType getStructEquivalent(TypeEnvironment environment) {
        
        if(!environment.namedCompoundTypeExists(getVTableName())) {
            addVTableTypeToEnvironment(environment);
        }
        
        CXType vtableType = new PointerType(environment.getNamedCompoundType(getVTableName()).getTypeIndirection());
        List<FieldDeclaration> fieldDeclarations = new LinkedList<>();
        fieldDeclarations.add(
                new FieldDeclaration(vtableType, UniversalCompilerSettings.getInstance().getSettings().getvTableName())
        );
        
        
        for (CXClassType cxClass : getLineage()) {
            fieldDeclarations.addAll(cxClass.getCFields(environment));
            for (CXMethod cxMethod : cxClass.concreteMethodsOrder) {
                fieldDeclarations.add(
                        convertToFieldDeclaration(cxMethod)
                );
            }
        }
        
        return new CXStructType(new Token(TokenType.t_id, getCTypeName()), fieldDeclarations);
    }
    
    public String getVTableName() {
        return getCTypeName() + "_vtable";
    }
    
    public void addVTableTypeToEnvironment(TypeEnvironment environment) {
        CXStructType vtable = getVTable();
        
        environment.addNamedCompoundType(vtable);
    }
    
    public List<FieldDeclaration> getCFields(TypeEnvironment e) {
        List<FieldDeclaration> output= new LinkedList<>();
        for (FieldDeclaration field : getFields()) {
            CXType type = field.getType();
            CXType cIndirection = type.getCTypeIndirection();
            output.add(
                    new FieldDeclaration(cIndirection, field.getName())
            );
        }
        return output;
    }
    
    @Override
    public List<CXMethod> getGeneratedSupers() {
        return generatedSupers;
    }
    
    public FieldDeclaration convertToFieldDeclaration(CXMethod method) {
        CXType type = method.getFunctionPointer();
        return new FieldDeclaration(type, method.getCMethodName());
    }
    
    public List<FieldDeclaration> getAllFields() {
        List<FieldDeclaration> output = new LinkedList<>();
        if(parent != null) output.addAll(parent.getAllFields());
        output.addAll(getFields());
        return output;
    }
    
    public List<CXMethod> getAllConcreteMethods() {
        if(this.parent == null) return concreteMethodsOrder;
        List<CXMethod> output = new LinkedList<>(parent.getAllConcreteMethods());
        output.addAll(concreteMethodsOrder);
        return output;
    }
    
    private AbstractSyntaxNode assign(AbstractSyntaxNode lhs, AbstractSyntaxNode rhs) {
        return new AbstractSyntaxNode(ASTNodeType.assignment, lhs, new AbstractSyntaxNode(ASTNodeType.assignment_type,
                new Token(TokenType.t_assign)),
                rhs);
    }
    
    private AbstractSyntaxNode fieldGet(AbstractSyntaxNode lhs, String name) {
        return new AbstractSyntaxNode(ASTNodeType.field_get,
                lhs,
                CXMethod.variableAST(name));
    }
    
    public List<CXMethod> getInstanceMethods() {
        return instanceMethods;
    }
    
    public boolean is(CXClassType other) {
        return getLineage().contains(other);
    }
    
    public List<CXClassType> getLineage() {
        List<CXClassType> output = new LinkedList<>();
        CXClassType ptr = this;
        while(ptr != null) {
            output.add(0, ptr);
            ptr = ptr.parent;
        }
        return output;
    }
    
    public CXMethod getMethodStrict(String name, ParameterTypeList parameterTypeList, Reference<Boolean> isVirtual) {
        ICompilationSettings.debugLog.finest("Getting method with strict parameter checking");
        ICompilationSettings.debugLog.finest("Name = " + name);
        ICompilationSettings.debugLog.finest("Parameter types = " + parameterTypeList);
        CXMethod output = getVirtualMethodStrict(name, parameterTypeList);
        if(output != null) {
            if(isVirtual != null) isVirtual.setValue(true);
            return output;
        }
        CXMethod concreteMethod = getConcreteMethodStrict(name, parameterTypeList);
        if(concreteMethod != null) {
            if(isVirtual != null) isVirtual.setValue(false);
            return concreteMethod;
        }
        return null;
    }
    
    public boolean isVirtualStrict(String name, ParameterTypeList typeList) {
        Reference<Boolean> output = new Reference<>();
        CXMethod method = getMethodStrict(name, typeList, output);
        return method != null && output.getValue();
    }
    
    private CXMethod getVirtualMethod(Token name, ParameterTypeList parameterTypeList) {
        return getVirtualMethod(name, parameterTypeList, new LinkedList<>());
    }
    
    private CXMethod getVirtualMethod(Token name, ParameterTypeList parameterTypeList, List<Token> tokens) {
        List<CXMethod> options = new LinkedList<>();
        for (CXMethod cxMethod : virtualMethodOrder) {
            if(cxMethod.getIdentifierName().equals(name.getImage()) && parameterTypeList.equals(cxMethod.getParameterTypeList(),
                    environment)) {
                if(parameterTypeList.equalsExact(cxMethod.getParameterTypeList(), environment)) {
                    return cxMethod;
                }
                options.add(cxMethod);
            }
        }
        if(options.size() == 0) return null;
        if(options.size() >= 2) throw new AmbiguousMethodCallError(name, options, tokens);
        return options.get(0);
    }
    
    private CXMethod getConcreteMethod(Token name, ParameterTypeList parameterTypeList) {
        for (CXMethod cxMethod : getAllConcreteMethods()) {
            if(cxMethod.getIdentifierName().equals(name.getImage()) && parameterTypeList.equals(cxMethod.getParameterTypeList(),
                    environment)) {
                return cxMethod;
            }
        }
        return null;
    }
    
    private CXMethod getVirtualMethodStrict(String name, ParameterTypeList parameterTypeList) {
        for (CXMethod cxMethod : virtualMethodOrder) {
            
            if(cxMethod.getIdentifierName().equals(name) && parameterTypeList.equalsExact(cxMethod.getParameterTypeList(),
                    environment)) {
                return cxMethod;
            }
        }
        return null;
    }
    
    private CXMethod getConcreteMethodStrict(String name, ParameterTypeList parameterTypeList) {
        for (CXMethod cxMethod : getAllConcreteMethods()) {
            if(cxMethod.getIdentifierName().equals(name) && parameterTypeList.equalsExact(cxMethod.getParameterTypeList(),
                    environment)) {
                return cxMethod;
            }
        }
        return null;
    }
    
    public void seal(TypeEnvironment e) {
        if(!sealed) {
            sealed = true;
            addVTableTypeToEnvironment(e);
            e.addNamedCompoundType(getStructEquivalent(e));
            this.environment =e;
        }
    }
    
    public String classInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(toString());
        builder.append(" INFORMATION");
        for (CXClassType cxClassType : getLineage()) {
            for (FieldDeclaration field : cxClassType.getFields()) {
                Visibility visibility = getVisibility(field.getName());
                builder.append("\n\t");
                builder.append(visibility.toString());
                builder.append(" ");
                builder.append(field);
            }
            
            
            
        }
        return builder.toString();
    }
    
    @Override
    public String infoDump() {
        return toString() + " in " + environment;
    }
    
    public Visibility getVisibility(String name) {
        return visibilityMap.get(name);
    }
    
    public List<CXClassType> getReverseInheritanceOrder() {
        List<CXClassType> lineage = getLineage();
        Collections.reverse(lineage);
        return lineage;
    }
    
    private boolean isExistingVirtualMethod(String name) {
        for (CXMethod cxMethod : virtualMethodOrder) {
            if(cxMethod.getIdentifierName().equals(name)) return true;
        }
        return false;
    }
    
    public Set<String> getAllNames() {
        return visibilityMap.keySet();
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        return generateCDefinition() + " " + identifier;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        
        
        if(other instanceof CXClassType){
            if(((CXClassType) other).environment != e) {
                throw new MismatchedTypeEnvironmentException(this, e, other, ((CXClassType) other).environment);
            }
            return this.getLineage().contains(other);
        } else if(other instanceof CXCompoundTypeNameIndirection) {
            if(((CXCompoundTypeNameIndirection) other).getCompoundType() != CXCompoundTypeNameIndirection.CompoundType._class) return false;
            CXCompoundType namedCompoundType = e.getNamedCompoundType(((CXCompoundTypeNameIndirection) other).getTypename());
            if(!(namedCompoundType instanceof CXClassType)) return false;
            
            return e.is(this, namedCompoundType);
        }
        return false;
        
    }
    
    @Override
    public CXType getTypeIndirection() {
        return this;
        //return new CXDynamicTypeDefinition()
        //return new CXDelayedTypeDefinition(getTypeNameIdentifier(), null, environment);
    }
    
    public CXType getCTypeIndirection() {
        return new CXCompoundTypeNameIndirection(CXCompoundTypeNameIndirection.CompoundType.struct,
                new Token(TokenType.t_typename, getCTypeName()));
    }
    
    @Override
    public String generateCDefinition() {
        return getStructName();
    }
    
    @Override
    public String generateCDeclaration() {
        return "struct " + getCTypeName();
    }
    
    @Override
    public String ASTableDeclaration() {
        return getTypeNameIdentifier().toString();
    }
    
    public String getCTypeName() {
        return "class_" + super.getCTypeName();
    }
    
    @Override
    public String toString() {
        return getTypeName();
    }
    
    private String getStructName() {
        return "struct " + getCTypeName();
    }
    
    public CXClassType getParent() {
        return parent;
    }
    
    private String guard() {
        return "__" + getTypeName().replaceAll("\\s+", "") + "__guard";
    }
    
    private String createNewFunctionDefinition() {
        return getStructName() + "* new_" + getCTypeName() + "() {\n" +
                "\treturn malloc(sizeof(" + getStructName() + "));\n}";
    }
    
    private String createNewFunctionDeclaration() {
        return getStructName() + "* new_" + getCTypeName() + "();\n";
    }
    
    private boolean isAlreadyDefined(String name) {
        return isAlreadyDefined(name, null);
    }
    
    private boolean isAlreadyDefined(String name, List<CXType> types) {
        if(parent != null) {
            if(parent.isAlreadyDefined(name, types)) return false;
        }
        if(types == null) {
            return visibilityMap.containsKey(name);
        }
        for (CXMethod cxMethod : concreteMethodsOrder) {
            if(cxMethod.getIdentifierName().equals(name) && cxMethod.getParameterTypes().equals(types)) return true;
        }
        return false;
    }
    
    @Override
    public boolean canInstantiateDirectly() {
        return true;
    }
    
    @Override
    public CXType getAsCXType() {
        return this;
    }
    
    
    
    
}
