package radin.output.typeanalysis;

import radin.core.IdentifierResolver;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.exceptions.RedeclareError;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.Visibility;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.Option;
import radin.core.utility.Reference;
import radin.output.typeanalysis.errors.ClassNotDefinedError;
import radin.output.typeanalysis.errors.RedeclarationError;

import java.util.*;
import java.util.stream.Collectors;

public class VariableTypeTracker implements IVariableTypeTracker {
    
    public enum EntryStatus {
        OLD,
        NEW,
        FIXED,
    }
    
    public static class TypeTrackerEntry {
        private EntryStatus status;
        private CXType type;
        
        TypeTrackerEntry(EntryStatus status, CXType type) {
            this.status = status;
            this.type = type;
        }
        
        public TypeTrackerEntry(TypeTrackerEntry other) {
            this.status = other.status;
            this.type = other.type;
        }
        
        public EntryStatus getStatus() {
            return status;
        }
        
        void setStatus(EntryStatus status) {
            this.status = status;
        }
        
        public CXType getType() {
            return type;
        }
        
        @Override
        public String toString() {
            return "{" +
                    "status=" + status +
                    ", type=" + type +
                    '}';
        }
    }
    
    private static class CompoundDeclarationKey {
        private CXCompoundType type;
        private String name;
        
        public CompoundDeclarationKey(CXCompoundType type, String name) {
            this.type = type;
            this.name = name;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            
            CompoundDeclarationKey compoundDeclarationKey = (CompoundDeclarationKey) o;
            
            if (!type.equals(compoundDeclarationKey.type)) return false;
            return name.equals(compoundDeclarationKey.name);
        }
        
        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
        
        @Override
        public String toString() {
            return "{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
    
    private class MethodKey extends CompoundDeclarationKey {
        
        private ParameterTypeList parameterTypeList;
        
        public MethodKey(CXCompoundType type, String name, ParameterTypeList params) {
            super(type, name);
            parameterTypeList = params;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            
            MethodKey methodKey = (MethodKey) o;
            return parameterTypeList.equals(methodKey.parameterTypeList, environment);
        }
    
        public ParameterTypeList getParameterTypeList() {
            return parameterTypeList;
        }
    
        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + parameterTypeList.hashCode();
            return result;
        }
    
        @Override
        public String toString() {
            return super.toString() +
                    " parameterTypeList=" + parameterTypeList;
        }
    }
    
    private class ConstructorKey extends MethodKey {
    
        public ConstructorKey(CXCompoundType type, ParameterTypeList params) {
            super(type, "", params);
        }
    }


    private IdentifierResolver resolver;

    /**
     * Keep track of available types
     */
    private HashSet<CXCompoundType> trackingTypes;
    // lexical variables
    // these should be demoted
    private HashMap<String, TypeTrackerEntry> variableEntries;
    private HashMap<CXIdentifier, TypeTrackerEntry> globalVariableEntries;



    
    // global availability
    // these should copied
    private HashMap<CXIdentifier, TypeTrackerEntry> functionEntries;
    
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> publicMethodEntries;
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> publicFieldEntries;
    private HashSet<ConstructorKey> publicConstructors;
    
    // internal availability
    // these should be demoted
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> internalMethodEntries;
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> internalFieldEntries;
    private HashSet<ConstructorKey> internalConstructors;
    
    // private availability
    // these should be new for every class declaration
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> privateFieldEntries;
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> privateMethodEntries;
    private HashSet<ConstructorKey> privateConstructors;
    
    
    private static HashMap<CXClassType, VariableTypeTracker> classTrackers;
    private TypeEnvironment environment;
    
    static {
        classTrackers = new HashMap<>();
    }
    
