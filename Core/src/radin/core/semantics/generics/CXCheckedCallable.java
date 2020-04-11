package radin.core.semantics.generics;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.types.compound.CXCallable;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.utility.Reference;

public interface CXCheckedCallable extends CXCallable {
    
    /**
     * Forces the compiler to check for
     */
    class GetMethodError extends Exception {
        private Token name;
        private ParameterTypeList inputTypes;
        
        
        public GetMethodError(AbstractCompilationError base, Token name, ParameterTypeList inputTypes) {
            super(base);
            this.name = name;
            this.inputTypes = inputTypes;
        }
    
        public GetMethodError(Token name, ParameterTypeList inputTypes) {
            super("No method found");
            this.name = name;
            this.inputTypes = inputTypes;
        }
    
        public Token getName() {
            return name;
        }
    
        public ParameterTypeList getInputTypes() {
            return inputTypes;
        }
    }
    
    CXMethod getMethodChecked(Token name, ParameterTypeList parameterTypeList, Reference<Boolean> isVirtual)
        throws GetMethodError;
    
    @Override
    default CXMethod getMethod(Token name, ParameterTypeList parameterTypeList, Reference<Boolean> isVirtual) {
        try {
            return getMethodChecked(name, parameterTypeList, isVirtual);
        } catch (GetMethodError getMethodError) {
            return null;
        }
    }
}
