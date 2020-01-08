package radin.core.semantics.types.compound;

import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.*;
import radin.core.semantics.types.wrapped.CXDelayedTypeDefinition;
import radin.core.semantics.types.wrapped.CXDynamicTypeDefinition;
import radin.utility.Pair;
import radin.utility.Reference;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.exceptions.IncorrectParameterTypesError;
import radin.core.semantics.exceptions.RedeclareError;
import radin.core.semantics.types.wrapped.PointerType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.semantics.types.primitives.CXPrimitiveType;

import java.lang.reflect.Method;
import java.util.*;

public class CXClassType extends CXCompoundType {
    
    public static class ClassFieldDeclaration extends FieldDeclaration {
        private Visibility visibility;
        
        public ClassFieldDeclaration(CXType type, String name, Visibility visibility) {
            super(type, name);
            this.visibility = visibility;
        }
        
        public Visibility getVisibility() {
            return visibility;
        }
    }
    
    private CXClassType parent;
    
    private HashMap<String, Visibility> visibilityMap;
    
    private List<CXMethod> virtualMethodOrder;
    private List<CXMethod> concreteMethodsOrder;
    private List<CXConstructor> constructors;
    
    private List<Pair<CXMethod, CXMethod>> supersToCreate;
    private List<CXMethod> generatedSupers;
    
    private CXMethod initMethod;
    
    private boolean sealed;
    private TypeEnvironment environment;
    
    public CXClassType(CXIdentifier identifier, List<ClassFieldDeclaration> declarations,
                       List<CXMethod> methods, List<CXConstructor> constructors, TypeEnvironment environment) {
        this(identifier, null, declarations, methods, constructors, environment);
        
    }
    
    public TypeEnvironment getEnvironment() {
        return environment;
    }
    
    public CXClassType(CXIdentifier typename, CXClassType parent, List<ClassFieldDeclaration> declarations,
                       List<CXMethod> methods, List<CXConstructor> constructors, TypeEnvironment e) {
        super(typename, new LinkedList<>(declarations));
        this.environment = e;
        sealed = false;
        this.parent = parent;
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
        List<String> virtualMethodsAlreadyExplored = new LinkedList<>();
        for (CXMethod method : methods) {
            
            
            method.setIdentifier(new CXIdentifier(typename, method.getName()));
            
            
            if(method.isVirtual()) {
                if(virtualMethodsAlreadyExplored.contains(method.getName())) {
                    throw new RedeclareError(method.getName());
                }
                
                if(isVirtual(method.getName(), method.getParameterTypeList())) {
                    boolean changed = false;
                    for (int i = 0; i < virtualMethodOrder.size(); i++) {
                        CXMethod oldMethod = virtualMethodOrder.get(i);
                        if(oldMethod.getName().equals(method.getName()) &&
                                environment.isStrict(oldMethod.getReturnType(), method.getReturnType()) &&
                                oldMethod.getParameterTypes().equals(method.getParameterTypes())) {
                            supersToCreate.add(new Pair<>(oldMethod, method));
                            virtualMethodOrder.set(i, method);
                            if(oldMethod.getVisibility() != method.getVisibility()) {
                                visibilityMap.replace(method.getName(), method.getVisibility());
                            }
                            changed = true;
                            break;
                        }
                    }
                    if(!changed) {
                        CXType returnType = getVirtualMethod(method.getName(), method.getParameterTypeList()).getReturnType();
                        throw new IncompatibleReturnTypeError(method.getName(), returnType, method.getReturnType());
                    }
                } else {
                    if(isAlreadyDefined(method.getName(), method.getParameterTypes())) {
                        throw new RedeclareError(method.getName());
                    }
                    virtualMethodOrder.add(method);
                    visibilityMap.put(method.getName(), method.getVisibility());
                }
                virtualMethodsAlreadyExplored.add(method.getName());
                
            } else {
                if(isAlreadyDefined(method.getName(), method.getParameterTypes())) {
                    throw new RedeclareError(method.getName());
                }
                
                concreteMethodsOrder.add(method);
            }
        }
    }
    
    public void addConstructors(List<CXConstructor> constructors) {
        this.constructors.addAll(constructors);
        for (CXConstructor constructor : constructors) {
            visibilityMap.put(constructor.getName(), constructor.getVisibility());
        }
    }
    