    public VariableTypeTracker(TypeEnvironment environment, IdentifierResolver resolver) {
        this.environment = environment;
        this.resolver = resolver;
        variableEntries = new HashMap<>();
        globalVariableEntries = new HashMap<>();
        functionEntries = new HashMap<>();
        
        publicMethodEntries = new HashMap<>();
        publicFieldEntries = new HashMap<>();
        publicConstructors = new HashSet<>();
        
        internalFieldEntries = new HashMap<>();
        internalMethodEntries = new HashMap<>();
        internalConstructors = new HashSet<>();
        
        privateMethodEntries = new HashMap<>();
        privateFieldEntries = new HashMap<>();
        privateConstructors = new HashSet<>();
        
        trackingTypes = new HashSet<>();
    }
    
    private VariableTypeTracker(VariableTypeTracker old) {
        environment = old.environment;
        resolver = old.resolver;
        trackingTypes = new HashSet<>(old.trackingTypes);
        variableEntries = new HashMap<>();
        globalVariableEntries = old.globalVariableEntries;
        demoteEntries(variableEntries, old.variableEntries);
        
        functionEntries = old.functionEntries;
        
        publicMethodEntries = old.publicMethodEntries;
        publicFieldEntries = old.publicFieldEntries;
        publicConstructors = old.publicConstructors;
        
        internalFieldEntries = new HashMap<>();
        internalMethodEntries = new HashMap<>();
        demoteEntries(internalFieldEntries, old.internalFieldEntries);
        demoteEntries(internalMethodEntries, old.internalMethodEntries);
        internalConstructors = new HashSet<>(old.internalConstructors);
        
        
        
        privateMethodEntries = new HashMap<>();
        privateFieldEntries = new HashMap<>();
        demoteEntries(privateFieldEntries, old.privateFieldEntries);
        demoteEntries(privateMethodEntries, old.privateMethodEntries);
        privateConstructors = new HashSet<>(old.privateConstructors);
    }
    
    private VariableTypeTracker(VariableTypeTracker old, VariableTypeTracker old2) {
        environment = old.environment;
        resolver = old.resolver;
        trackingTypes = new HashSet<>(old.trackingTypes);
        trackingTypes.addAll(old2.trackingTypes);
        variableEntries = new HashMap<>();
        demoteEntries(variableEntries, old.variableEntries);
        demoteEntries(variableEntries, old2.variableEntries);
        globalVariableEntries = old.globalVariableEntries;
        
        functionEntries = old.functionEntries;
        functionEntries.putAll(old2.functionEntries);

        
        publicMethodEntries = old.publicMethodEntries;
        publicMethodEntries.putAll(old2.publicMethodEntries);
        publicFieldEntries = old.publicFieldEntries;
        publicFieldEntries.putAll(old2.publicFieldEntries);
        publicConstructors = old.publicConstructors;
        publicConstructors.addAll(old2.publicConstructors);
        
        internalFieldEntries = new HashMap<>();
        internalMethodEntries = new HashMap<>();
        demoteEntries(internalFieldEntries, old.internalFieldEntries);
        demoteEntries(internalMethodEntries, old.internalMethodEntries);
        demoteEntries(internalFieldEntries, old2.internalFieldEntries);
        demoteEntries(internalMethodEntries, old2.internalMethodEntries);
        internalConstructors = new HashSet<>(old.internalConstructors);
        internalConstructors.addAll(old2.internalConstructors);
        
        
        privateMethodEntries = new HashMap<>();
        privateFieldEntries = new HashMap<>();
        demoteEntries(privateFieldEntries, old.privateFieldEntries);
        demoteEntries(privateMethodEntries, old.privateMethodEntries);
        demoteEntries(privateFieldEntries, old2.privateFieldEntries);
        demoteEntries(privateMethodEntries, old2.privateMethodEntries);
        privateConstructors = new HashSet<>(old.privateConstructors);
        privateConstructors.addAll(old2.privateConstructors);
    }
    
    private VariableTypeTracker(VariableTypeTracker old, CXClassType parentType) {
        this(old);
        
        VariableTypeTracker variableTypeTracker = classTrackers.getOrDefault(parentType, null);
        if(variableTypeTracker == null) throw new ClassNotDefinedError();
        
        demoteEntries(internalFieldEntries, variableTypeTracker.internalFieldEntries);
        demoteEntries(internalMethodEntries, variableTypeTracker.internalMethodEntries);
        
        internalConstructors.addAll(variableTypeTracker.internalConstructors);
    }
    
