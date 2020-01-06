package radin.v1;

import radin.v1.parsing.ParseNode;

public class InheritMissingError extends Error {
    public InheritMissingError(ParseNode n) {
        super("Node " + n.toString() + " missing inherit");
    }
}
