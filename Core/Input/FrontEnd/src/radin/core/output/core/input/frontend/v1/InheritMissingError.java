package radin.core.output.core.input.frontend.v1;

import radin.core.output.core.input.frontend.v1.parsing.ParseNode;

public class InheritMissingError extends Error {
    public InheritMissingError(ParseNode n) {
        super("Node " + n.toString() + " missing inherit");
    }
}
