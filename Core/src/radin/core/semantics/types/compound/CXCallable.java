package radin.core.semantics.types.compound;

import radin.core.lexical.Token;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.utility.Reference;

public interface CXCallable {
    
    CXMethod getMethod(Token name, ParameterTypeList parameterTypeList, Reference<Boolean> isVirtual);
}
