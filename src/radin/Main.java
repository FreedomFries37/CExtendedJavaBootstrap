package radin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


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
        try {
            if(args.length > 0) {
                text = new String(Files.readAllBytes(Paths.get(args[0])));
            } else text = new String(Files.readAllBytes(Paths.get("classTest.cx")));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
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
                TypeAugmentedSemanticTree tasTree = new TypeAugmentedSemanticTree(completed, environment);
                tasTree.printTreeForm();
                
                ProgramTypeAnalyzer analyzer = new ProgramTypeAnalyzer(tasTree.getHead());
                try{
                    System.out.println("analyzer.determineTypes() = " + analyzer.determineTypes());
                    tasTree.printTreeForm();
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
                
            } catch (SynthesizedMissingException e) {
                e.printStackTrace();
            }
        }
    }
}
