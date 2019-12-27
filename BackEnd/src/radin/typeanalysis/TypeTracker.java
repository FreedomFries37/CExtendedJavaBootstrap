package radin.typeanalysis;

import radin.interphase.semantics.exceptions.RedeclareError;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.PointerType;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.compound.CXCompoundType;
import radin.typeanalysis.errors.ClassNotDefinedError;
import radin.typeanalysis.errors.IdentifierDoesNotExistError;

import java.util.HashMap;
import java.util.Map;

public class TypeTracker {

    public enum EntryStatus {
        OLD,
        NEW
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
    
    
    // lexical variables
    // these should be demoted
    private HashMap<String, TypeTrackerEntry> variableEntries;
    
    // global availability
    // these should copied
    private HashMap<String, TypeTrackerEntry> functionEntries;
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> publicMethodEntries;
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> publicFieldEntries;
    
    // internal availability
    // these should be demoted
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> internalMethodEntries;
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> internalFieldEntries;
    
    // private availability
    // these should be new for every class declaration
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> privateFieldEntries;
    private HashMap<CompoundDeclarationKey, TypeTrackerEntry> privateMethodEntries;
    
    
    private static HashMap<CXClassType, TypeTracker> classTrackers;
    
    static {
        classTrackers = new HashMap<>();
    }
    
    public TypeTracker() {
        variableEntries = new HashMap<>();
        functionEntries = new HashMap<>();
        publicMethodEntries = new HashMap<>();
        publicFieldEntries = new HashMap<>();
        internalFieldEntries = new HashMap<>();
        internalMethodEntries = new HashMap<>();
        privateMethodEntries = new HashMap<>();
        privateFieldEntries = new HashMap<>();
    }
    
    private TypeTracker(TypeTracker old) {
        variableEntries = new HashMap<>();
        demoteEntries(variableEntries, old.variableEntries);
        
        functionEntries = old.functionEntries;
        
        publicMethodEntries = old.publicMethodEntries;
        publicFieldEntries = old.publicFieldEntries;
    
        
        internalFieldEntries = new HashMap<>();
        internalMethodEntries = new HashMap<>();
        demoteEntries(internalFieldEntries, old.internalFieldEntries);
        demoteEntries(internalMethodEntries, old.internalMethodEntries);
        
        privateMethodEntries = new HashMap<>();
        privateFieldEntries = new HashMap<>();
        demoteEntries(privateFieldEntries, old.privateFieldEntries);
        demoteEntries(privateFieldEntries, old.privateMethodEntries);
        
    }
    
    private TypeTracker(TypeTracker old, CXClassType parentType) {
        this(old);
        
        TypeTracker typeTracker = classTrackers.getOrDefault(parentType, null);
        if(typeTracker == null) throw new ClassNotDefinedError();
    
        demoteEntries(internalFieldEntries, typeTracker.internalFieldEntries);
        demoteEntries(internalMethodEntries, typeTracker.internalMethodEntries);
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
    
    public TypeTracker createInnerTypeTracker() {
        return new TypeTracker(this);
    }
    
    public TypeTracker createInnerTypeTracker(CXClassType owner) {
        TypeTracker typeTracker;
        if(owner.getParent() != null) {
            typeTracker = new TypeTracker(this, owner.getParent());
        } else {
            typeTracker = new TypeTracker(this);
        }
        //typeTracker.addEntry("this", new PointerType(owner));
        classTrackers.put(owner, typeTracker);
        return typeTracker;
    }
    
    public boolean entryExists(String name) {
        return variableEntries.containsKey(name);
    }
    
    public boolean functionExists(String name) {
        return functionEntries.containsKey(name);
    }
    
    public boolean fieldVisible(CXCompoundType type, String name) {
        
        return isVisible(type, name, publicFieldEntries, internalFieldEntries, privateFieldEntries);
    }
    
    public boolean methodVisible(CXCompoundType type, String name) {
        return isVisible(type, name, publicMethodEntries, internalMethodEntries, privateMethodEntries);
    }
    
    public boolean isVisible(CXCompoundType type, String name, HashMap<CompoundDeclarationKey, TypeTrackerEntry> publicMethodEntries, HashMap<CompoundDeclarationKey, TypeTrackerEntry> internalMethodEntries, HashMap<CompoundDeclarationKey, TypeTrackerEntry> privateMethodEntries) {
        CompoundDeclarationKey key = new CompoundDeclarationKey(type, name);
        if(publicMethodEntries.containsKey(key)) return true;
        if(type instanceof CXClassType) {
            if(internalMethodEntries.containsKey(key)) return true;
            return privateMethodEntries.containsKey(key);
        }
        return false;
    }
    
    public void addEntry(String name, CXType type) {
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.NEW, type);
        if(!entryExists(name)) {
            variableEntries.put(name, typeTrackerEntry);
        } else {
            TypeTrackerEntry oldEntry = variableEntries.get(name);
            if(oldEntry.getStatus() == EntryStatus.NEW) {
                throw new RedeclareError(name);
            }
            
            variableEntries.replace(name, typeTrackerEntry);
        }
    }
    
    public void addFunction(String name, CXType type) {
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.NEW, type);
        if(functionExists(name)) throw new RedeclareError(name);
        functionEntries.put(name, typeTrackerEntry);
    }
    
    private void putEntry(String name, CXType type) {
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.NEW, type);
        variableEntries.put(name, typeTrackerEntry);
    }
    
    public void addCompoundTypeEntry(CXCompoundType parent, boolean isField, String name, CXType type, HashMap<CompoundDeclarationKey, TypeTrackerEntry> publicFieldEntries, HashMap<CompoundDeclarationKey, TypeTrackerEntry> publicMethodEntries) {
        CompoundDeclarationKey key = new CompoundDeclarationKey(parent, name);
        TypeTrackerEntry typeTrackerEntry = new TypeTrackerEntry(EntryStatus.NEW, type);
        if(isField) {
            if(fieldVisible(parent, name)) {
                throw new RedeclareError(name);
            }
            publicFieldEntries.put(key, typeTrackerEntry);
        } else {
            if(methodVisible(parent, name)) {
                throw new RedeclareError(name);
            }
            publicMethodEntries.put(key, typeTrackerEntry);
        }
    }
    
    public void addPublic(CXCompoundType parent, boolean isField, String name, CXType type) {
        addCompoundTypeEntry(parent, isField, name, type, publicFieldEntries, publicMethodEntries);
    }
    
    public void addInternal(CXClassType parent, boolean isField, String name, CXType type) {
        addCompoundTypeEntry(parent, isField, name, type, internalFieldEntries, internalMethodEntries);
    }
    
    public void addPrivate(CXClassType parent, boolean isField, String name, CXType type) {
        addCompoundTypeEntry(parent, isField, name, type, privateFieldEntries, privateMethodEntries);
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
    
    public CXType getMethodType(CXClassType owner, String name) {
        for (CXClassType cxClassType : owner.getReverseInheritanceOrder()) {
            if (!methodVisible(cxClassType, name)) continue;
            CompoundDeclarationKey key = new CompoundDeclarationKey(cxClassType, name);
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
    
   
    public CXType getType(String name) {
        if(!entryExists(name)) {
            if(functionExists(name)) {
                return functionEntries.get(name).getType();
            }
            throw new IdentifierDoesNotExistError(name);
        }
        return variableEntries.get(name).getType();
    }
}
