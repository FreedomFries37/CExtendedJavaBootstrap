package radin;

import radin.core.ErrorReader;
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
import radin.core.output.midanalysis.ScopedTypeTracker;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.midanalysis.TypeAugmentedSemanticTree;
import radin.core.output.midanalysis.typeanalysis.analyzers.ProgramTypeAnalyzer;
import radin.core.output.typeanalysis.TypeAnalyzer;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.utility.CompilationSettings;
import radin.core.utility.ICompilationSettings;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class Main {
    
    private static ICompilationSettings settings;
    
    public static void main(String[] args) throws FileNotFoundException {
        CompilationSettings compilationSettings = new CompilationSettings();
        compilationSettings.setShowErrorStackTrace(false);
        compilationSettings.setReduceIndirection(false);
        setCompilationSettings(compilationSettings);
        getSettings().setLogLevel(Level.FINER);
        
        
        System.setErr(ICompilationSettings.debugLog.divertOutput(Level.SEVERE, System.err));
        if (System.getenv().containsKey("DEBUG")) {
            String debug = System.getenv("DEBUG");
            boolean val = Boolean.parseBoolean(debug);
            if (!val) {
                System.setOut(ICompilationSettings.debugLog.divertOutput(Level.INFO, System.out));
            }
            
        }
        
        List<String> strings = Arrays.asList(args);
        if (strings.contains("-E")) {// experimental
            compilationSettings.setExperimental(true);
        }
        
        String filename = "classTest.cx";
        
        if (args.length > 0) {
            filename = args[0];
        }
        
        StringBuilder text = new StringBuilder();
        try {
            
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            
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
        
        String outputFile = filename.replace(".cx", ".c");
        
        
        String fullText = text.toString().replace("\t", " ".repeat(compilationSettings.getTabSize()));
        /*
        Lexer lex = new Lexer(filename, fullText);
        for (Token token : lex) {
            System.out.println(token);
        }
        if(lex.hasErrors()) {
            ErrorReader errorReader = new ErrorReader(filename, lex.getInputString(),
                    lex.getErrors());
            errorReader.readErrors();
            return;
        }
        
        Parser parser = new Parser(lex);
        CategoryNode program = parser.parse();
        if(parser.hasErrors() || program == null) {
            ErrorReader errorReader = new ErrorReader(filename, lex.getInputString(), parser.getErrors());
            errorReader.readErrors();
            return;
        }
        program.printTreeForm();
        
        if(args.length >= 2) {
            if(args[1].equals("-p")) {
                File preprocesserOutput = new File(filename.replace(".cx", ".cxp"));
                try {
                    FileWriter fileWriter = new FileWriter(preprocesserOutput);
                    fileWriter.write(lex.getInputString());
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        TypeEnvironment environment = TypeEnvironment.getStandardEnvironment();
        ActionRoutineApplier applier = new ActionRoutineApplier(environment);
        boolean b = applier.enactActionRoutine(program);
        
         */
    
        
    
    
        PreProcessingLexer lex = new PreProcessingLexer(filename, fullText);
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
        
        
        if (build != null) {
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
                        ErrorReader errorReader = new ErrorReader(filename, lex.getInputString(),
                                analyzer.getErrors());
                        errorReader.readErrors();
                    } else {
                        
                        
                        File output = new File(outputFile);
                        output.createNewFile();
                        
                        
                        FileCompiler compiler = new FileCompiler(output);
                        compiler.setPreamble("");
                        System.out.println("compiler.compile(tasTree.getHead()) = " + compiler.compile(tasTree.getHead()));
                        ICompilationSettings.debugLog.info("File " + filename + " compiled");
                        
                        ICompilationSettings.debugLog.info("Outputting compiled file as " + outputFile);
                        ICompilationSettings.debugLog.info(outputFile + " size = " + ((double) output.length() / 1028) + " kB");
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
                
                 */
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ErrorReader errorReader = new ErrorReader(filename, lex.getInputString(),
                    frontEndUnit.getErrors());
            errorReader.readErrors();
        }
    }
    
    public static ICompilationSettings getSettings() {
        return settings;
    }
    
    public static void setCompilationSettings(ICompilationSettings settings) {
        Main.settings = settings;
        TypeAnalyzer.setCompilationSettings(settings);
        // AbstractCompiler.setSettings(settings);
        Tokenizer.setCompilationSettings(settings);
    }
}
