package radin;

import radin.core.output.core.input.frontend.v2.parsing.structure.*;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.output.core.input.frontend.v2.lexing.BasicLexer;
import radin.core.output.core.input.frontend.v2.parsing.grammars.StandardCGrammar;

import java.util.Map;
import java.util.Set;

public class GrammarTester {
    
    
    public static void main(String[] args) {
        GrammarBuilder<TokenType> grammar = new StandardCGrammar();
        grammar.print();
        if(!grammar.valid()) {
            
            System.out.println("INVALID SYMBOLS: " + grammar.invalidSymbols());
    
            return;
        }
        SLRData<TokenType> tokenSLRData = grammar.toData(TokenType.t_eof);
        tokenSLRData.generateFirstSets();
        for (Map.Entry<ParsableObject<?>, Set<NonTerminal<TokenType>>> parsableObjectSetEntry : tokenSLRData.getFirstSet().entrySet()) {
            System.out.println(parsableObjectSetEntry);
        }
        System.out.println();
        for (Symbol ep : tokenSLRData.getEps()) {
            System.out.println(ep);
        }
        tokenSLRData.generateFollowSet();
        System.out.println();
        for (Map.Entry<ParsableObject<?>, Set<NonTerminal<TokenType>>> parsableObjectSetEntry : tokenSLRData.getFollowSet().entrySet()) {
            System.out.println(parsableObjectSetEntry);
        }
        
        tokenSLRData.generateParseTable();
        tokenSLRData.printParseTable();
    
        String inputString = "int main(int argc, char* argv[]) {" +
                "return 5+4;" +
                "}";
        BasicLexer lexer = new BasicLexer(inputString, "test");
        
        
        SLRParser<Token, TokenType> slrParser = new SLRParser<>(
                Token::getType,
                lexer,
                tokenSLRData
        );
        slrParser.parse().printTreeForm();
    }
}
