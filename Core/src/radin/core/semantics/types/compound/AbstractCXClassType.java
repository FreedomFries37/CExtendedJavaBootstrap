package radin.core.semantics.types.compound;

import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.Visibility;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.utility.Reference;

import java.util.List;

public abstract class AbstractCXClassType extends CXCompoundType implements CXCallable {
    
    public AbstractCXClassType(List<FieldDeclaration> fields) {
        super(fields);
    }
    
    public AbstractCXClassType(FieldDeclaration f1, FieldDeclaration... fields) {
        super(f1, fields);
    }
    
    public AbstractCXClassType(CXIdentifier identifier, List<FieldDeclaration> fields) {
        super(identifier, fields);
    }
    
    public AbstractCXClassType(CXIdentifier identifier, FieldDeclaration f1, FieldDeclaration... fields) {
        super(identifier, f1, fields);
    }
    
    public abstract CXType getAsCXType();
    
    public abstract TypeEnvironment getEnvironment();
    
    public abstract void addConstructors(List<CXConstructor> constructors);
    
    public abstract void setEnvironment(TypeEnvironment environment);
    
    public abstract CXStructType getVTable();
    
    public abstract CXMethod getInitMethod();
    
    public abstract List<CXMethod> getVirtualMethodsOrder();
    
    public abstract List<CXMethod> getConcreteMethodsOrder();
    
    public abstract List<CXConstructor> getConstructors();
    
    public abstract CXConstructor getConstructor(List<CXType> parameters, TypeEnvironment environment);
    
    public abstract CXConstructor getConstructor(int length);
    
    public abstract CXConstructor getConstructor(ParameterTypeList parameterTypeList);
    
    public abstract void generateSuperMethods(String vtablename);
    
    public abstract CXMethod getSuperMethod(String name, ParameterTypeList typeList);
    
    public abstract boolean isVirtual(Token name, ParameterTypeList typeList);
    
    public abstract CXStructType getStructEquivalent();
    
    public abstract List<CXMethod> getGeneratedSupers();
    
    public abstract boolean canInstantiateDirectly();
    
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
}
