package radin.frontend.v1;

import radin.frontend.v1.parsing.ParseNode;

public class InheritMissingError extends Error {
    public InheritMissingError(ParseNode n) {
        super("Node " + n.toString() + " missing inherit");
    }
}
