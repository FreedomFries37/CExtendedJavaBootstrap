package radin.midanalysis.typeanalysis.errors;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;

public class IncorrectMainDefinition extends AbstractCompilationError {
    
    public IncorrectMainDefinition(Token mainAttempt) {
        super("Main has incorrect signature", mainAttempt, "Should be int main(int, std::String[])");
    }
}
