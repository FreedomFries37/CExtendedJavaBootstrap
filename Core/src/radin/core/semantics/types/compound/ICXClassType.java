package radin.core.semantics.types.compound;

import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.Visibility;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.utility.Reference;

import java.util.List;

public interface ICXClassType extends ICXCompoundType, CXCallable {
    
    CXType getAsCXType();
    
    TypeEnvironment getEnvironment();
    
    void addConstructors(List<CXConstructor> constructors);
    
    void setEnvironment(TypeEnvironment environment);
    
    CXStructType getVTable();
    
    CXMethod getInitMethod();
    
    List<CXMethod> getVirtualMethodsOrder();
    
    List<CXMethod> getConcreteMethodsOrder();
    
    List<CXConstructor> getConstructors();
    
    CXConstructor getConstructor(List<CXType> parameters, TypeEnvironment environment);
    
    CXConstructor getConstructor(int length);
    
    CXConstructor getConstructor(ParameterTypeList parameterTypeList);
    
    void generateSuperMethods(String vtablename);
    
    CXMethod getSuperMethod(String name, ParameterTypeList typeList);
    
    boolean isVirtual(Token name, ParameterTypeList typeList);
    
    CXStructType getStructEquivalent();
    
    List<CXMethod> getGeneratedSupers();
    
    boolean canInstantiateDirectly();
    
    class ClassFieldDeclaration extends FieldDeclaration {
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
