package radin.core.output.combo;

import radin.core.ErrorReader;
import radin.core.ICompilationMapper;
import radin.core.IFrontEndUnit;
import radin.core.chaining.IToolChain;
import radin.core.chaining.ToolChainFactory;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.errorhandling.CompilationError;
import radin.core.errorhandling.ICompilationErrorCollector;
import radin.core.output.backend.compilation.FileCompiler;
import radin.core.output.midanalysis.ScopedTypeTracker;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.utility.ICompilationSettings;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MultipleFileHandler implements ICompilationErrorCollector {
    
    
    
    
    private List<AbstractCompilationError> fullErrors;
    private boolean stateChanged = true;
    
    private enum CompilationResult {
        ErroredOut,
        Completed,
        Failed
    }
    
    private final long compileStartTime;
    
    
    private class CompilationNode implements ICompilationErrorCollector {
        private String file;
        private AbstractSyntaxNode astTree;
        private TypeAugmentedSemanticNode typedTree;
        private String inputString;
        private long lastCompileAttemptTime;
        
        private IToolChain<? super AbstractSyntaxNode, ? extends TypeAugmentedSemanticNode> midToolChain;
        private IToolChain<? super TypeAugmentedSemanticNode, Boolean> backEndToolChain;
        private IFrontEndUnit<? extends AbstractSyntaxNode> frontEndUnit;
        
        private boolean isCompleted;
        private List<AbstractCompilationError> errors;
        
        private TypeEnvironment environment;
        
        public CompilationNode(File f, IFrontEndUnit<? extends AbstractSyntaxNode> astProducer, IToolChain<?
                super AbstractSyntaxNode, ? extends TypeAugmentedSemanticNode> midToolChain, IToolChain<?
                super TypeAugmentedSemanticNode, Boolean> backEndToolChain) {
            file = f.getPath();
            this.frontEndUnit = astProducer;
            this.midToolChain = midToolChain;
            this.environment = astProducer.getEnvironment();
            this.backEndToolChain = backEndToolChain;
            errors = new LinkedList<>();
        }
        
        public String getInputString() {
            return inputString;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompilationNode that = (CompilationNode) o;
            return file.equals(that.file);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(file);
        }
        
        public CompilationResult attemptCompile() {
            try {
                if(astTree == null) {
                    ICompilationSettings.debugLog.finer("Creating AST for " + file);
                    astTree = frontEndUnit.invoke();
                    inputString = frontEndUnit.getUsedString();
                    if (frontEndUnit.hasErrors()) {
                        errors.addAll(frontEndUnit.getErrors());
                        return CompilationResult.Failed;
                    }
                } else {
                    ICompilationSettings.debugLog.finer("Skipping creating AST for " + file);
                }
                ICompilationSettings.debugLog.finest("Clearing errors for " + file);
                errors.clear();
                ScopedTypeTracker.setEnvironment(environment);
                lastCompileAttemptTime = System.currentTimeMillis();
                
                TypeAugmentedSemanticNode invoke;
                if(typedTree == null) {
                    ICompilationSettings.debugLog.finer("Creating Type-AST for " + file);
                    midToolChain.clearErrors();
                    invoke = midToolChain.invoke(astTree);
                } else {
                    ICompilationSettings.debugLog.finer("Skipping creating Type-AST for " + file);
                    invoke = typedTree;
                }
                if (invoke != null) {
                    typedTree = invoke;
                    isCompleted = true;
                    backEndToolChain.getErrors().clear();
                    Boolean aBoolean = backEndToolChain.invoke(invoke);
                    if (!aBoolean || backEndToolChain.hasErrors()) {
                        if (!stateChanged) {
                            errors.addAll(backEndToolChain.getErrors());
                            return CompilationResult.Failed;
                        }
                        errors.addAll(backEndToolChain.getErrors());
                        backEndToolChain.clearErrors();
                        return CompilationResult.ErroredOut;
                    }
                    return CompilationResult.Completed;
                } else {
                    if (midToolChain.hasErrors()) {
                        if (!stateChanged) {
                            errors.addAll(midToolChain.getErrors());
                            return CompilationResult.Failed;
                        }
                        errors.addAll(midToolChain.getErrors());
                        midToolChain.clearErrors();
                        return CompilationResult.ErroredOut;
                    } else {
                        return CompilationResult.Failed;
                    }
                }
            } catch (AbstractCompilationError error) {
                errors.add(error);
                return CompilationResult.ErroredOut;
            } catch (Error e) {
                AbstractCompilationError error = new CompilationError(e, null);
                errors.add(error);
                return CompilationResult.ErroredOut;
            }
        }
        
        public String getFile() {
            return file;
        }
        
        public TypeAugmentedSemanticNode getTypedTree() {
            return typedTree;
        }
        
        public boolean isCompleted() {
            return isCompleted;
        }
        
        public long timeSinceLastCompile() {
            return lastCompileAttemptTime - compileStartTime;
        }
        
        @Override
        public List<AbstractCompilationError> getErrors() {
            return errors;
        }
    }
    
    private class NodeComparator implements Comparator<CompilationNode> {
        
        @Override
        public int compare(CompilationNode t0, CompilationNode t1) {
            var lhs = t0.errors.size() * t0.timeSinceLastCompile() * priorityFactor.getOrDefault(t0, 1.0);
            var rhs = t1.errors.size() * t1.timeSinceLastCompile() * priorityFactor.getOrDefault(t1, 1.0);
            ICompilationSettings.debugLog.finer("Comparing two possible nodes for next compilation");
            ICompilationSettings.debugLog.finer("\tNode 1: " + t0.file + " [" + lhs + "]");
            ICompilationSettings.debugLog.finer("\tNode 2: " + t1.file + " [" + rhs + "]");
            return (int) (lhs -
                    rhs) ;
        }
    }
    
    private PriorityQueue<CompilationNode> nodes;
    private HashMap<CompilationNode, Double> priorityFactor;
    private HashMap<CompilationNode, List<CXClassType>> directingMap;
    
    private HashMap<CXClassType, CompilationNode> classToFile;
    
    public MultipleFileHandler(List<File> files,
                               List<IFrontEndUnit<? extends AbstractSyntaxNode>> frontEndUnits,
                               List<IToolChain<? super AbstractSyntaxNode, ? extends TypeAugmentedSemanticNode>> midtoolChains) {
        compileStartTime = System.currentTimeMillis();
        fullErrors = new LinkedList<>();
        nodes = new PriorityQueue<>(files.size(), new NodeComparator());
        Iterator<IFrontEndUnit<? extends AbstractSyntaxNode>> iterator = frontEndUnits.iterator();
        Iterator<IToolChain<? super AbstractSyntaxNode, ? extends TypeAugmentedSemanticNode>> toolChainIterator = midtoolChains.iterator();
        priorityFactor = new HashMap<>();
        
        
        for (File file : files) {
            try {
                File newFile = new File(file.getName().replaceAll("\\.cx|\\.h", ".c"));
                FileCompiler fileCompiler = new FileCompiler(newFile);
                
                var backEndToolChain = ToolChainFactory.compilerFunction(fileCompiler);
                
                nodes.offer(new CompilationNode(file, iterator.next(), toolChainIterator.next(), backEndToolChain));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            
        }
        priorityFactor = new HashMap<>();
        directingMap = new HashMap<>();
        classToFile = new HashMap<>();
    }
    
    public boolean compileAll() {
        CompilationNode last = null;
        List<CompilationNode> failed = new LinkedList<>();
        while (!nodes.isEmpty()) {
            CompilationNode next = nodes.poll();
            stateChanged = !next.equals(last);
            
            ICompilationSettings.debugLog.info("Attempting to compile " + next.getFile());
            CompilationResult compilationResult = next.attemptCompile();
            var createdClasses = next.environment.getCreatedClasses();
            
            directingMap.putIfAbsent(next, new LinkedList<>());
            List<CXClassType> cxClassTypes = directingMap.get(next);
            if(cxClassTypes != null) {
                List<CXClassType> newClasses = new LinkedList<>(createdClasses);
                newClasses.removeAll(cxClassTypes);
                if(newClasses.size() > 0) {
                    ICompilationSettings.debugLog.info("In " + next.file + ":");
                    for (CXClassType newClass : newClasses) {
                        if(!classToFile.containsKey(newClass)) {
                            ICompilationSettings.debugLog.info("\t+" + newClass + " <- " + newClass.getParent());
                            cxClassTypes.add(newClass);
                            classToFile.put(newClass, next);
                        }
                    }
                }
            }
    
            switch (compilationResult) {
                case ErroredOut:
                    ICompilationSettings.debugLog.warning("Erroring out of " + next.getFile());
                    nodes.offer(next);
                    break;
                case Completed:
                    ICompilationSettings.debugLog.info("Compiled " + next.getFile());
                    break;
                case Failed: {
                    ICompilationSettings.debugLog.severe("Failed to compile " + next.getFile());
                    failed.add(next);
                }
                break;
            }
            
            // AFTER COMPILE ATTEMPT
           
            last = next;
        }
        
        for (CompilationNode compilationNode : failed) {
            ErrorReader errorReader = new ErrorReader(compilationNode.file, compilationNode.inputString,
                    compilationNode.getErrors());
            errorReader.readErrors();
        }
        
        return failed.isEmpty();
    }
    
    
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return fullErrors;
    }
}