    /**
     * Moves entries from old set, while also "demoting" them
     * @param entryHashMap the new map
     * @param oldMap the old map
     * @param <T> the key type
     */
    private static <T> void demoteEntries(HashMap<T, TypeTrackerEntry> entryHashMap, HashMap<T,
            TypeTrackerEntry> oldMap) {
        for (Map.Entry<T, TypeTrackerEntry> typeTrackerEntry : oldMap.entrySet()) {
            TypeTrackerEntry newEntry = new TypeTrackerEntry(typeTrackerEntry.getValue());
            if (newEntry.getStatus() == EntryStatus.NEW) {
                newEntry.status = EntryStatus.OLD;
            }
            entryHashMap.put(typeTrackerEntry.getKey(), newEntry);
        }
    }
    
    public VariableTypeTracker createInnerTypeTracker() {
        return new VariableTypeTracker(this);
    }
    
    public VariableTypeTracker createInnerTypeTracker(CXClassType owner) {
        VariableTypeTracker variableTypeTracker;
        if(owner.getParent() != null) {
            variableTypeTracker = new VariableTypeTracker(this, owner.getParent());
        } else {
            variableTypeTracker = new VariableTypeTracker(this);
        }
        //typeTracker.addEntry("this", new PointerType(owner));
        classTrackers.put(owner, variableTypeTracker);
        return variableTypeTracker;
    }
    
    public VariableTypeTracker createInnerTypeTrackerLoad(CXClassType owner) {
        return new VariableTypeTracker(this, classTrackers.getOrDefault(owner, new VariableTypeTracker(environment, this.resolver)));
    }

    @Override
    public boolean localVariableExists(String id) {
        return variableEntries.containsKey(id);
    }

    @Override
    public boolean globalVariableExists(CXIdentifier id) {
        Option<CXIdentifier> resolved = tryResolveIdentifier(id);
        if(resolved.isNone()) return false;
        return globalVariableEntries.containsKey(resolved.unwrap());
    }

    @Override
    public CXIdentifier resolveIdentifier(CXIdentifier id) {
        return resolver.resolvePath(id).unwrap();
    }


    @Override
    public Option<CXIdentifier> tryResolveIdentifier(CXIdentifier id) {
        return resolver.resolvePath(id);
    }

    @Override
    public Option<CXIdentifier> tryResolveFromName(String name) {
        return resolver.resolvePath(CXIdentifier.from(name));
    }

    @Override
    public CXType getLocalVariableType(String name) {
        TypeTrackerEntry variableEntry = getVariableEntry(name);
        if (variableEntry == null) {
            return null;
        }
        return variableEntry.getType();
    }

    @Override
    public CXFunctionPointer getFunctionType(CXIdentifier id) {
        TypeTrackerEntry variableEntry = getFunctionEntry(id);
        if (variableEntry == null) {
            return null;
        }
        return (CXFunctionPointer) variableEntry.getType();
    }

    @Override
    public CXType getGlobalVariableType(CXIdentifier id) {
        TypeTrackerEntry variableEntry = getGlobalVariableEntry(id);
        if (variableEntry == null) {
            return null;
        }
        return variableEntry.getType();
    }



    public boolean functionExists(CXIdentifier name) {
        Option<CXIdentifier> full = resolver.resolvePath(name);
        if(full.isNone()) return false;
        return functionEntries.containsKey(full.unwrap());
    }


    public boolean fieldVisible(CXCompoundType type, String name) {
        
        return isVisible(type, name, publicFieldEntries, internalFieldEntries, privateFieldEntries, null);
    }
    
    public boolean methodVisible(CXCompoundType type, String name, ParameterTypeList typeList) {
        return isVisible(type, name, publicMethodEntries, internalMethodEntries, privateMethodEntries, typeList);
    }
    
