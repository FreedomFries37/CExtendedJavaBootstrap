package radin.core.output.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.CXType;

import java.util.List;

public class IncorrectMainDefinition extends AbstractCompilationError {
    
    public IncorrectMainDefinition(Token mainAttempt) {
        super("Main has incorrect signature", mainAttempt, "Should be int main(int, std::String[])");
    }
}
