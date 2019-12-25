package radin.interphase.semantics;

import jdk.jshell.spi.ExecutionControl;
import radin.interphase.semantics.exceptions.InvalidPrimitiveException;
import radin.interphase.semantics.exceptions.PrimitiveTypeDefinitionError;
import radin.interphase.semantics.exceptions.TypeDefinitionAlreadyExistsError;
import radin.interphase.semantics.exceptions.VoidTypeError;
import radin.interphase.semantics.types.ConstantType;
import radin.interphase.semantics.types.PointerType;
import radin.interphase.semantics.types.compound.CXCompoundType;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;
import radin.interphase.semantics.types.primitives.LongPrimitive;
import radin.interphase.semantics.types.primitives.UnsignedPrimitive;

import javax.lang.model.type.PrimitiveType;
import java.util.*;

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
    
    public CXType addTypeDefinition(AbstractSyntaxNode typeAST, String name) throws InvalidPrimitiveException{
        if(primitives.contains(name)) throw new PrimitiveTypeDefinitionError(name);
        if(name.equals("void")) throw new VoidTypeError();
        if(typeDefinitions.containsKey(name)) throw new TypeDefinitionAlreadyExistsError(name);
    
        CXType type = getType(typeAST);
        typeDefinitions.put(name, type);
        return type;
    }
    
    public CXType getType(AbstractSyntaxNode ast) throws InvalidPrimitiveException {
        System.out.println("Getting type for:");
        ast.printTreeForm();
        if(ast.getType().equals(ASTNodeType.typename)) {
            return typeDefinitions.get(ast.getToken().getImage());
        }
        
        if(ast.getType().equals(ASTNodeType.pointer_type)) {
            return new PointerType(getType(ast.getChild(0)));
        }
        
        if(ast.getType().equals(ASTNodeType.qualifiers_and_specifiers)) {
            List<AbstractSyntaxNode> specifiers = ast.getChildren(ASTNodeType.specifier);
            specifiers.sort(new SpecifierComparator());
            CXType type = null;
            for (AbstractSyntaxNode specifier : specifiers) {
                if(type == null) {
                    type = getType(specifier);
                }else if(isModifier(getSpecifier(specifier))) {
                    switch (getSpecifier(specifier)) {
                        case "unsigned": {
                            assert type instanceof CXPrimitiveType;
                            type = new UnsignedPrimitive((CXPrimitiveType) type);
                            break;
                        }
                        case "long": {
                            assert type instanceof CXPrimitiveType;
                            type = new LongPrimitive((CXPrimitiveType) type);
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
                // TODO: implement compound type creation
            } else if(ast.hasChild(ASTNodeType.basic_compound_type_reference)) {
                AbstractSyntaxNode name = ast.getChild(ASTNodeType.basic_compound_type_reference).getChild(ASTNodeType.id);
                return getNamedCompoundType(name.getToken().getImage());
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
        
        throw new UnsupportedOperationException();
    }
    
    private String getSpecifier(AbstractSyntaxNode node) {
        String o1Specifier = node.getToken().getImage();
        if(o1Specifier == null) o1Specifier = node.getToken().getType().toString();
        return o1Specifier;
    }
    
    private CXCompoundType createType(AbstractSyntaxNode ast) {
        
        throw new UnsupportedOperationException();
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
            if(isModifier(name)) return 2;
            return 0;
        }
        
        
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
        return namedCompoundTypesMap.get(name);
    }
}
