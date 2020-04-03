package radin.frontend.v1.semantics;

import radin.frontend.v1.parsing.ParseNode;

public class SynthesizedMissingException extends Exception {
    public final ParseNode node;
    
    public SynthesizedMissingException(ParseNode node) {
        super("Node " + node.toString() + " missing synthesized");
        this.node = node;
    }
}
