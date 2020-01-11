package radin.core.input;

import radin.core.errorhandling.ICompilationErrorCollector;
import radin.core.AbstractTree;

public interface ISemanticAnalyzer<P extends AbstractTree<? extends P>, S> extends ICompilationErrorCollector {
    S analyze(P tree);
}
