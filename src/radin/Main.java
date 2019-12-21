package radin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import radin.lexing.Lexer;
import radin.lexing.Token;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("Testing out lexer");
        try {
            String text = new String(Files.readAllBytes(Paths.get("test.cx")));
            Lexer lex = new Lexer(text);
            for (Token token : lex) {
                System.out.println(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
