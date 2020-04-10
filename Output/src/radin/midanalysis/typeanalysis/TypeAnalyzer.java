package radin.midanalysis.typeanalysis;

import radin.core.chaining.IInPlaceCompilerAnalyzer;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.errorhandling.CompilationError;
import radin.core.errorhandling.RecoverableCompilationError;
import radin.core.lexical.Token;
import radin.midanalysis.ScopedTypeTracker;
import radin.midanalysis.typeanalysis.errors.MissingClassReferenceError;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.wrapped.ConstantType;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.core.utility.ICompilationSettings;
import radin.midanalysis.typeanalysis.errors.UndefinedError;

import java.util.*;

public abstract class TypeAnalyzer extends ScopedTypeTracker implements IInPlaceCompilerAnalyzer<TypeAugmentedSemanticNode> {
    
    
    private TypeAugmentedSemanticNode tree;
    
    private static ICompilationSettings<?,?,?> compilationSettings;
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
    
        @Override
        public TypeAugmentedSemanticNode invoke(TypeAugmentedSemanticNode input) {
            return null;
        }
    
        @Override
        public void reset() {
        
        }
    }
    
    @Override
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }
    
    public static TypeAnalyzer getInstance() {
        return new EmptyTypeAnalyzer(null);
    }
    
    
    static {
        
        methods = new HashMap<>();
    }
    
    @Override
    public <V> void setVariable(String variable, V value) {
        switch (variable) {
            case "environment": {
                setEnvironment((TypeEnvironment) value);
                break;
            }
        }
    }
    
    @Override
    public <V> V getVariable(String variable) {
        switch (variable) {
            case "environment": {
                return (V) getEnvironment();
            }
        }
        return null;
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
        this(tree, getEnvironment());
    }
    
    public TypeAnalyzer(TypeAugmentedSemanticNode tree, TypeEnvironment environment) {
        super();
        this.tree = tree;
        errors = new LinkedList<>();
        
        if(environment != null) {
            setEnvironment(environment);
        }
        
        if(getEnvironment() != null && getEnvironment().typedefExists("boolean")) {
            trackerStack.push(new VariableTypeTracker(getEnvironment()));
            CXType booleanType = getEnvironment().getTypeDefinition("boolean");
            
            getCurrentTracker().addFixedFunction("true", new ConstantType(booleanType));
            getCurrentTracker().addFixedFunction("false", new ConstantType(booleanType));
        } else {
            ICompilationSettings.debugLog.severe("Hopefully shouldn't reach here");
            trackerStack.push(new VariableTypeTracker(new TypeEnvironment()));
        }
    }
    
    @Override
    public void setHead(TypeAugmentedSemanticNode object) {
        tree = object;
    }
    
    @Override
    public TypeAugmentedSemanticNode invoke(TypeAugmentedSemanticNode input) {
        setHead(input);
        if(!determineTypes()) return null;
        return input;
    }
    
    @Override
    public boolean invoke() {
        return determineTypes();
    }
    
    public boolean determineTypes()  {
        try {
            return determineTypes(tree);
        }catch (RecoverableCompilationError e) {
            Token closestToken = tree.findFailureToken();
            CompilationError error = new CompilationError(e, closestToken);
            errors.add(error);
            ICompilationSettings.debugLog.throwing(getClass().getName(),  "TypeAnalyzer(TypeAugmentedSemanticNode " +
                    "tree)", e);
            // ICompilationSettings.debugLog.warning(error.getClass().getSimpleName() + ": " + error.getMessage());
    
            return true;
        }catch (AbstractCompilationError compilationError) {
            setIsFailurePoint(tree);
            errors.add(compilationError);
            //tree.printTreeForm();
            ICompilationSettings.debugLog.throwing(getClass().getName(),  "TypeAnalyzer(TypeAugmentedSemanticNode " +
                    "tree)", compilationError);
            // ICompilationSettings.debugLog.warning(compilationError.getClass().getSimpleName() + ": " +
            // compilationError.getMessage());
            return false;
        } catch (Error e) {
            setIsFailurePoint(tree);
            Token closestToken = tree.findFailureToken();
            CompilationError error = new CompilationError(e, closestToken);
            errors.add(error);
            ICompilationSettings.debugLog.throwing(getClass().getName(),  "TypeAnalyzer(TypeAugmentedSemanticNode " +
                    "tree)", e);
            // ICompilationSettings.debugLog.warning(e.getClass().getSimpleName() + ": " + e.getMessage());
    
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
    
    public <T extends TypeAnalyzer> boolean determineTypes(T other) throws MissingClassReferenceError {
        other.trackerStack = trackerStack;
        ((TypeAnalyzer) other).errors = errors;
        other.genericModule = genericModule;
        return other.determineTypes();
    }
    
    @Override
    public void clearErrors() {
        errors.clear();
    }
    
    @Override
    public void reset() {
        super.reset();
    }
   
    
    protected void setIsFailurePoint(TypeAugmentedSemanticNode node) {
        node.setFailurePoint(true);
        ICompilationSettings.debugLog.severe(node.findFirstToken().info());
        try {
            ICompilationSettings.debugLog.finest("\n" + node.toTreeForm());
        } catch (Error unused) {}
    }
}
