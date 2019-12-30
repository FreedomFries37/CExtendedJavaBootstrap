package radin.typeanalysis.errors;

import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.methods.ParameterTypeList;

public class NoConstructorError extends Error {
    
    public NoConstructorError(CXClassType owner, ParameterTypeList parameterTypeList) {
        super("No constructor exists for " + owner + " with parameters" + parameterTypeList);
    }
}
