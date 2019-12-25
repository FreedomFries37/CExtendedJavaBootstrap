package radin.interphase.semantics.types.compound;

import radin.interphase.semantics.exceptions.IncorrectParameterTypesError;
import radin.interphase.semantics.exceptions.RedeclareError;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.Visibility;
import radin.interphase.semantics.types.methods.CXMethod;

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
    
    
   
    
    public CXClassType(String typename, CXClassType parent, List<ClassFieldDeclaration> declarations,
                       List<CXMethod> methods) {
        super(typename, new LinkedList<>(declarations));
        if(parent != null) {
            this.virtualMethodOrder = new LinkedList<>(parent.virtualMethodOrder);
            visibilityMap = new HashMap<>(parent.visibilityMap);
        } else {
            this.virtualMethodOrder = new LinkedList<>();
            visibilityMap = new HashMap<>();
        }
        concreteMethodsOrder = new LinkedList<>();
        
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
                
                if(isExistingVirtualMethod(method.getName())) {
                    for (int i = 0; i < virtualMethodOrder.size(); i++) {
                        CXMethod cxMethod = virtualMethodOrder.get(i);
                        if(cxMethod.getName().equals(method.getName())) {
                            if(cxMethod.getParameterTypes().equals(method.getParameterTypes())) {
                                throw new IncorrectParameterTypesError();
                            }
                            
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
    
    public CXStructType getVTable() {
    
    }
    
    public CXStructType getStructEquivalent() {
        List<FieldDeclaration> fieldDeclarations = new LinkedList<>();
    
        for (CXClassType cxClass : getLineage()) {
            fieldDeclarations.addAll(cxClass.getFields());
            
        }
    }
    
    public CXClassType(String typename, List<ClassFieldDeclaration> declarations,
                       List<CXMethod> methods) {
        this(typename, null, declarations, methods);
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
    
    private boolean isAlreadyDefined(String name) {
        if(parent != null) {
            if(parent.isAlreadyDefined(name)) return false;
        }
        return visibilityMap.containsKey(name);
    }
    
}
