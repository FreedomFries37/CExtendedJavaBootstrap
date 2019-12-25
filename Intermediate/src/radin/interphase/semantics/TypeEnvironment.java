package radin.interphase.semantics;

import radin.interphase.semantics.exceptions.PrimitiveTypeDefinitionError;
import radin.interphase.semantics.exceptions.TypeDefinitionAlreadyExistsError;
import radin.interphase.semantics.exceptions.VoidTypeError;
import radin.interphase.semantics.types.compound.CXCompoundType;
import radin.interphase.semantics.types.CXType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class TypeEnvironment {
    
    private HashMap<String, CXType> typeDefinitions;
    private HashSet<CXCompoundType> namedCompoundTypes;
    private HashMap<String, CXCompoundType> namedCompoundTypesMap;
    
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
    
    public void addTypeDefinition(AbstractSyntaxNode type, String name) {
        if(primitives.contains(name)) throw new PrimitiveTypeDefinitionError(name);
        if(name.equals("void")) throw new VoidTypeError();
        if(typeDefinitions.containsKey(name)) throw new TypeDefinitionAlreadyExistsError(name);
        
        
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
        return namedCompoundTypesMap.get(name);
    }
}
