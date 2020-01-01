package radin;

import radin.compilation.AbstractCompiler;
import radin.compilation.FileCompiler;
import radin.interphase.CompilationSettings;
import radin.interphase.ICompilationSettings;
import radin.interphase.lexical.Token;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.TypeEnvironment;
import radin.lexing.Lexer;
import radin.parsing.CategoryNode;
import radin.parsing.Parser;
import radin.semantics.ActionRoutineApplier;
import radin.semantics.SynthesizedMissingException;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticTree;
import radin.typeanalysis.analyzers.ProgramTypeAnalyzer;

import java.io.*;

public class Main {
    
    private static ICompilationSettings settings;
    
    public static ICompilationSettings getSettings() {
        return settings;
    }
    
    public static void setCompilationSettings(ICompilationSettings settings) {
        Main.settings = settings;
        TypeAnalyzer.setCompilationSettings(settings);
        AbstractCompiler.setSettings(settings);
        Lexer.setCompilationSettings(settings);
    }
    
    public static void main(String[] args) {
        System.out.println("Testing out lexer");
        
        String filename = "classTest.cx";
        
        if(args.length > 0) {
            filename = args[0];
        }
    
        StringBuilder text = new StringBuilder();
        try {
    
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            
            String line;
            while((line = bufferedReader.readLine()) != null) {
                
                if(!line.endsWith("\\")) {
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
    
        CompilationSettings compilationSettings = new CompilationSettings();
        compilationSettings.setShowErrorStackTrace(false);
        compilationSettings.setReduceIndirection(false);
        setCompilationSettings(compilationSettings);
    
        String fullText = text.toString().replace("\t", " ".repeat(compilationSettings.getTabSize()));
        Lexer lex = new Lexer(filename, fullText);
        for (Token token : lex) {
            System.out.println(token);
        }
        
        Parser parser = new Parser(lex);
        CategoryNode program = parser.parse();
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
        if(b) {
            try {
                AbstractSyntaxNode completed = program.getSynthesized();
                completed.printTreeForm();
                //System.out.println(applier.getSuccessOrder());
                //System.out.println(completed.getRepresentation());
                
                System.out.println("applier.noTypeErrors() = " + applier.noTypeErrors());
                
                TypeAnalyzer.setEnvironment(environment);
                
                
                TypeAugmentedSemanticTree tasTree = new TypeAugmentedSemanticTree(completed, environment);
                tasTree.printTreeForm();
                
                ProgramTypeAnalyzer analyzer = new ProgramTypeAnalyzer(tasTree.getHead());
                try{
                    boolean determineTypes = analyzer.determineTypes();
                    System.out.println("analyzer.determineTypes() = " + determineTypes);
                    tasTree.printTreeForm();
                    
                    
                    if(!determineTypes) {
                        ErrorReader errorReader = new ErrorReader(filename, lex.getInputString(),
                                TypeAnalyzer.getErrors());
                        errorReader.readErrors();
                    } else {
    
    
                        File output = new File(outputFile);
                        output.createNewFile();
    
    
                        FileCompiler compiler = new FileCompiler(output);
                        System.out.println("compiler.compile(tasTree.getHead()) = " + compiler.compile(tasTree.getHead()));
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
                
            } catch (SynthesizedMissingException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
