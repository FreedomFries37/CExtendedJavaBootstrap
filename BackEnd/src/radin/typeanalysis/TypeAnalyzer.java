package radin.typeanalysis;

import radin.interphase.AbstractCompilationError;
import radin.interphase.CompilationError;
import radin.interphase.ICompilationSettings;
import radin.interphase.lexical.Token;
import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.wrapped.ConstantType;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.methods.CXMethod;
import radin.typeanalysis.errors.RecoverableError;

import java.util.*;

public abstract class TypeAnalyzer implements ITypeAnalyzer{

   
    private Stack<TypeTracker> trackerStack;
    private TypeAugmentedSemanticNode tree;
    
    private static TypeEnvironment environment;
    private static ICompilationSettings compilationSettings;
    private static List<AbstractCompilationError> errors;
    
    private static HashMap<CXMethod, TypeAugmentedSemanticNode> methods;
    
    
    
    static {
        errors = new LinkedList<>();
        methods = new HashMap<>();
    }
    
    public static HashMap<CXMethod, TypeAugmentedSemanticNode> getMethods() {
        return methods;
    }
    
    public static ICompilationSettings getCompilationSettings() {
        return compilationSettings;
    }
    
    public static void setCompilationSettings(ICompilationSettings compilationSettings) {
        TypeAnalyzer.compilationSettings = compilationSettings;
    }
    
    public TypeAnalyzer(TypeAugmentedSemanticNode tree) {
        this.tree = tree;
        trackerStack = new Stack<>();
        
        if(environment != null && environment.typedefExists("boolean")) {
            trackerStack.push(new TypeTracker(environment));
            CXType booleanType = environment.getTypeDefinition("boolean");
            
            getCurrentTracker().addFixedFunction("true", new ConstantType(booleanType));
            getCurrentTracker().addFixedFunction("false", new ConstantType(booleanType));
        } else {
            trackerStack.push(new TypeTracker(new TypeEnvironment()));
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
        try {
            return determineTypes(tree);
        }catch (RecoverableError e) {
            Token closestToken = tree.findFailureToken();
            CompilationError error = new CompilationError(e, closestToken);
            errors.add(error);
            return true;
        }catch (AbstractCompilationError compilationError) {
            setIsFailurePoint(tree);
            errors.add(compilationError);
            return false;
        } catch (Error e) {
            setIsFailurePoint(tree);
            Token closestToken = tree.findFailureToken();
            CompilationError error = new CompilationError(e, closestToken);
            errors.add(error);
            return false;
        }
    }
    
    
    public static List<AbstractCompilationError> getErrors() {
        return errors;
    }
    
    
    
    /**
     * Checks if two types are equivalent, with const stripping for going from non-const to const
     * checks if type1 <= type2
     * @param o1 type1
     * @param o2 type2
     * @return whether they can be used
     */
    protected static boolean is(CXType o1, CXType o2) {
        return environment.is(o1, o2);
    }
    
    /**
     * Checks if two types are equivalent, with const stripping for going from non-const to const
     * checks if type1 <= type2
     * Strict primitive type checking
     * @param o1 type1
     * @param o2 type2
     * @return whether they can be used
     */
    protected static boolean isStrict(CXType o1, CXType o2) {
        return environment.isStrict(o1, o2);
    }
    
    @Override
    public abstract boolean determineTypes(TypeAugmentedSemanticNode node);
    
    public <T extends TypeAnalyzer> boolean determineTypes(T other) {
        ((TypeAnalyzer) other).trackerStack = trackerStack;
        return other.determineTypes();
    }
    
    protected void setIsFailurePoint(TypeAugmentedSemanticNode node) {
        node.setFailurePoint(true);
    }
}