    public boolean isVisible(CXCompoundType type, String name, HashMap<CompoundDeclarationKey, TypeTrackerEntry> publicEntries, HashMap<CompoundDeclarationKey, TypeTrackerEntry> internalEntries, HashMap<CompoundDeclarationKey, TypeTrackerEntry> privateEntries, ParameterTypeList params) {
        CompoundDeclarationKey key;
        
        
        if(type instanceof CXClassType) {
            CXClassType cxClassType = (CXClassType) type;
            for (CXClassType inherit : cxClassType.getReverseInheritanceOrder()) {
                if(params == null) {
                    key =new CompoundDeclarationKey(inherit, name);
                } else {
                    key = new MethodKey(inherit, name, params);
                }
                if(publicEntries.containsKey(key) || internalEntries.containsKey(key) || privateEntries.containsKey(key)) return true;
            }
        } else {
            if(params == null) {
                key =new CompoundDeclarationKey(type, name);
            } else {
                key = new MethodKey(type, name, params);
            }
            return publicEntries.containsKey(key);
            
        }
        return false;
    }


    public CXIdentifier addGlobalVariable(CXIdentifier name, CXType type) {
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.NEW, type);
        return addGlobalVariable(name, typeTrackerEntry);
    }



    private TypeTrackerEntry getVariableEntry(String name) {
        if(variableEntries.containsKey(name)) return variableEntries.get(name);
        Reference<CXIdentifier> match = new Reference<>();
        if(resolver.resolvePath(CXIdentifier.from(name)).match(match)) {
            CXIdentifier full = match.getValue();
            if(!idExists(full)) return null;
            if(functionExists(full)) return functionEntries.get(full);
            return null;
        } else {
            return null;
        }
    }

    private TypeTrackerEntry getGlobalVariableEntry(CXIdentifier name) {
        Reference<CXIdentifier> match = new Reference<>();
        if(resolver.resolvePath(name).match(match)) {
            CXIdentifier full = match.getValue();
            if(!idExists(full)) return null;
            //if(functionExists(full)) return functionEntries.get(full);
            return globalVariableEntries.get(name);
        } else {
            return null;
        }
    }


    private TypeTrackerEntry getFunctionEntry(CXIdentifier name) {
        Reference<CXIdentifier> match = new Reference<>();
        if(resolver.resolvePath(name).match(match)) {
            CXIdentifier full = match.getValue();
            if(!idExists(full)) return null;
            if(functionExists(full)) return functionEntries.get(full);
            return null;
        } else {
            return null;
        }
    }


