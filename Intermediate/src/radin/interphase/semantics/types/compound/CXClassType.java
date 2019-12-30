package radin.interphase.semantics.types.compound;

import radin.interphase.Reference;
import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.exceptions.IncorrectParameterTypesError;
import radin.interphase.semantics.exceptions.RedeclareError;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.CXCompoundTypeNameIndirection;
import radin.interphase.semantics.types.wrapped.PointerType;
import radin.interphase.semantics.types.Visibility;
import radin.interphase.semantics.types.methods.CXConstructor;
import radin.interphase.semantics.types.methods.CXMethod;
import radin.interphase.semantics.types.methods.ParameterTypeList;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;

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
    
    private List<CXMethod> supersToCreate;
    private List<CXMethod> generatedSupers;
    
    private boolean sealed;
    private TypeEnvironment environment;
    
    public CXClassType(String typename, List<ClassFieldDeclaration> declarations,
                       List<CXMethod> methods, List<CXConstructor> constructors, TypeEnvironment environment) {
        this(typename, null, declarations, methods, constructors, environment);
        
    }
    
    
    public CXClassType(String typename, CXClassType parent, List<ClassFieldDeclaration> declarations,
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
            if(method.isVirtual()) {
                if(virtualMethodsAlreadyExplored.contains(method.getName())) {
                    throw new RedeclareError(method.getName());
                }
                
                if(isVirtual(method.getName(), method.getParameterTypeList())) {
                    for (int i = 0; i < virtualMethodOrder.size(); i++) {
                        CXMethod cxMethod = virtualMethodOrder.get(i);
                        if(cxMethod.getName().equals(method.getName()) && cxMethod.getParameterTypes().equals(method.getParameterTypes())) {
                            supersToCreate.add(cxMethod);
                            virtualMethodOrder.set(i, method);
                            if(cxMethod.getVisibility() != method.getVisibility()) {
                                visibilityMap.replace(method.getName(), method.getVisibility());
                            }
                            break;
                        }
                    }
                } else {
                    if(isAlreadyDefined(method.getName())) {
                        throw new RedeclareError(method.getName());
                    }
                    virtualMethodOrder.add(method);
                    visibilityMap.put(method.getName(), method.getVisibility());
                }
                virtualMethodsAlreadyExplored.add(method.getName());
                
            } else {
                if(isAlreadyDefined(method.getName())) {
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
    
    public String getVTableName() {
        return getCTypeName() + "_vtable";
    }
    
    public String getCTypeName() {
        return "class_" + getTypeName();
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
        for (CXMethod method : supersToCreate) {
            generatedSupers.add(method.createSuperMethod(this, vtablename));
        }
    }
    
    public CXMethod getSuperMethod(String name, ParameterTypeList typeList) {
        if(generatedSupers == null) return null;
        for (CXMethod generatedSuper : generatedSupers) {
            if(generatedSuper.getName().equals("super_" + name) && typeList.equals(generatedSuper.getParameterTypeList(),
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
            if(cxMethod.getName().equals(name) && parameterTypeList.equals(cxMethod.getParameterTypeList(), environment)) {
                return cxMethod;
            }
        }
        return null;
    }
    
    private CXMethod getConcreteMethod(String name, ParameterTypeList parameterTypeList) {
        for (CXMethod cxMethod : concreteMethodsOrder) {
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
        return new FieldDeclaration(type, method.getName());
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
        return getTypeName();
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
        if(parent != null) {
            if(parent.isAlreadyDefined(name)) return false;
        }
        return visibilityMap.containsKey(name);
    }
    
    @Override
    public CXType getTypeIndirection() {
        return new CXCompoundTypeNameIndirection(CXCompoundTypeNameIndirection.CompoundType._class, this);
    }
    
}