    public void setEnvironment(TypeEnvironment environment) {
        this.environment = environment;
    }
    
    public CXStructType getVTable() {
        String name = getVTableName();
        List<FieldDeclaration> methods = new LinkedList<>();
        methods.add(new FieldDeclaration(CXPrimitiveType.INTEGER, "offset"));
        for (CXMethod cxMethod : virtualMethodOrder) {
            methods.add(convertToFieldDeclaration(cxMethod));
        }
        
        return new CXStructType(name, methods);
    }
    
    public CXMethod getInitMethod() {
        if(initMethod == null) {
            List<AbstractSyntaxNode> children = new LinkedList<>();
            
            AbstractSyntaxNode vtable = new AbstractSyntaxNode(
                    ASTNodeType.indirection,
                    CXMethod.variableAST("vtable")
            );
            
            AbstractSyntaxNode outputDec = new AbstractSyntaxNode(ASTNodeType.declarations,
                    new TypeAbstractSyntaxNode(
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
                    new TypeAbstractSyntaxNode(
                            ASTNodeType.declaration,
                            new PointerType(getVTable().getTypeIndirection()),
                            CXMethod.variableAST("vtable")
                    ));
            children.add(vtableDec);
            AbstractSyntaxNode vtableSet = assign(
                    CXMethod.variableAST("vtable"),
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
                                    "vtable"),
                            CXMethod.variableAST("vtable")
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
            
            for (CXMethod cxMethod : getAllConcreteMethods()) {
                AbstractSyntaxNode method = CXMethod.variableAST(cxMethod.getCMethodName());
                AbstractSyntaxNode function = CXMethod.variableAST(cxMethod.getCFunctionName());
                AbstractSyntaxNode fieldGet = new AbstractSyntaxNode(ASTNodeType.field_get,
                        new AbstractSyntaxNode(ASTNodeType.indirection, CXMethod.variableAST("output")),
                        method);
                
                children.add(assign(fieldGet, function));
            }
    
            /*
            for (FieldDeclaration field : getFields()) {
                initMethodBody.append("output->").append(field.getName()).append(" = {0};");
            }
            
             */
            
            children.add(new AbstractSyntaxNode(ASTNodeType._return, CXMethod.variableAST("output")));
            
            
            
            
            AbstractSyntaxNode compound = new AbstractSyntaxNode(ASTNodeType.compound_statement, children);
            //compound.printTreeForm();
            initMethod =  new CXMethod(null, Visibility._public, getCTypeName() + "_init", false, new PointerType(this),
                    new LinkedList<>(), compound);
        }
        
        return initMethod;
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
    
    public String getVTableName() {
        return getCTypeName() + "_vtable";
    }
    
    public String getCTypeName() {
        return "class_" + super.getCTypeName();
    }
    
    public boolean is(CXClassType other) {
        return getLineage().contains(other);
    }
    
    public void addVTableTypeToEnvironment(TypeEnvironment environment) {
        CXStructType vtable = getVTable();
        
        environment.addNamedCompoundType(vtable);
    }
    
    @Override
    public String generateCDeclaration() {
        return "struct " + getCTypeName();
    }
    
    public List<CXMethod> getVirtualMethodsOrder() {
        return virtualMethodOrder;
    }
    
    public List<CXMethod> getConcreteMethodsOrder() {
        return concreteMethodsOrder;
    }
    
    public List<CXConstructor> getConstructors() {
        return constructors;
    }
    
    public CXConstructor getConstructor(List<CXType> parameters, TypeEnvironment environment) {
        for (CXConstructor constructor : constructors) {
            
            List<CXType> parameterTypes = constructor.getParameterTypes();
            if(parameterTypes.size() != parameters.size()) {
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
    
    public CXConstructor getConstructor(int length) {
        for (CXConstructor constructor : constructors) {
            if(constructor.getParameterTypes().size() == length + 1) return constructor;
        }
        return null;
    }
    
    public CXConstructor getConstructor(ParameterTypeList parameterTypeList) {
        for (CXConstructor constructor : constructors) {
            if(parameterTypeList.equals(constructor.getParameterTypeList(), environment)) return constructor;
        }
        return null;
    }
    
    public CXMethod getMethod(String name, ParameterTypeList parameterTypeList, Reference<Boolean> isVirtual) {
        CXMethod output = getVirtualMethod(name, parameterTypeList);
        if(output != null) {
            isVirtual.setValue(true);
            return output;
        }
        CXMethod concreteMethod = getConcreteMethod(name, parameterTypeList);
        if(concreteMethod != null) {
            isVirtual.setValue(false);
            return concreteMethod;
        }
        return null;
    }
    
    public void generateSuperMethods(String vtablename) {
        generatedSupers = new LinkedList<>();
        for (Pair<CXMethod,CXMethod> methods : supersToCreate) {
            generatedSupers.add(methods.getVal1().createSuperMethod(this, vtablename, methods.getVal2()));
        }
    }
    
    public CXMethod getSuperMethod(String name, ParameterTypeList typeList) {
        if(generatedSupers == null) return null;
        for (CXMethod generatedSuper : generatedSupers) {
            if(generatedSuper.getName().contains(name) && typeList.equals(generatedSuper.getParameterTypeList(),
                    environment)) return generatedSuper;
        }
        
        return null;
    }
    
    public boolean isVirtual(String name, ParameterTypeList typeList) {
        Reference<Boolean> output = new Reference<>();
        CXMethod method = getMethod(name, typeList, output);
        return method != null && output.getValue();
    }
    
    private CXMethod getVirtualMethod(String name, ParameterTypeList parameterTypeList) {
        for (CXMethod cxMethod : virtualMethodOrder) {
            if(cxMethod.getName().equals(name) && parameterTypeList.equals(cxMethod.getParameterTypeList(),
                    environment)) {
                return cxMethod;
            }
        }
        return null;
    }
    
    private CXMethod getConcreteMethod(String name, ParameterTypeList parameterTypeList) {
        for (CXMethod cxMethod : getAllConcreteMethods()) {
            if(cxMethod.getName().equals(name) && parameterTypeList.equals(cxMethod.getParameterTypeList(), environment)) {
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
                new FieldDeclaration(vtableType, "vtable")
        );
        
        
        for (CXClassType cxClass : getLineage()) {
            fieldDeclarations.addAll(cxClass.getCFields(environment));
            for (CXMethod cxMethod : cxClass.concreteMethodsOrder) {
                fieldDeclarations.add(
                        convertToFieldDeclaration(cxMethod)
                );
            }
        }
        
        return new CXStructType(getCTypeName(), fieldDeclarations);
    }
    
    public CXType getCTypeIndirection() {
        return new CXCompoundTypeNameIndirection(CXCompoundTypeNameIndirection.CompoundType.struct, getCTypeName());
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
    
    public List<CXMethod> getGeneratedSupers() {
        return generatedSupers;
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
    
    public FieldDeclaration convertToFieldDeclaration(CXMethod method) {
        CXType type = method.getFunctionPointer();
        return new FieldDeclaration(type, method.getCMethodName());
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
    
    public List<CXClassType> getReverseInheritanceOrder() {
        List<CXClassType> lineage = getLineage();
        Collections.reverse(lineage);
        return lineage;
    }
    
    private boolean isExistingVirtualMethod(String name) {
        for (CXMethod cxMethod : virtualMethodOrder) {
            if(cxMethod.getName().equals(name)) return true;
        }
        return false;
    }
    
    public Visibility getVisibility(String name) {
        return visibilityMap.get(name);
    }
    
    
    
    public Set<String> getAllNames() {
        return visibilityMap.keySet();
    }
    
    @Override
    public String generateCDefinition(String identifier) {
        return generateCDefinition() + " " + identifier;
    }
    
    public CXClassType getParent() {
        return parent;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        
        
        if(other instanceof CXClassType){
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
    public String generateCDefinition() {
        return getStructName();
    }
    
    @Override
    public String toString() {
        return "CXClass " + getTypeName();
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
    
    private String getStructName() {
        return "struct " + getCTypeName();
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
            if(cxMethod.getName().equals(name) && cxMethod.getParameterTypes().equals(types)) return true;
        }
        return false;
    }
    
    @Override
    public CXType getTypeIndirection() {
        return this;
        //return new CXDynamicTypeDefinition()
        //return new CXDelayedTypeDefinition(getTypeNameIdentifier(), null, environment);
    }
    
}
