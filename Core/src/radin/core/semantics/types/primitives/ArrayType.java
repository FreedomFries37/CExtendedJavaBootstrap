package radin.core.semantics.types.primitives;

import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.generics.CXParameterizedType;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.ICXWrapper;

import java.util.LinkedList;
import java.util.List;

public class ArrayType extends AbstractCXPrimitiveType {

    private CXType baseType;
    private AbstractSyntaxNode size;
    
    public ArrayType(CXType baseType) {
        this.baseType = baseType;
        this.size = null;
    }
    
    public ArrayType(CXType baseType, AbstractSyntaxNode size) {
        this.baseType = baseType;
        this.size = size;
    }
    
    public CXType getBaseType() {
        return baseType;
    }
    
    
    public AbstractSyntaxNode getSize() {
        return size;
    }
    
    @Override
    public String generateCDefinition() {
        return baseType.generateCDefinition() + "[]";
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        if(baseType instanceof ArrayType || baseType instanceof ICXWrapper && ((ICXWrapper) baseType).getWrappedType() instanceof ArrayType) {
            ArrayType next;
            if(baseType instanceof ArrayType) {
                next = ((ArrayType) baseType);
            } else {
                next = ((ArrayType) ((ICXWrapper) baseType).getWrappedType());
            }
            if(size != null) {
                return this.generateCDefinition(next, identifier, 0);
            }
            return this.generateCDefinition(next, identifier, 0);
        }
        
        if(size != null) {
            return baseType.generateCDeclaration() + " " + identifier + "[" + "$REPLACE ME$" + "]";
        }
        return baseType.generateCDeclaration() + " " + identifier + "[]";
    }
    
    @Override
    public String toString() {
        if(size == null) return baseType.toString() + "[]";
        return baseType.toString() + "[" + size.toTreeForm().replaceAll("\\s+", " ");
    }
    
    @Override
    public String ASTableDeclaration() {
        if(size == null) return baseType.ASTableDeclaration() + "[]";
        return baseType.ASTableDeclaration() + "[" + size.toTreeForm().replaceAll("\\s+", " ");
    }
    
    /**
     *
     * @param identifier
     * @param nthDimension the dimension of the array
     * @return
     */
    private String generateCDefinition(ArrayType baseType, String identifier, int nthDimension) {
        String replaceMe = String.format("$REPLACE ME %d$", nthDimension);
        
        if(baseType.baseType instanceof ArrayType || baseType.baseType instanceof ICXWrapper && ((ICXWrapper) baseType.baseType).getWrappedType() instanceof ArrayType) {
            if(size != null) {
                return baseType.generateCDefinition((ArrayType) baseType.baseType, identifier, nthDimension + 1) +
                        "[" + replaceMe + "]";
            }
            return  baseType.generateCDefinition((ArrayType) baseType.baseType, identifier, nthDimension) + "[]";
        }
        
        if(size != null) {
            return baseType.generateCDefinition(identifier, nthDimension + 1) + "[" + replaceMe+ "]";
        }
        return baseType.generateCDefinition(identifier, nthDimension) + "[]";
    }
    
    private String generateCDefinition(String identifier, int nthDimension) {
        if(size != null) {
            return baseType.generateCDefinition() + " " + identifier + "[" + "$REPLACE ME " + nthDimension +"$" + "]";
        }
        return baseType.generateCDefinition() + " " + identifier + "[]";
    }
    
    @Override
    public boolean isIntegral() {
        return false;
    }
    
    @Override
    public CXType getTypeRedirection(TypeEnvironment e) {
        return new ArrayType(baseType.getTypeRedirection(e), size);
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return false;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return 0;
    }
    
   
    public List<AbstractSyntaxNode> getSizes() {
        LinkedList<AbstractSyntaxNode> output = new LinkedList<>();
        if(size != null) output.add(size);
        if(baseType instanceof ArrayType || baseType instanceof ICXWrapper && ((ICXWrapper) baseType).getWrappedType() instanceof ArrayType) {
            if(baseType instanceof ArrayType) {
                output.addAll(((ArrayType) baseType).getSizes());
            } else {
                output.addAll(((ArrayType) ((ICXWrapper) baseType).getWrappedType()).getSizes());
            }
        }
        
        return output;
    }
    
    public int getDimensions() {
        int output = 1;
        if(baseType instanceof ArrayType || baseType instanceof ICXWrapper && ((ICXWrapper) baseType).getWrappedType() instanceof ArrayType) {
            if(baseType instanceof ArrayType) {
                output += ((ArrayType) baseType).getDimensions();
            } else {
                output += ((ArrayType) ((ICXWrapper) baseType).getWrappedType()).getDimensions();
            }
        }
        return output;
    }
    
    /**
     * Creates a modified version of the C Declaration that matches the pattern {@code \W+}
     *
     * @return Such a string
     */
    @Override
    public String getSafeTypeString() {
        return baseType.getSafeTypeString() + "_a";
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(!(other instanceof ArrayType || other instanceof PointerType)) {
            return false;
        }
        CXType baseType;
        if(other instanceof ArrayType) {
            baseType = ((ArrayType) other).baseType;
        }else {
            baseType = ((PointerType) other).getSubType();
        }
        return e.is(this.baseType, baseType);
        //return this.baseType.is(baseType, e);
    }
    
    @Override
    public CXType propagateGenericReplacement(CXParameterizedType original, CXType replacement) {
        return new ArrayType(baseType.propagateGenericReplacement(original, replacement), size);
    }
}
