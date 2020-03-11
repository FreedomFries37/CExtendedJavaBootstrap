package radin;

import radin.core.IFrontEndUnit;
import radin.core.SymbolTable;
import radin.core.chaining.ToolChainFactory;
import radin.core.input.FrontEndUnit;
import radin.core.input.IParser;
import radin.core.input.Tokenizer;
import radin.core.input.frontend.v1.lexing.PreProcessingLexer;
import radin.core.input.frontend.v1.parsing.ParseNode;
import radin.core.input.frontend.v1.parsing.Parser;
import radin.core.input.frontend.v1.semantics.ActionRoutineApplier;
import radin.core.lexical.Token;
import radin.core.output.backend.compilation.FileCompiler;
import radin.core.output.backend.compilation.RuntimeCompiler;
import radin.core.output.backend.interpreter.Interpreter;
import radin.core.output.backend.interpreter.SymbolTableCreator;
import radin.core.output.backend.microcompilers.FunctionCompiler;
import radin.core.output.combo.MultipleFileHandler;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.midanalysis.TypeAugmentedSemanticTree;
import radin.core.output.midanalysis.typeanalysis.analyzers.ProgramTypeAnalyzer;
import radin.core.output.typeanalysis.TypeAnalyzer;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
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

import static java.lang.System.exit;

public class InterpreterEntrancePoint {
    private static ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, SymbolTable<CXIdentifier,
            TypeAugmentedSemanticNode>> settings;
    
    public static ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode,SymbolTable<CXIdentifier,
            TypeAugmentedSemanticNode>> getSettings() {
        return settings;
    }
    
    
    public static void main(String[] args) throws IOException {
        ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode,
                SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>> compilationSettings =
                new CompilationSettings<>();
        setCompilationSettings(compilationSettings);
        settings = compilationSettings;
        // UniversalCompilerSettings.getInstance().setSettings(compilationSettings);
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
                        compilationSettings.setLogLevel(actual);
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
        
        
        var backChain = new SymbolTableCreator();
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
            String jodinHome = System.getenv("JODIN_HOME");
            Stream<Path> pathStream = Files.find(Paths.get(jodinHome), Integer.MAX_VALUE, (p, bfa) -> bfa.isRegularFile());
            List<File> fileList = pathStream.map((p) -> new File(p.toUri())).collect(Collectors.toList());
            fileList.removeIf((f) -> !f.getName().endsWith(".jdn"));
            files.addAll(fileList);
        }
        
        MultipleFileHandler<SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>> multipleFileHandler = new MultipleFileHandler<>(
                files,
                compilationSettings
        );
        
        boolean b = multipleFileHandler.compileAll();
        if (b) {
            ICompilationSettings.debugLog.info("Generating runtime...");
            UniversalCompilerSettings.getInstance().getSettings().setLookForMainFunction(false);
            UniversalCompilerSettings.getInstance().getSettings().setInRuntimeCompilationMode(true);
            RuntimeCompiler runtimeCompiler = new RuntimeCompiler(environment);
            runtimeCompiler.setEntrancePoint("start");
            runtimeCompiler.setJodinEntrancePoint("main");
            runtimeCompiler.compile();
            
            File runtimeFile = new File("runtime.jdn");
            List<SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>> generatedOutputs =
                    multipleFileHandler.getGeneratedOutputs();
            multipleFileHandler = new MultipleFileHandler<> (
                    Collections.singletonList(runtimeFile),
                    compilationSettings
            );
            multipleFileHandler.compileAll();
            generatedOutputs.addAll(multipleFileHandler.getGeneratedOutputs());
            SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> symbolTable = new SymbolTable<>(generatedOutputs);
            Interpreter interpreter = new Interpreter(environment, symbolTable);
            
            ICompilationSettings.debugLog.info("Running interpreter");
            exit(interpreter.run(argPassOff.toArray(new String[0])));
        } else {
            ICompilationSettings.debugLog.warning("Compilation failed");
        }
        
        
    }
    
    public static void setCompilationSettings(ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode,
            SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>> settings) {
        TypeAnalyzer.setCompilationSettings(settings);
        // AbstractCompiler.setSettings(settings);
        Tokenizer.setCompilationSettings(settings);
        // ErrorReader.setSettings(settings);
        UniversalCompilerSettings.getInstance().setSettings(settings);
    }
}
