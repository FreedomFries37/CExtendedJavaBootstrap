package radin.semantics;

import radin.parsing.ParseNode;

public class SynthesizedMissingException extends Exception {
    public final ParseNode node;
    
    public SynthesizedMissingException(ParseNode node) {
        super("Node " + node.toString() + " missing synthesized");
        this.node = node;
    }
}
