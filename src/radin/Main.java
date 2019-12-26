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

public class Main {
    
    public static void main(String[] args) {
        System.out.println("Testing out lexer");
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get("classTest.cx")));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Lexer lex = new Lexer(text);
        for (Token token : lex) {
            System.out.println(token);
        }
        /*
        //Lexer lex2 = new Lexer("typedef const int[][] pid; typedef pid* pid_ptr;");
        Lexer lex2 =
                new Lexer(
                        "typedef unsigned long int* llpt;\n" +
                        "typedef struct node { int val; struct node* next; } name;\n" +
                        "typedef name** name_ptr;\n" +
                        "typedef union { int a; unsigned int b; } either;"
                );
        for (Token token : lex2) {
            System.out.println(token);
        }
        
         */
        Parser parser = new Parser(lex);
        CategoryNode program = parser.parse();
        program.printTreeForm();
    
        TypeEnvironment environment = new TypeEnvironment();
        ActionRoutineApplier applier = new ActionRoutineApplier(environment);
        boolean b = applier.enactActionRoutine(program);
        if(b) {
            try {
                AbstractSyntaxNode completed = program.getSynthesized();
                completed.printTreeForm();
                //System.out.println(applier.getSuccessOrder());
                //System.out.println(completed.getRepresentation());
                
                System.out.println("applier.noTypeErrors() = " + applier.noTypeErrors());
                for (CXClassType createdClass : environment.getCreatedClasses()) {
                    createdClass.seal(environment);
                    System.out.println(createdClass.generateCDefinition());
                }
                
            } catch (SynthesizedMissingException e) {
                e.printStackTrace();
            }
        }
    }
}
