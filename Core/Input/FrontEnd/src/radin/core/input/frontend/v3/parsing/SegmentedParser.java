package radin.core.input.frontend.v3.parsing;

import radin.core.input.IParser;
import radin.core.input.frontend.v1.lexing.PreProcessingLexer;
import radin.core.input.frontend.v1.parsing.BasicParser;
import radin.core.input.frontend.v1.parsing.ParseNode;
import radin.core.input.frontend.v1.semantics.ActionRoutineApplier;
import radin.core.lexical.Token;
import radin.core.semantics.ASTMeaningfulNode;

/**
 * This parser is intended to be cooperative with the {@link PreProcessingLexer}
 * and but incompatible with the v1 {@link ActionRoutineApplier}, and as such, uses the same grammar
 * <p>
 *     It differs in implementation. Instead of the single parse used by the traditional {@link BasicParser}, it
 *     instead is free to use any sort of parsing implementation on smaller segments of the code, which are parsed in
 *     lexical order. This allows for a combination of both LR and LL parsing, when necessary
 * </p>
 */
public class SegmentedParser implements IParser<Token, ParseNode> {
    
    public enum SegmentType {
    
    }
    
    protected class SegmentTree extends ASTMeaningfulNode<>
}
