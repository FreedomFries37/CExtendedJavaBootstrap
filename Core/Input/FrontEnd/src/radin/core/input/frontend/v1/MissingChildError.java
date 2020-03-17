package radin.core.input.frontend.v1;

import radin.core.input.frontend.v1.parsing.ParseNode;

public class MissingChildError extends Error {
    public MissingChildError(ParseNode parent, int index) {
        super(parent.toString() + " has no child at index " + index);
    }
}
