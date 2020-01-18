package radin.core.output.typeanalysis;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.errorhandling.CompilationError;
import radin.core.errorhandling.ICompilationErrorCollector;
import radin.core.errorhandling.RecoverableCompilationError;
import radin.core.lexical.Token;
import radin.core.output.midanalysis.ScopedTypeTracker;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.wrapped.ConstantType;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.utility.ICompilationSettings;

import java.util.*;

public abstract class TypeAnalyzer extends ScopedTypeTracker implements ICompilationErrorCollector {
    
    
    private TypeAugmentedSemanticNode tree;
    
    private static ICompilationSettings compilationSettings;
    private List<AbstractCompilationError> errors;
    
    private static HashMap<CXMethod, TypeAugmentedSemanticNode> methods;
    
    private static class EmptyTypeAnalyzer extends TypeAnalyzer{
    
        public EmptyTypeAnalyzer(TypeAugmentedSemanticNode tree) {
            super(tree);
        }
    
        @Override
        public boolean determineTypes(TypeAugmentedSemanticNode node) {
            return false;
        }
    }
    
    public static TypeAnalyzer getInstance() {
        return new EmptyTypeAnalyzer(null);
    }
    
    
    static {
        
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
        super();
        this.tree = tree;
        errors = new LinkedList<>();
        
        if(getEnvironment() != null && getEnvironment().typedefExists("boolean")) {
            trackerStack.push(new VariableTypeTracker(getEnvironment()));
            CXType booleanType = getEnvironment().getTypeDefinition("boolean");
            
            getCurrentTracker().addFixedFunction("true", new ConstantType(booleanType));
            getCurrentTracker().addFixedFunction("false", new ConstantType(booleanType));
        } else {
            trackerStack.push(new VariableTypeTracker(new TypeEnvironment()));
        }
    }
    
    
    
    public boolean determineTypes() {
        try {
            return determineTypes(tree);
        }catch (RecoverableCompilationError e) {
            Token closestToken = tree.findFailureToken();
            CompilationError error = new CompilationError(e, closestToken);
            errors.add(error);
            return true;
        }catch (AbstractCompilationError compilationError) {
            setIsFailurePoint(tree);
            errors.add(compilationError);
            //tree.printTreeForm();
            return false;
        } catch (Error e) {
            setIsFailurePoint(tree);
            Token closestToken = tree.findFailureToken();
            CompilationError error = new CompilationError(e.getMessage(), closestToken);
            errors.add(error);
            //tree.printTreeForm();
            return false;
        }
    }
    
    
    public List<AbstractCompilationError> getTypeErrors() {
        return errors;
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return getTypeErrors();
    }
    
    
    /**
     * Checks if two types are equivalent, with const stripping for going from non-const to const
     * checks if type1 <= type2
     * @param o1 type1
     * @param o2 type2
     * @return whether they can be used
     */
    protected static boolean is(CXType o1, CXType o2) {
        return getEnvironment().is(o1, o2);
    }
    
    /**
     * Checks if two types are equivalent, with const stripping for going from non-const to const
     * checks if type1 <= type2
     * Strict primitive type checking
     * @param o1 type1
     * @param o2 type2
     * @return whether they can be used
     */
    protected static boolean strictIs(CXType o1, CXType o2) {
        return getEnvironment().isStrict(o1, o2);
    }
    
    public abstract boolean determineTypes(TypeAugmentedSemanticNode node);
    
    public <T extends TypeAnalyzer> boolean determineTypes(T other) {
        ((TypeAnalyzer) other).trackerStack = trackerStack;
        ((TypeAnalyzer) other).errors = errors;
        return other.determineTypes();
    }
    
    
    
    protected void setIsFailurePoint(TypeAugmentedSemanticNode node) {
        node.setFailurePoint(true);
    }
}