    public void addLocalVariable(String name, CXType type) {
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.NEW, type);
        addLocalVariable(name, typeTrackerEntry);
    }

    private void addLocalVariable(String name, TypeTrackerEntry typeTrackerEntry) {
        if(!localVariableExists(name)) {
            variableEntries.put(name, typeTrackerEntry);
        } else {
            TypeTrackerEntry oldEntry = getVariableEntry(name);
            assert oldEntry != null;
            if (oldEntry.getStatus() != EntryStatus.OLD) {
                throw new RedeclareError(name);
            }
            variableEntries.replace(name, typeTrackerEntry);
        }
    }

    private CXIdentifier addGlobalVariable(CXIdentifier name, TypeTrackerEntry typeTrackerEntry) {
        CXIdentifier full = resolver.createIdentity(name);
        if(!globalVariableExists(full)) {
            globalVariableEntries.put(full, typeTrackerEntry);
        } else {
            globalVariableEntries.replace(full, typeTrackerEntry);
        }
        return full;
    }



    public CXIdentifier addFunction(CXIdentifier name, CXFunctionPointer type, boolean isDefinition) {
        CXIdentifier full = resolver.createIdentity(name);
        ICompilationSettings.debugLog.fine("Added function " + full);
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.NEW, type.getReturnType());
        if(functionExists(full)) {
            if(isDefinition) {
                throw new RedeclareError(full);
            }
           else return full;
        }
        functionEntries.put(full, typeTrackerEntry);
        return full;
    }

    @Override
    public void addFunction(CXIdentifier name, CXType returnType, boolean isDefinition) {
        CXIdentifier full = resolver.createIdentity(name);
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.NEW, returnType);
        if(functionExists(full)) {
            if(isDefinition) {
                throw new RedeclareError(full);
            }
            else return;
        }
        functionEntries.put(full, typeTrackerEntry);
    }

    @Deprecated(forRemoval = true)
    public void addFixedFunction(CXIdentifier name, CXType type) {
        CXIdentifier full = resolver.createIdentity(name);
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.FIXED, type);
        if(functionExists(full)) throw new RedeclareError(full);
        functionEntries.put(full, typeTrackerEntry);
    }

    public void addCompoundTypeField(CXCompoundType parent, String name, CXType type, HashMap<CompoundDeclarationKey, TypeTrackerEntry> publicFieldEntries, HashMap<CompoundDeclarationKey, TypeTrackerEntry> publicMethodEntries) {
        CompoundDeclarationKey key = new CompoundDeclarationKey(parent, name);
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.NEW, type);
        
        if(fieldVisible(parent, name)) {
            throw new RedeclareError(name);
        }
        publicFieldEntries.put(key, typeTrackerEntry);
        
    }
    
    public Set<String> allMethodsAvailable() {
        Set<String> output = new HashSet<>();
        output.addAll(publicMethodEntries.keySet().stream().map(o -> o.name).collect(Collectors.toList()));
        output.addAll(internalMethodEntries.keySet().stream().map(o -> o.name).collect(Collectors.toList()));
        output.addAll(privateMethodEntries.keySet().stream().map(o -> o.name).collect(Collectors.toList()));
        return output;
    }
    
    public void addIsTracking(CXCompoundType type) {
        trackingTypes.add(type);
    }
    
    public boolean isTracking(CXCompoundType type) {
        return trackingTypes.contains(type);
    }
    
    public void addBasicCompoundType(CXCompoundType type) {
        if(type instanceof CXClassType) return;
        for (CXCompoundType.FieldDeclaration field : type.getFields()) {
            addPublicField(type, field.getName(), field.getType());
            if(field.getType() instanceof CXCompoundType) {
                CXCompoundType fieldType = (CXCompoundType) field.getType();
                if(!isTracking(fieldType)) {
                    addBasicCompoundType(fieldType);
                    addIsTracking(fieldType);
                }
            }
        }
    }
    
    public void removeParentlessStructFields() {
        HashSet<CompoundDeclarationKey> remove = new HashSet<>();
        for (CompoundDeclarationKey compoundDeclarationKey : publicFieldEntries.keySet()) {
            if(!(compoundDeclarationKey.type instanceof CXClassType) && !trackingTypes.contains(compoundDeclarationKey.type)) {
                remove.add(compoundDeclarationKey);
            }
        }
        for (CompoundDeclarationKey compoundDeclarationKey : remove) {
            publicFieldEntries.remove(compoundDeclarationKey);
        }
    }
    
    public void addPublicField(CXCompoundType parent, String name, CXType type) {
        addCompoundTypeField(parent, name, type, publicFieldEntries, publicMethodEntries);
    }
    
    public void addInternalField(CXClassType parent, String name, CXType type) {
        addCompoundTypeField(parent, name, type, internalFieldEntries, internalMethodEntries);
    }
    
    public void addPrivateField(CXClassType parent, String name, CXType type) {
        addCompoundTypeField(parent, name, type, privateFieldEntries, privateMethodEntries);
    }
    
    public void addPublicMethod(CXCompoundType parent, String name, CXType type, ParameterTypeList typeList) {
        addCompoundTypeMethodEntry(parent, name, type, typeList, publicMethodEntries);
    }
    
    public void addInternalMethod(CXClassType parent, String name, CXType type, ParameterTypeList typeList) {
        addCompoundTypeMethodEntry(parent, name, type, typeList, internalMethodEntries);
    }
    
    public void addPrivateMethod(CXClassType parent, String name, CXType type, ParameterTypeList typeList) {
        addCompoundTypeMethodEntry(parent, name, type, typeList, privateMethodEntries);
    }
    
    public void addConstructor(Visibility visibility, CXClassType owner, ParameterTypeList parameterTypeList) {
        ConstructorKey constructorKey = new ConstructorKey(owner, parameterTypeList);
        if(constructorVisible(owner, parameterTypeList)) throw new RedeclarationError("Constructor of type " + owner + " on parameters " + parameterTypeList);
        
        switch (visibility) {
            case _public:
                publicConstructors.add(constructorKey);
            return;
            case internal:
                internalConstructors.add(constructorKey);
                return;
            case _private:
                privateConstructors.add(constructorKey);
            
        }
    }
    
    public boolean constructorVisible(CXClassType owner, ParameterTypeList parameterTypeList) {
        ConstructorKey constructorKey = new ConstructorKey(owner, parameterTypeList);
        return publicConstructors.contains(constructorKey) || internalConstructors.contains(constructorKey) || privateConstructors.contains(constructorKey);
    }
    
    
    public void addCompoundTypeMethodEntry(CXCompoundType parent, String name, CXType type, ParameterTypeList typeList,
                                           HashMap<CompoundDeclarationKey, TypeTrackerEntry> methodEntries) {
        CompoundDeclarationKey key = new MethodKey(parent, name, typeList);
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.NEW, type);
        
        if(methodVisible(parent, name, typeList)) {
            assert parent instanceof CXClassType;
            System.out.println(((CXClassType) parent).classInfo());
            
            System.out.println("New: " +  typeList);
            throw new RedeclareError(name);
        }
        methodEntries.put(key, typeTrackerEntry);
        
    }
    
    public CXType getFieldType(CXCompoundType owner, String name) {
        if(!fieldVisible(owner, name)) return null;
        CompoundDeclarationKey key = new CompoundDeclarationKey(owner, name);
        if(publicFieldEntries.containsKey(key)) {
            return publicFieldEntries.get(key).getType();
        }
        if(internalFieldEntries.containsKey(key)) {
            return internalFieldEntries.get(key).getType();
        }
        if(privateFieldEntries.containsKey(key)) {
            return privateFieldEntries.get(key).getType();
        }
        return null;
    }
    
    public CXType getFieldType(CXClassType owner, String name) {
        for (CXClassType cxClassType : owner.getLineage()) {
            CXType fieldType = getFieldType((CXCompoundType) cxClassType, name);
            if(fieldType != null) return fieldType;
        }
        return null;
    }
    
    public CXType getMethodType(CXClassType owner, String name, ParameterTypeList typeList) {
        for (CXClassType cxClassType : owner.getReverseInheritanceOrder()) {
            if (!methodVisible(cxClassType, name, typeList)) continue;
            CompoundDeclarationKey key = new MethodKey(cxClassType, name, typeList);
            if (publicMethodEntries.containsKey(key)) {
                return publicMethodEntries.get(key).getType();
            }
            if (internalMethodEntries.containsKey(key)) {
                return internalMethodEntries.get(key).getType();
            }
            if (privateMethodEntries.containsKey(key)) {
                return privateMethodEntries.get(key).getType();
            }
        }
        return null;
    }

    public void enterNamespace(CXIdentifier namespace) {
        resolver.pushNamespace(namespace);
    }

    public void exitNamespace() {
        resolver.popNamespace();
    }

    public void useNamespace(CXIdentifier namespace) {
        resolver.useNamespace(namespace);
    }

    public void stopUseNamespace(CXIdentifier namespace) {
        resolver.stopUseNamespace(namespace);
    }
    
    public static VariableTypeTracker getTracker(CXClassType cxClassType) {
        return classTrackers.get(cxClassType);
    }
    
    public static boolean trackerPresent(CXClassType cxClassType) {
        return classTrackers.containsKey(cxClassType);
    }
}
