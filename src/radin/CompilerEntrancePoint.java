package radin;

import radin.core.IFrontEndUnit;
import radin.core.chaining.ToolChainFactory;
import radin.input.FrontEndUnit;
import radin.input.IParser;
import radin.input.Tokenizer;
import radin.frontend.v1.lexing.PreProcessingLexer;
import radin.frontend.v1.parsing.ParseNode;
import radin.frontend.v1.parsing.Parser;
import radin.frontend.v1.semantics.ActionRoutineApplier;
import radin.core.lexical.Token;
import radin.backend.compilation.FileCompiler;
import radin.backend.compilation.RuntimeCompiler;
import radin.backend.microcompilers.FunctionCompiler;
import radin.combo.MultipleFileHandler;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.TypeAugmentedSemanticTree;
import radin.midanalysis.typeanalysis.analyzers.ProgramTypeAnalyzer;
import radin.midanalysis.typeanalysis.TypeAnalyzer;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.utility.CompilationSettings;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompilerEntrancePoint {
    
    private static ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> settings;
    
    public static ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> getSettings() {
        return settings;
    }
    
    
    public static void main(String[] args) throws IOException {
        ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> compilationSettings =
                new CompilationSettings<>();
        setCompilationSettings(compilationSettings);
        // UniversalCompilerSettings.getInstance().setSettings(compilationSettings);
        
        List<String> filenamesStrings = new LinkedList<>();
        Iterator<String> argsIterator = Arrays.stream(args).iterator();
        Integer arch = null;
        while (argsIterator.hasNext()) {
            String argument = argsIterator.next();
            
            if (argument.startsWith("-") || argument.startsWith("--")) {
                switch (argument) {
                    case "-E":
                    case "--experimental": {
                        compilationSettings.setExperimental(true);
                        break;
                    }
                    case "--ast": {
                        compilationSettings.setOutputAST(true);
                        break;
                    }
                    case "--tast": {
                        compilationSettings.setOutputTAST(true);
                        break;
                    }
                    case "--directory":
                    case "-D": {
                        String dir = argsIterator.next();
                        compilationSettings.setDirectory(dir);
                        break;
                    }
                    case "-P": {
                        compilationSettings.setOutputPostprocessingOutput(true);
                        break;
                    }
                    case "--arch": {
                        if (!argsIterator.hasNext()) {
                            System.err.println("Expected an argument");
                            System.exit(-1);
                        }
                        int a = Integer.parseInt(argsIterator.next());
                        if(a != 32 && a != 64) {
                            System.err.println("Must be either 32 or 64");
                            System.exit(-1);
                        }
                        arch = a;
                        break;
                    }
                    case "--debug-level": {
                        if (!argsIterator.hasNext()) {
                            System.err.println("Expected an argument");
                            System.exit(-1);
                        }
                        int level = Integer.parseInt(argsIterator.next());
                        Level actual;
                        switch (level) {
                            case 0:
                                actual = Level.OFF;
                                break;
                            case 1:
                                actual = Level.CONFIG;
                                break;
                            case 2:
                                actual = Level.FINE;
                                break;
                            case 3:
                                actual = Level.FINER;
                                break;
                            case 4:
                                actual = Level.FINEST;
                                break;
                            case 5:
                                actual = Level.ALL;
                                break;
                            default: {
                                System.err.println("Expected level 0-5");
                                return;
                            }
                        }
                        compilationSettings.setLogLevel(actual);
                        break;
                    }
                }
            } else {
                filenamesStrings.add(argument);
                while (argsIterator.hasNext()) {
                    filenamesStrings.add(argsIterator.next());
                }
            }
            
        }
    
    
        String property = System.getProperty("os.arch");
        if(arch == null) {
            if (property == null) {
                arch = 32;
            } else {
                arch = property.contains("64") ? 64 : 32;
            }
        }
        
    
    
        if(System.getenv("MSFT") != null) {
            UniversalCompilerSettings.getInstance().getSettings().setDirectivesMustStartAtColumn1(false);
        }
        
        
        PreProcessingLexer lex = new PreProcessingLexer();
        
        if(arch == 64) {
            ICompilationSettings.debugLog.config("Using 64-bit mode");
            lex.define("__64_bit__");
        } else {
            ICompilationSettings.debugLog.config("Using 32-bit mode");
        }
        
        
        
        IParser<Token, ParseNode> parser = new Parser();
        TypeEnvironment environment = TypeEnvironment.getStandardEnvironment();
        TypeAnalyzer.setEnvironment(environment);
        ActionRoutineApplier applier = new ActionRoutineApplier(environment);
    
        FrontEndUnit<Token, ParseNode, AbstractSyntaxNode> frontEndUnit = new FrontEndUnit<>(lex, parser, applier);
        settings.setFrontEndUnit(frontEndUnit);
        
    
        ToolChainFactory.ToolChainBuilder<AbstractSyntaxNode, TypeAugmentedSemanticNode> function = ToolChainFactory.function(
                (AbstractSyntaxNode o) -> TypeAugmentedSemanticTree.convertAST(o, environment)
        );
        ToolChainFactory.ToolChainBuilder<TypeAugmentedSemanticNode, TypeAugmentedSemanticNode> compilerAnalyzer = ToolChainFactory.compilerAnalyzer(
                new ProgramTypeAnalyzer((TypeAugmentedSemanticNode) null)
        );
        var midChain = function.chain_to(compilerAnalyzer);
        settings.setMidToolChain(midChain);
        
        
        var backChain = new FileCompiler();
        settings.setBackToolChain(backChain);
        FunctionCompiler.environment = environment;
        
        List<File> files = new LinkedList<>();
        List<IFrontEndUnit<? extends AbstractSyntaxNode>> frontEndUnits = new LinkedList<>();
        for (String filenamesString : filenamesStrings) {
            File f = new File(filenamesString);
            // f.setReadOnly();
            files.add(f);
            ICompilationSettings.debugLog.info("Adding " + filenamesString + " for compilation");
        }
        
        if(System.getenv("JODIN_HOME") != null) {
            String jodinHome = System.getenv("JODIN_HOME") + "/core";
            Stream<Path> pathStream = Files.find(Paths.get(jodinHome), Integer.MAX_VALUE, (p, bfa) -> bfa.isRegularFile());
            List<File> fileList = pathStream.map((p) -> new File(p.toUri())).collect(Collectors.toList());
            fileList.removeIf((f) -> !f.getName().endsWith(".jdn"));
            files.addAll(fileList);
        }
        
        MultipleFileHandler<Boolean> multipleFileHandler = new MultipleFileHandler<>(
                files,
                compilationSettings
        );
        
        boolean b = multipleFileHandler.compileAll();
        if (b) {
            ICompilationSettings.debugLog.info("Generating runtime...");
            UniversalCompilerSettings.getInstance().getSettings().setLookForMainFunction(false);
            UniversalCompilerSettings.getInstance().getSettings().setInRuntimeCompilationMode(true);
            RuntimeCompiler runtimeCompiler = new RuntimeCompiler(environment);
            runtimeCompiler.compile();
            
            File runtimeFile = ICompilationSettings.getBuildFile("runtime.jdn");
            multipleFileHandler = new MultipleFileHandler<Boolean>(
                    Collections.singletonList(runtimeFile),
                    compilationSettings
            );
            multipleFileHandler.compileAll();
            
            ICompilationSettings.debugLog.info("Compilation completed");
        } else {
            ICompilationSettings.debugLog.warning("Compilation failed");
        }
        
        
         /*
    
        for (File file : files) {
            
            ICompilationSettings.debugLog.info("Compiling " + file.getName());
            
    
            StringBuilder text = new StringBuilder();
            try {
        
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        
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
        
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
    
            String fullText = text.toString().replace("\t", " ".repeat(compilationSettings.getTabSize()));
    
            PreProcessingLexer lex = new PreProcessingLexer(file.getPath(), fullText);
            IParser<Token, ParseNode> parser = new Parser();
            TypeEnvironment environment = TypeEnvironment.getStandardEnvironment();
            ActionRoutineApplier applier = new ActionRoutineApplier(environment);
    
            FrontEndUnit<Token, ParseNode, AbstractSyntaxNode> frontEndUnit = new FrontEndUnit<>(lex, parser, applier);
    
    
            ToolChainFactory.ToolChainHead<AbstractSyntaxNode> abstractSyntaxNodeToolChainHead = ToolChainFactory.compilerProducer(frontEndUnit);
            ToolChainFactory.ToolChainBuilder<AbstractSyntaxNode, TypeAugmentedSemanticNode> function = ToolChainFactory.function(
                    (AbstractSyntaxNode o) -> TypeAugmentedSemanticTree.convertAST(o, environment)
            );
            ToolChainFactory.ToolChainBuilder<Void, TypeAugmentedSemanticNode> chain =
                    abstractSyntaxNodeToolChainHead.chain_to(function);
    
    
            AbstractSyntaxNode build = frontEndUnit.build();
            ICompilationSettings.debugLog.info("Building completed");
            ICompilationSettings.debugLog.info("Created Abstract Syntax Tree of depth " + build.getDepth() + " with a " +
                    "total of " + build.getTotalNodes() + " nodes");
            ICompilationSettings.debugLog.info("Builder run count: " + applier.getRunCount());
    
            if(build != null) {
                try {
                    // build.printTreeForm();
                    //System.out.println(applier.getSuccessOrder());
                    //System.out.println(completed.getRepresentation());
        
                    boolean typeErrors = applier.noTypeErrors();
                    System.out.println("applier.noTypeErrors() = " + typeErrors);
        
        
                    ScopedTypeTracker.setEnvironment(environment);
        
        
                    TypeAugmentedSemanticTree tasTree = new TypeAugmentedSemanticTree(build, environment);
                    // tasTree.printTreeForm();
        
                    ProgramTypeAnalyzer analyzer = new ProgramTypeAnalyzer(tasTree.getHead());
                    try {
                        boolean determineTypes = analyzer.determineTypes();
                        System.out.println("analyzer.determineTypes() = " + determineTypes);
                        tasTree.printTreeForm();
            
            
                        if (!determineTypes) {
                            ErrorReader errorReader = new ErrorReader(file.getName(), lex.getInputString(),
                                    analyzer.getErrors());
                            errorReader.readErrors();
                        } else {
                
                
                            
                
                            FileCompiler compiler = new FileCompiler(file);
                            compiler.setPreamble("");
                            System.out.println("compiler.compile(tasTree.getHead()) = " + compiler.compile(tasTree.getHead()));
                            ICompilationSettings.debugLog.info("File " + file.getName() + " compiled");
                
                            ICompilationSettings.debugLog.info("Outputting compiled file as " + compiler.getOutputFile());
                            ICompilationSettings.debugLog.info(compiler.getOutputFile() + " size = " + ((double) compiler.getOutputFile().length() / 1028) + " kB");
                        }
                    } catch (Error e) {
                        tasTree.printTreeForm();
                        e.printStackTrace();
                    }
    
                
                /*
                for (CXClassType createdClass : environment.getCreatedClasses()) {
                    createdClass.seal(environment);
                    System.out.println(createdClass.generateCDefinition());
                }
                
                
        
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ErrorReader errorReader = new ErrorReader(file.getName(), lex.getInputString(),
                        frontEndUnit.getErrors());
                errorReader.readErrors();
            
            }
        }
        
        
          */
        
        
    }
    
    public static void setCompilationSettings(ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> settings) {
        CompilerEntrancePoint.settings = settings;
        TypeAnalyzer.setCompilationSettings(settings);
        // AbstractCompiler.setSettings(settings);
        Tokenizer.setCompilationSettings(settings);
        // ErrorReader.setSettings(settings);
        UniversalCompilerSettings.getInstance().setSettings(settings);
    }
}
