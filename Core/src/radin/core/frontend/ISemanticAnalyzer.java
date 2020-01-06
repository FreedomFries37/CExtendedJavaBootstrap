package radin.core.frontend;

import radin.core.AbstractTree;
import radin.core.errorhandling.ICompilationErrorCollector;

public interface ISemanticAnalyzer<P extends AbstractTree<? extends P>, S> extends ICompilationErrorCollector {
    S analyze(P tree);
}
