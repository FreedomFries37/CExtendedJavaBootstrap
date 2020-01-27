package radin.core.input.frontend.v1.lexing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PreProcessingLexerTest {
    
    @Test
    void run() {
        String text = "using std::String;int main(int argc, std::String args[])";
        PreProcessingLexer lexer = new PreProcessingLexer("test", text);
        int run = lexer.run();
        
        // should create 18 tokens
        assertEquals(run, 18);
        
    }
}