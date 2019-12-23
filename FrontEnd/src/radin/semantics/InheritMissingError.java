package radin.semantics;

import radin.parsing.ParseNode;

public class InheritMissingError extends Error {
    public InheritMissingError(ParseNode n) {
        super("Node " + n.toString() + " missing inherit");
    }
}
