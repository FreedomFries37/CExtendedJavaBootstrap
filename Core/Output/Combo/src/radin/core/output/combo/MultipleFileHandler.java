package radin.core.output.combo;

import radin.core.ErrorReader;
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

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class MultipleFileHandler implements ICompilationErrorCollector {
    
    private ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> settings;
    
    
    private List<AbstractCompilationError> fullErrors;
    private boolean stateChanged = true;
    
    private enum CompilationResult {
        ErroredOut,
        Completed,
        Failed
    }
    
    private final long compileStartTime;
    
    private IFrontEndUnit<? extends AbstractSyntaxNode> frontEndUnit;
    private IToolChain<? super AbstractSyntaxNode, ? extends TypeAugmentedSemanticNode> midToolChain;
    private IToolChain<? super TypeAugmentedSemanticNode, ? extends Boolean> backToolChain;
    
    
    private class CompilationNode implements ICompilationErrorCollector {
        private String file;
        private AbstractSyntaxNode astTree;
        private TypeAugmentedSemanticNode typedTree;
        private String inputString;
        private long lastCompileAttemptTime;
        
        
        private boolean isCompleted;
        private List<AbstractCompilationError> errors;
        
        private TypeEnvironment environment;
        
        public CompilationNode(File f, TypeEnvironment e) {
            file = f.getPath();
            this.environment = e;
            StringBuilder text = new StringBuilder();
            try {
        
                BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
        
                String line;
                while ((line = bufferedReader.readLine()) != null) {
            
                    if (!line.endsWith("\\")) {
                        text.append(line);
                        text.append("\n");
                    } else {
                        text.append(line, 0, line.length() - 1);
                        text.append(' ');
                    }
                }
        
            } catch (IOException err) {
                err.printStackTrace();
                return;
            }
            inputString = text.toString().replace("\t", " ".repeat(settings.getTabSize()));
    
            errors = new LinkedList<>();
        }
        
        public String getInputString() {
            return inputString;
        }
    
        @Override
        public String toString() {
            return "CompilationNode{" +
                    "file='" + file + '\'' +
                    ", lastCompileAttemptTime=" + lastCompileAttemptTime +
                    ", isCompleted=" + isCompleted +
                    '}';
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
                lastCompileAttemptTime = compileAttempt;
                if(astTree == null) {
                    frontEndUnit.reset();
                    frontEndUnit.clearErrors();
                    ICompilationSettings.debugLog.finer("Creating AST for " + file);
                    ICompilationSettings.debugLog.finer("Setting lexer.filename to " + file);
                    frontEndUnit.setVariable("lexer.filename", file);
                    ICompilationSettings.debugLog.finer("Setting lexer.inputString");
                    frontEndUnit.setVariable("lexer.inputString", inputString);
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
                
                
                TypeAugmentedSemanticNode invoke;
                if(typedTree == null) {
                    ICompilationSettings.debugLog.finer("Setting environment");
                    midToolChain.setVariable("environment", environment);
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
                    ICompilationSettings.debugLog.finer("Setting file to " + file);
                    backToolChain.setVariable("file", file);
                    backToolChain.getErrors().clear();
                    Boolean aBoolean = backToolChain.invoke(invoke);
                    if (!aBoolean || backToolChain.hasErrors()) {
                        if (!stateChanged) {
                            errors.addAll(backToolChain.getErrors());
                            return CompilationResult.Failed;
                            
                        }
                        errors.addAll(backToolChain.getErrors());
                        backToolChain.clearErrors();
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
            return compileAttempt - lastCompileAttemptTime;
        }
        
        public long getNumberOfUnfilledDependencies() {
            Stream<CXIdentifier> cxIdentifiers = dependencies.get(this).stream();
            long dependencyCount = dependencies.get(this).size();
            for (CXClassType cxClassType : classToFile.keySet()) {
                if(cxIdentifiers.anyMatch(o -> o.equals(cxClassType.getTypeNameIdentifier()))) {
                    --dependencyCount;
                }
            }
            return dependencyCount;
        }
        
        
        
        @Override
        public List<AbstractCompilationError> getErrors() {
            return errors;
        }
    }
    
    private class NodeComparator implements Comparator<CompilationNode> {
        
        @Override
        public int compare(CompilationNode t0, CompilationNode t1) {
            var lhs = (double) t0.getNumberOfUnfilledDependencies() / t0.timeSinceLastCompile();
            var rhs = (double) t1.getNumberOfUnfilledDependencies() / t1.timeSinceLastCompile();
            ICompilationSettings.debugLog.finer("Comparing two possible nodes for next compilation");
            ICompilationSettings.debugLog.finer("\tNode 1: " + t0.file + " [" + lhs + "]");
            ICompilationSettings.debugLog.finer("\tNode 2: " + t1.file + " [" + rhs + "]");
            return Double.compare(lhs, rhs);
        }
    }
    
    private HashSet<CompilationNode> nodes;
    private int compileAttempt;
    private HashMap<CompilationNode, List<CXClassType>> directingMap;
    private HashMap<CompilationNode, List<CXIdentifier>> dependencies;
    private HashMap<CXClassType, CompilationNode> classToFile;
    
    
    
    public MultipleFileHandler(List<File> files, ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> settings) {
        this.settings = settings;
        compileStartTime = System.currentTimeMillis();
        fullErrors = new LinkedList<>();
        nodes = new HashSet<>();
        frontEndUnit = settings.getFrontEndUnit();
        if(frontEndUnit == null) {
            ICompilationSettings.debugLog.severe("Front End Missing!");
            System.exit(-1);
        }
        midToolChain = settings.getMidToolChain();
        if(midToolChain == null) {
            ICompilationSettings.debugLog.severe("Mid tool chain (AST -> TAST) Missing!");
            System.exit(-1);
        }
        backToolChain = settings.getBackToolChain();
        if(backToolChain == null) {
            ICompilationSettings.debugLog.severe("Back tool chain (TAST -> output) Missing!");
            System.exit(-1);
        }
        
        dependencies = new HashMap<>();
        
        for (File file : files) {
            
            CompilationNode e = new CompilationNode(file, TypeEnvironment.getStandardEnvironment());
            nodes.add(e);
            dependencies.put(e, new LinkedList<>());
        }
        
        directingMap = new HashMap<>();
        classToFile = new HashMap<>();
    }
    
    public boolean compileAll() {
        compileAttempt = 0;
        CompilationNode last = null;
        List<CompilationNode> failed = new LinkedList<>();
        CompilationNode firstInCycle = null;
        Set<CompilationNode> explored = new HashSet<>();
        while (!nodes.isEmpty()) {
            compileAttempt++;
            CompilationNode next = Collections.min(nodes, new NodeComparator());
            nodes.remove(next);
           
            stateChanged = !next.equals(last);
            if(firstInCycle == null) {
                firstInCycle = next;
                explored.clear();
            } else if(firstInCycle == next) {
                if(explored.equals(new HashSet<>(nodes))) {
                    ICompilationSettings.debugLog.severe("Attempted to compile project and made no progress");
                    ICompilationSettings.debugLog.severe("Compilation Failed");
    
                    return false;
                }
            }
            
            next.getErrors().clear();
            
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
                    for (AbstractCompilationError error : next.getErrors()) {
                        ICompilationSettings.debugLog.throwing("MultipleFileHandler", "attemptCompile", error);
                    }
                    nodes.add(next);
                    break;
                case Completed:
                    ICompilationSettings.debugLog.info("Compiled " + next.getFile());
                    if(firstInCycle == next) {
                        firstInCycle = null;
                    }
                    break;
                case Failed: {
                    ICompilationSettings.debugLog.severe("Failed to compile " + next.getFile());
                    failed.add(next);
                }
                break;
            }
            
            // AFTER COMPILE ATTEMPT
            explored.add(next);
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
