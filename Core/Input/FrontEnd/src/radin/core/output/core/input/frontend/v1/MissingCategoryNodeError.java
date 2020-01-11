package radin.core.output.core.input.frontend.v1;

import radin.core.output.core.input.frontend.v1.parsing.CategoryNode;

public class MissingCategoryNodeError extends Error {
    public MissingCategoryNodeError(CategoryNode node, String missing) {
        super("Node " + node + " does not have child of category " + missing);
    }
    
    public MissingCategoryNodeError(CategoryNode node, String missing, int num) {
        super("Node " + node + " does not have child " + num + " of category " + missing);
    }
}
