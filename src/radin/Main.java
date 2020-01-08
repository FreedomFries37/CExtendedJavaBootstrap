package radin;

import radin.compilation.AbstractCompiler;
import radin.compilation.FileCompiler;
import radin.core.frontend.FrontEndUnit;
import radin.core.frontend.Tokenizer;
import radin.utility.CompilationSettings;
import radin.utility.ICompilationSettings;
import radin.core.lexical.Token;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.v1.lexing.PreprocessingLexer;
import radin.v1.parsing.ParseNode;
import radin.v1.parsing.Parser;
import radin.v1.semantics.ActionRoutineApplier;
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
        Tokenizer.setCompilationSettings(settings);
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
        PreprocessingLexer lex = new PreprocessingLexer(filename, fullText);
        Parser parser = new Parser();
        TypeEnvironment environment = TypeEnvironment.getStandardEnvironment();
        ActionRoutineApplier applier = new ActionRoutineApplier(environment);
    
        FrontEndUnit<Token, ParseNode, AbstractSyntaxNode> frontEndUnit = new FrontEndUnit<>(lex, parser, applier);
    
        AbstractSyntaxNode build = frontEndUnit.build();
    
        if(build != null) {
            try {
                build.printTreeForm();
                //System.out.println(applier.getSuccessOrder());
                //System.out.println(completed.getRepresentation());
                
                System.out.println("applier.noTypeErrors() = " + applier.noTypeErrors());
                
                TypeAnalyzer.setEnvironment(environment);
                
                
                TypeAugmentedSemanticTree tasTree = new TypeAugmentedSemanticTree(build, environment);
                tasTree.printTreeForm();
                
                ProgramTypeAnalyzer analyzer = new ProgramTypeAnalyzer(tasTree.getHead());
                try{
                    boolean determineTypes = analyzer.determineTypes();
                    System.out.println("analyzer.determineTypes() = " + determineTypes);
                    tasTree.printTreeForm();
                    
                    
                    if(!determineTypes) {
                        ErrorReader errorReader = new ErrorReader(filename, lex.getInputString(),
                                analyzer.getErrors());
                        errorReader.readErrors();
                    } else {
    
    
                        File output = new File(outputFile);
                        output.createNewFile();
    
    
                        FileCompiler compiler = new FileCompiler(output);
                        compiler.setPreamble("#include <stdlib.h>\n" + "void print(char* name);\n" +
                                "void println(char* name);\n");
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
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ErrorReader errorReader = new ErrorReader(filename, lex.getInputString(),
                    frontEndUnit.getErrors());
            errorReader.readErrors();
        }
    }
}
