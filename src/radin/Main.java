package radin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import radin.interphase.semantics.AbstractSyntaxNode;
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
            text = new String(Files.readAllBytes(Paths.get("test.cx")));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Lexer lex = new Lexer(text);
        for (Token token : lex) {
            System.out.println(token);
        }
        
        //Lexer lex2 = new Lexer("typedef const int[][] pid; typedef pid* pid_ptr;");
        Lexer lex2 = new Lexer("typedef struct name { int val1; long** val2, *val3; } name;\ntypedef struct name* " +
                "name_ptr;");
        for (Token token : lex2) {
            System.out.println(token);
        }
        Parser parser = new Parser(lex2);
        CategoryNode program = parser.parse();
        program.printTreeForm();
    
        ActionRoutineApplier applier = new ActionRoutineApplier();
        boolean b = applier.enactActionRoutine(program);
        if(b) {
            try {
                AbstractSyntaxNode completed = program.getSynthesized();
                completed.printTreeForm();
                System.out.println(applier.getSuccessOrder());
                System.out.println(completed.getRepresentation());
            } catch (SynthesizedMissingException e) {
                e.printStackTrace();
            }
        }
    }
}
