package radin.core.output.combo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import radin.core.chaining.ToolChainFactory;
import radin.core.input.FrontEndUnit;
import radin.core.input.IParser;
import radin.core.input.frontend.v1.lexing.PreProcessingLexer;
import radin.core.input.frontend.v1.parsing.ParseNode;
import radin.core.input.frontend.v1.parsing.Parser;
import radin.core.input.frontend.v1.semantics.ActionRoutineApplier;
import radin.core.lexical.Token;
import radin.core.output.backend.microcompilers.TopLevelDeclarationCompiler;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.midanalysis.TypeAugmentedSemanticTree;
import radin.core.output.midanalysis.typeanalysis.analyzers.ProgramTypeAnalyzer;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.utility.CompilationSettings;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;


class MultipleFileHandlerTest {
    
    private static ICompilationSettings<AbstractSyntaxNode, TypeAugmentedSemanticNode, Boolean> settings;
    
    @BeforeAll
    private static void init() {
        settings = new CompilationSettings<>();
    
        PreProcessingLexer lex = new PreProcessingLexer();
        IParser<Token, ParseNode> parser = new Parser();
        TypeEnvironment environment = TypeEnvironment.getStandardEnvironment();
        ActionRoutineApplier applier = new ActionRoutineApplier(environment);
    
        FrontEndUnit<Token, ParseNode, AbstractSyntaxNode> frontEndUnit = new FrontEndUnit<>(lex, parser, applier);
        settings.setFrontEndUnit(frontEndUnit);
    
    
        ToolChainFactory.ToolChainBuilder<AbstractSyntaxNode, TypeAugmentedSemanticNode> function = ToolChainFactory.function(
                (AbstractSyntaxNode o) -> TypeAugmentedSemanticTree.convertAST(o, environment)
        );
        ToolChainFactory.ToolChainBuilder<TypeAugmentedSemanticNode, TypeAugmentedSemanticNode> compilerAnalyzer = ToolChainFactory.compilerAnalyzer(
                new ProgramTypeAnalyzer((TypeAugmentedSemanticNode) null)
        );
        var midChain = function.chain_to(compilerAnalyzer);
        settings.setMidToolChain(midChain);
    
    
        var backChain = new TopLevelDeclarationCompiler(new PrintWriter(System.out));
        settings.setBackToolChain(backChain);
        UniversalCompilerSettings.getInstance().setSettings(settings);
    }
    
    private int numFilesCreated = 0;
    private File createTestFile(String content) {
        return createTestFile(String.format("temp%d", numFilesCreated++), content);
    }
    
    private File createTestFile(String name, String content) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(name, ".cx");
            tempFile.deleteOnExit();
    
            FileWriter writer = new FileWriter(tempFile);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }
    
    @BeforeEach
    private void resetNumFilesCreated() {
        numFilesCreated = 0;
    }
    
    
    @Test
    void testSingleFileCompile() {
        String content = "" +
                "" +
                "int main(int argc, char* args[]) {" +
                "   return 0;" +
                "}";
        
        File f = createTestFile(content);
        MultipleFileHandler multipleFileHandler = new MultipleFileHandler(Collections.singletonList(f), settings);
        assertTrue(multipleFileHandler.compileAll());
    }
    
    @RepeatedTest(100)
    void testUsingStatement() {
        String content1 = "in std class String {" +
                "private char* backing; " +
                "private int length;" +
                "public char* getBacking() { return this->backing; } " +
                "};";
        File definition = createTestFile(content1);
        String content2 = "using std::String;" +
                "int main(int argc, std::String args[]) {" +
                "   return 0;" +
                "}";
        File use = createTestFile(content2);
        MultipleFileHandler multipleFileHandler = new MultipleFileHandler(Arrays.asList(definition, use), settings);
        assertTrue(multipleFileHandler.compileAll());
    }
}