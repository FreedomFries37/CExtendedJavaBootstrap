package radin.core.annotations;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.errorhandling.ICompilationErrorCollector;
import radin.core.utility.Pair;
import radin.core.lexical.Token;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AnnotationManager<T> implements ICompilationErrorCollector {
    @FunctionalInterface
    public interface Command {
        void execute(Object... args);
    }
    
    @FunctionalInterface
    public interface TargetCommand<T> {
        void execute(T target, Object... args);
    }
    
    @FunctionalInterface
    public interface TargetCommandNoArgs<T> extends TargetCommand<T> {
        void execute(T target);
    
        @Override
        default void execute(T target, Object... args) {
            execute(target);
        }
    }
    
    private List<AbstractCompilationError> errors = new LinkedList<>();
    private HashMap<String, Command> functions = new HashMap<>();
    private HashMap<String, TargetCommand<T>> targetedFunctions = new HashMap<>();
    
    
    public AnnotationManager(List<Pair<String, Command>> methods,
                             List<Pair<String, ? extends TargetCommand<T>>> targetedMethods) {
        for (Pair<String, Command> method : methods) {
            functions.put(method.getVal1(), method.getVal2());
        }
        for (Pair<String, ? extends TargetCommand<T>> targetedMethod : targetedMethods) {
            targetedFunctions.put(targetedMethod.getVal1(), targetedMethod.getVal2());
        }
    }
    
    @SafeVarargs
    public AnnotationManager(List<Pair<String, ? extends TargetCommand<T>>> targetedMethods,
                             Pair<String, Command>... methods) {
        for (Pair<String, Command> method : methods) {
            functions.put(method.getVal1(), method.getVal2());
        }
        for (Pair<String, ? extends TargetCommand<T>> targetedMethod : targetedMethods) {
            targetedFunctions.put(targetedMethod.getVal1(), targetedMethod.getVal2());
        }
    }
    
    @SafeVarargs
    public static <T> AnnotationManager<T> createTargeted(Pair<String, ? extends TargetCommand<T>>... targetedMethods) {
        return new AnnotationManager<>(new LinkedList<>(), Arrays.asList(targetedMethods));
    }
    
    @SafeVarargs
    public static <T> AnnotationManager<T> create(Pair<String, Command>... methods) {
        return new AnnotationManager<>(new LinkedList<>(), methods);
    }
    
    
    public void invokeAnnotation(Token id) {
        invokeAnnotation(id, new Object[0]);
    }
    
    public void invokeAnnotation(Token id, Object[] objects) {
        String image = id.getImage();
        if(!functions.containsKey(image)) return;
        functions.get(image).execute(objects);
    }
    
    public void invokeAnnotation(Token id, T target, Object[] objects) {
        String image = id.getImage();
        if(!targetedFunctions.containsKey(image)) return;
        targetedFunctions.get(image).execute(target, objects);
    }
    
    public void invokeAnnotation(Token id, T target) {
        String image = id.getImage();
        if(!targetedFunctions.containsKey(image)) return;
        targetedFunctions.get(image).execute(target);
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return errors;
    }
}
