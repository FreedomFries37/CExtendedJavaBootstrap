package radin.typeanalysis;

import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.ConstantType;
import radin.interphase.semantics.types.compound.CXClassType;

import java.util.Stack;

public abstract class TypeAnalyzer implements ITypeAnalyzer{

   
    private Stack<TypeTracker> trackerStack;
    private TypeAugmentedSemanticNode tree;
    
    private static TypeEnvironment environment;
    
    public TypeAnalyzer(TypeAugmentedSemanticNode tree) {
        this.tree = tree;
        trackerStack = new Stack<>();
        trackerStack.push(new TypeTracker());
        if(environment != null && environment.typedefExists("boolean")) {
            CXType booleanType = environment.getTypeDefinition("boolean");
            
            getCurrentTracker().addFixedFunction("true", new ConstantType(booleanType));
            getCurrentTracker().addFixedFunction("false", new ConstantType(booleanType));
        }
    }
    
    public static TypeEnvironment getEnvironment() {
        return environment;
    }
    
    public static void setEnvironment(TypeEnvironment environment) {
        TypeAnalyzer.environment = environment;
    }
    
    @Override
    public TypeTracker getCurrentTracker() {
        return trackerStack.peek();
    }
    
    @Override
    public void typeTrackingClosure() {
        TypeTracker next = trackerStack.peek().createInnerTypeTracker();
        trackerStack.push(next);
    }
    
    @Override
    public void typeTrackingClosure(CXClassType classType) {
        TypeTracker next = trackerStack.peek().createInnerTypeTracker(classType);
        trackerStack.push(next);
    }
    
    @Override
    public void releaseTrackingClosure() {
        trackerStack.pop();
        getCurrentTracker().removeParentlessStructFields();
    }
    
    @Override
    public boolean determineTypes() {
        return determineTypes(tree);
    }
    
    
    /**
     * Checks if two types are equivalent, with const stripping for going from non-const to const
     * checks if type1 <= type2
     * @param o1 type1
     * @param o2 type2
     * @return whether they can be used
     */
    protected static boolean is(CXType o1, CXType o2) {
        
        if(!(o1 instanceof ConstantType) && o2 instanceof ConstantType) {
            return o1.is(((ConstantType) o2).getSubtype(), getEnvironment());
        }
        return o1.is(o2, getEnvironment());
    }
    
    @Override
    public abstract boolean determineTypes(TypeAugmentedSemanticNode node);
    
    public <T extends TypeAnalyzer> boolean determineTypes(T other) {
        ((TypeAnalyzer) other).trackerStack = trackerStack;
        return other.determineTypes();
    }
}
