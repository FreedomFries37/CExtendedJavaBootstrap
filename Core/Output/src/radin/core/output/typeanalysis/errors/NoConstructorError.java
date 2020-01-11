package radin.core.output.typeanalysis.errors;

import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.semantics.types.compound.CXClassType;

public class NoConstructorError extends Error {
    
    public NoConstructorError(CXClassType owner, ParameterTypeList parameterTypeList) {
        super("No constructor exists for " + owner + " with parameters" + parameterTypeList);
    }
}
