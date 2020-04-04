package radin;

import radin.backend.compilation.FileCompiler;
import radin.backend.compilation.RuntimeCompiler;
import radin.backend.microcompilers.FunctionCompiler;
import radin.combo.MultipleFileHandler;
import radin.core.SymbolTable;
import radin.core.chaining.ToolChainFactory;
import radin.core.lexical.Token;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.utility.CompilationSettings;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;
import radin.frontend.v1.lexing.PreProcessingLexer;
import radin.frontend.v1.parsing.ParseNode;
import radin.frontend.v1.parsing.Parser;
import radin.frontend.v1.semantics.ActionRoutineApplier;
import radin.input.FrontEndUnit;
import radin.input.IParser;
import radin.interpreter.Interpreter;
import radin.interpreter.SymbolTableCreator;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.TypeAugmentedSemanticTree;
import radin.midanalysis.typeanalysis.analyzers.ProgramTypeAnalyzer;
import radin.midanalysis.typeanalysis.TypeAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.*;

public class ToolchainEntrancePoint {
    
    /**
     * Runs the toolchain execution mode
     * @param args
     */
    public static void main(String[] args) throws IOException {
        File toolchainDirectory = new File(".toolchain");
        if (!toolchainDirectory.exists() || !toolchainDirectory.isDirectory()) {
            if (System.getenv("JODIN_HOME") == null) throw new IOException("Jodin Home not set");
            toolchainDirectory = new File(System.getenv("JODIN_HOME") + "/toolchain");
            if (!toolchainDirectory.exists() || !toolchainDirectory.isDirectory()) {
                throw new IOException("No toolchain directory!");
            }
        }
        
        File configFile = new File(toolchainDirectory, "config");
        ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, ?> settings = new CompilationSettings<>();
       
    
        List<String> argPassOff = new LinkedList<>();
        List<String> filenamesStrings = new LinkedList<>();
        Iterator<String> argsIterator = Arrays.stream(args).iterator();
        Integer arch = null;
        while (argsIterator.hasNext()) {
            String argument = argsIterator.next();
        
            if (argument.startsWith("-") || argument.startsWith("--")) {
                switch (argument) {
                    case "-E":
                    case "--experimental": {
                        settings.setExperimental(true);
                        break;
                    }
                    case "--ast": {
                        settings.setOutputAST(true);
                        break;
                    }
                    case "--tast": {
                        settings.setOutputTAST(true);
                        break;
                    }
                    case "--directory":
                    case "-D": {
                        String dir = argsIterator.next();
                        settings.setDirectory(dir);
                        break;
                    }
                    case "-P": {
                        settings.setOutputPostprocessingOutput(true);
                        break;
                    }
                    case "--arch": {
                        if (!argsIterator.hasNext()) {
                            System.err.println("Expected an argument");
                            exit(-1);
                        }
                        int a = Integer.parseInt(argsIterator.next());
                        if(a != 32 && a != 64) {
                            System.err.println("Must be either 32 or 64");
                            exit(-1);
                        }
                        arch = a;
                        break;
                    }
                    case "--debug-level": {
                        if (!argsIterator.hasNext()) {
                            System.err.println("Expected an argument");
                            exit(-1);
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
                        settings.setLogLevel(actual);
                        break;
                    }
                    case "--args":
                    case "-a": {
                        while (argsIterator.hasNext()) {
                            argPassOff.add(argsIterator.next());
                        }
                        break;
                    }
                }
            } else {
                filenamesStrings.add(argument);
                /*
                while (argsIterator.hasNext()) {
                    filenamesStrings.add(argsIterator.next());
                }
                
                 */
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
        UniversalCompilerSettings.getInstance().setSettings(settings);
    
        if(System.getenv("MSFT") != null) {
            UniversalCompilerSettings.getInstance().getSettings().setDirectivesMustStartAtColumn1(false);
        }
        
        BufferedReader fileReader = new BufferedReader(new FileReader(configFile));
        PreProcessingLexer lex = new PreProcessingLexer();
    
        if(arch == 64) {
            ICompilationSettings.debugLog.config("Using 64-bit mode");
            lex.define("__64_bit__");
        } else {
            ICompilationSettings.debugLog.config("Using 32-bit mode");
        }
    
        List<File> files = new LinkedList<>();
        for (String filenamesString : filenamesStrings) {
            File f = new File(filenamesString);
            // f.setReadOnly();
            files.add(f);
            ICompilationSettings.debugLog.info("Adding " + filenamesString + " for compilation");
        }
        if(System.getenv("JODIN_HOME") != null) {
            String jodinHome = System.getenv("JODIN_HOME");
            Stream<Path> pathStream = Files.find(Paths.get(jodinHome, "core"), Integer.MAX_VALUE,
                    (p, bfa) -> bfa.isRegularFile());
            List<File> fileList = pathStream.map((p) -> new File(p.toUri())).collect(Collectors.toList());
            fileList.removeIf((f) -> !f.getName().endsWith(".jdn"));
            files.addAll(fileList);
        }
        
    
        IParser<Token, ParseNode> parser = new Parser();
        TypeEnvironment environment = TypeEnvironment.getStandardEnvironment();
        TypeAnalyzer.setEnvironment(environment);
        ActionRoutineApplier applier = new ActionRoutineApplier(environment);
    
        FrontEndUnit<Token, ParseNode, AbstractSyntaxNode> frontEndUnit = new FrontEndUnit<>(lex, parser, applier);
        
    
    
        ToolChainFactory.ToolChainBuilder<AbstractSyntaxNode, TypeAugmentedSemanticNode> function = ToolChainFactory.function(
                (AbstractSyntaxNode o) -> TypeAugmentedSemanticTree.convertAST(o, environment)
        );
        ToolChainFactory.ToolChainBuilder<TypeAugmentedSemanticNode, TypeAugmentedSemanticNode> compilerAnalyzer = ToolChainFactory.compilerAnalyzer(
                new ProgramTypeAnalyzer((TypeAugmentedSemanticNode) null)
        );
        var midChain = function.chain_to(compilerAnalyzer);
        
    
        MultipleFileHandler<?> compiler = null;
        
        
        boolean useInterpreter = false;
        while (fileReader.ready()) {
            String line = fileReader.readLine().replaceAll("#.*$", "");
            if(line.isBlank()) {
                continue;
            }
            int index = line.indexOf('=');
            String option = line.substring(0, index).trim();
            String argument = line.substring(index + 1).trim();
            
            switch (option) {
                case "type": {
                    if (argument.equals("interpreter")) {
                        useInterpreter = true;
                        ICompilationSettings.debugLog.config("Using interpreter");
                        
                        ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode,
                                SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>> newSettings =
                                new CompilationSettings<>();
                        newSettings.copySettingsFrom(settings);
                        newSettings.setFrontEndUnit(frontEndUnit);
                        newSettings.setMidToolChain(midChain);
                        var backChain = new SymbolTableCreator();
                        newSettings.setBackToolChain(backChain);
                        settings = newSettings;
                        UniversalCompilerSettings.getInstance().setSettings(settings);
                        
                        
                        compiler =
                                new MultipleFileHandler<>(files, newSettings);
                        FunctionCompiler.environment = environment;
                    } else if(argument.equals("compiler")) {
                        useInterpreter = false;
                        ICompilationSettings.debugLog.config("Using Compiler");
    
                        ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> newSettings =
                                new CompilationSettings<>();
                        newSettings.copySettingsFrom(settings);
                        newSettings.setFrontEndUnit(frontEndUnit);
                        newSettings.setMidToolChain(midChain);
                        settings = newSettings;
                        UniversalCompilerSettings.getInstance().setSettings(settings);
    
                        compiler = new MultipleFileHandler<>(files, newSettings);
    
                        var backChain = new FileCompiler();
                        newSettings.setBackToolChain(backChain);
                        FunctionCompiler.environment = environment;
                    } else {
                        throw new IllegalArgumentException();
                    }
                    break;
                }
                case "toolchain": {
                    ICompilationSettings.debugLog.config("Set toolchain to " + argument);
                    String sources = new File(toolchainDirectory, argument + "/src").getAbsolutePath();
                    String include = new File(toolchainDirectory, argument + "/include").getAbsolutePath();
                    settings.addAdditionalSourceDirectory(sources);
                    settings.addIncludeDirectories(include);
                    break;
                }
                case "experimental": {
                    boolean useExperimental = Boolean.parseBoolean(argument);
                    settings.setExperimental(useExperimental);
                    break;
                }
                case "opt-level": {
                    int optlevel = Integer.parseInt(argument);
                    settings.setOptimizationLevel(optlevel);
                    break;
                }
                case "trycatch": {
                    boolean tryCatch = Boolean.parseBoolean(argument);
                    settings.setUseTryCatch(tryCatch);
                    break;
                }
                case "stacktrace": {
                    boolean stacktrace = Boolean.parseBoolean(argument);
                    settings.setUseStackTrace(stacktrace);
                    break;
                }
                case "autostring": {
                    boolean autostring = Boolean.parseBoolean(argument);
                    settings.setAutoCreateStrings(autostring);
                    break;
                }
                default: {
                    err.println("Invalid Config Option: " + option);
                    exit(-1);
                }
            }
        }
        
        if(compiler == null) throw new IllegalArgumentException("No toolchain set");
    
        compiler.addFiles(UniversalCompilerSettings.getInstance().getSettings().getAdditionalSources());
        
        
        if(compiler.compileAll()) {
            ICompilationSettings.debugLog.info("Generating runtime...");
            UniversalCompilerSettings.getInstance().getSettings().setLookForMainFunction(false);
            UniversalCompilerSettings.getInstance().getSettings().setInRuntimeCompilationMode(true);
            RuntimeCompiler runtimeCompiler = new RuntimeCompiler(environment);
            if(useInterpreter) {
                runtimeCompiler.setEntrancePoint("start");
                runtimeCompiler.setJodinEntrancePoint("main");
            }
            if (!runtimeCompiler.compile()) {
                err.println("Runtime Jodin Compilation failed");
            }
    
            File runtimeFile = ICompilationSettings.getBuildFile("runtime.jdn");
            if(useInterpreter) {
                ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, SymbolTable<CXIdentifier,
                        TypeAugmentedSemanticNode>> fixedSettings =
                        (ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode,
                        SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>>) settings;
    
                MultipleFileHandler<SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>> fixedCompiler = (MultipleFileHandler<SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>>) compiler;
                List<SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>> generatedOutputs =
                        fixedCompiler.getGeneratedOutputs();
                fixedCompiler = new MultipleFileHandler<>(
                        Collections.singletonList(runtimeFile),
                        fixedSettings
                );
                if(!fixedCompiler.compileAll()) {
                    err.println("Runtime Compilation failed");
                }
                generatedOutputs.addAll(fixedCompiler.getGeneratedOutputs());
                SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> symbolTable = new SymbolTable<>(generatedOutputs);
                Interpreter interpreter = new Interpreter(environment, symbolTable);
    
                ICompilationSettings.debugLog.info("Running interpreter");
                exit(interpreter.run(argPassOff.toArray(new String[0])));
            } else {
                ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> fixedSettings =
                        (ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean>) settings;
                MultipleFileHandler<Boolean> fixedCompiler = new MultipleFileHandler<>(Collections.singletonList(runtimeFile),
                        fixedSettings);
    
                if(!fixedCompiler.compileAll()) {
                    err.println("Runtime Compilation failed");
                }
    
                System.out.println("Compilation Succeeded");
            }
    
        } else {
            System.err.println("Compilation Failed");
        }
        
    }
}
