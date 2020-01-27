package radin;

import radin.core.IFrontEndUnit;
import radin.core.chaining.IToolChain;
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
import radin.core.output.combo.MultipleFileHandler;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.midanalysis.TypeAugmentedSemanticTree;
import radin.core.output.midanalysis.typeanalysis.analyzers.ProgramTypeAnalyzer;
import radin.core.output.typeanalysis.TypeAnalyzer;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.utility.CompilationSettings;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class CompilerEntrancePoint {
    
    private static ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> settings;
    
    public static ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> getSettings() {
        return settings;
    }
    
    
    public static void main(String[] args) throws IOException {
        ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> compilationSettings =
                new CompilationSettings<>();
        
        setCompilationSettings(compilationSettings);
        List<String> filenamesStrings = new LinkedList<>();
        Iterator<String> argsIterator = Arrays.stream(args).iterator();
        while (argsIterator.hasNext()) {
            String argument = argsIterator.next();
            
            if (argument.startsWith("-") || argument.startsWith("--")) {
                switch (argument) {
                    case "-E":
                    case "--experimental": {
                        compilationSettings.setExperimental(true);
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
    
        PreProcessingLexer lex = new PreProcessingLexer();
        IParser<Token, ParseNode> parser = new Parser();
        TypeEnvironment environment = TypeEnvironment.getStandardEnvironment();
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
        
        List<File> files = new LinkedList<>();
        List<IFrontEndUnit<? extends AbstractSyntaxNode>> frontEndUnits = new LinkedList<>();
        List<IToolChain<? super AbstractSyntaxNode, ? extends TypeAugmentedSemanticNode>> toolChains = new LinkedList<>();
        for (String filenamesString : filenamesStrings) {
            File f = new File(filenamesString);
            // f.setReadOnly();
            files.add(f);
            ICompilationSettings.debugLog.info("Adding " + filenamesString + " for compilation");
        }
        
        
        MultipleFileHandler multipleFileHandler = new MultipleFileHandler(
                files,
                compilationSettings
        );
        
        boolean b = multipleFileHandler.compileAll();
        if (!b) {
            ICompilationSettings.debugLog.info("Compilation completed");
        }
        
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
