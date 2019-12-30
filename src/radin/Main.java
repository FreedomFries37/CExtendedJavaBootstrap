package radin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import radin.compilation.AbstractCompiler;
import radin.compilation.FileCompiler;
import radin.interphase.CompilationSettings;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.lexing.Lexer;
import radin.interphase.lexical.Token;
import radin.parsing.CategoryNode;
import radin.parsing.Parser;
import radin.semantics.ActionRoutineApplier;
import radin.semantics.SynthesizedMissingException;
import radin.typeanalysis.TypeAnalyzer;
import radin.typeanalysis.TypeAugmentedSemanticTree;
import radin.typeanalysis.analyzers.ProgramTypeAnalyzer;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("Testing out lexer");
        String text;
        String filename = "classTest.cx";
        
        if(args.length > 0) {
            filename = args[0];
        }
        try {
            
            text = new String(Files.readAllBytes(Paths.get(filename)));
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        String outputFile = filename.replace(".cx", ".c");
        
        Lexer lex = new Lexer(text);
        for (Token token : lex) {
            System.out.println(token);
        }
        
        Parser parser = new Parser(lex);
        CategoryNode program = parser.parse();
        program.printTreeForm();
        
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
                CompilationSettings compilationSettings = new CompilationSettings();
                TypeAnalyzer.setCompilationSettings(compilationSettings);
                TypeAugmentedSemanticTree tasTree = new TypeAugmentedSemanticTree(completed, environment);
                tasTree.printTreeForm();
                
                ProgramTypeAnalyzer analyzer = new ProgramTypeAnalyzer(tasTree.getHead());
                try{
                    boolean determineTypes = analyzer.determineTypes();
                    System.out.println("analyzer.determineTypes() = " + determineTypes);
                    tasTree.printTreeForm();
                    if(!determineTypes) {
                        for (Error error : TypeAnalyzer.getErrors()) {
                            System.err.println(error.toString());
                        }
                    }
                } catch (Error e) {
                    tasTree.printTreeForm();
                    e.printStackTrace();
                }
    
                File output = new File(outputFile);
                output.createNewFile();
    
                AbstractCompiler.setSettings(compilationSettings);
    
                FileCompiler compiler = new FileCompiler(output);
                System.out.println("compiler.compile(tasTree.getHead()) = " + compiler.compile(tasTree.getHead()));
                
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
