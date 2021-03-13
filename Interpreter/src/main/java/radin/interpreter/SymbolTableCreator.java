package radin.interpreter;

import radin.core.SymbolTable;
import radin.core.chaining.IToolChain;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXIdentifier;
import radin.output.tags.ImplementMethodTag;
import radin.output.tags.ResolvedPathTag;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SymbolTableCreator implements IToolChain<TypeAugmentedSemanticNode, SymbolTable<CXIdentifier,
        TypeAugmentedSemanticNode>> {

    private List<AbstractCompilationError> errors = new LinkedList<>();
    private String file;

    @Override
    public <V> void setVariable(String variable, V value) {
        switch (variable) {
            case "file": {
                this.file = (String) value;
                break;
            }
        }

    }

    @Override
    public SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> invoke(TypeAugmentedSemanticNode input) {
        SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> output = new SymbolTable<>();
        List<TypeAugmentedSemanticNode> functionDefinitions = input.getAllChildren(ASTNodeType.function_definition);

        // Creates symbols for all functions
        for (TypeAugmentedSemanticNode functionDefinition : functionDefinitions) {
            if(!functionDefinition.containsCompilationTag(ImplementMethodTag.class)) {
                if(functionDefinition.getChild(0).getASTType() != ASTNodeType._virtual) {
                    CXIdentifier resolved = functionDefinition.getASTChild(ASTNodeType.id).getCompilationTag(ResolvedPathTag.class).getAbsolutePath();
                    output.put(
                            output.new Key(resolved,
                                    file, functionDefinition.findFirstToken()),
                            functionDefinition
                    );
                }
            }
        }
        String f = new File(file).getName();
        f = f.replaceAll("(\\..+)+", "");
        CXIdentifier parentIdentifier = new CXIdentifier(new Token(TokenType.t_id, f)
        );
        for (TypeAugmentedSemanticNode decs : input.getAllChildren(ASTNodeType.declarations)) {
            for (TypeAugmentedSemanticNode dec : decs.getAllChildren(ASTNodeType.declaration, 1)) {
                if(dec.getASTChild(ASTNodeType.id).containsCompilationTag(ResolvedPathTag.class)) {
                    CXIdentifier id = dec.getASTChild(ASTNodeType.id).getCompilationTag(ResolvedPathTag.class).getAbsolutePath();
                    TypeAugmentedSemanticNode value = new TypeAugmentedSemanticNode(new AbstractSyntaxNode(ASTNodeType.empty));
                    value.setType(dec.getCXType());
                    output.put(output.new Key(
                                    id,
                                    file,
                                    dec.getASTChild(ASTNodeType.id).getToken(),
                                    dec.getCXType()
                            ),
                            value);
                }
            }
            for (TypeAugmentedSemanticNode dec : decs.getAllChildren(ASTNodeType.initialized_declaration)) {
                TypeAugmentedSemanticNode innerDec = dec.getChild(0);
                if(innerDec.getASTChild(ASTNodeType.id).containsCompilationTag(ResolvedPathTag.class)) {
                    CXIdentifier id = innerDec.getASTChild(ASTNodeType.id).getCompilationTag(ResolvedPathTag.class).getAbsolutePath();
                    //Token id = dec.getASTChild(ASTNodeType.declaration).getASTChild(ASTNodeType.id).getToken();
                    TypeAugmentedSemanticNode val = dec.getChild(1);

                    output.put(output.new Key(
                                    id,
                                    file,
                                    id.getBase(),
                                    dec.getCXType()
                            ),
                            val);
                }
            }
        }

        for (TypeAugmentedSemanticNode allChild : input.getAllChildren(ASTNodeType.top_level_decs, 1)) {
            if(allChild == input) continue;
            output = new SymbolTable<>(Arrays.asList(output, invoke(allChild)));
        }
        return output;
    }

    @Override
    public List<AbstractCompilationError> getErrors() {
        return errors;
    }
}
