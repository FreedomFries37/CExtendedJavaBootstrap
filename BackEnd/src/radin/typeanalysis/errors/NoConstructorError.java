package radin.typeanalysis.errors;

import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.methods.ParameterTypeList;

public class NoConstructorError extends Error {
    
    public NoConstructorError(CXClassType owner, ParameterTypeList parameterTypeList) {
        super("No constructor exists for " + owner + " with parameters" + parameterTypeList);
    }
}
