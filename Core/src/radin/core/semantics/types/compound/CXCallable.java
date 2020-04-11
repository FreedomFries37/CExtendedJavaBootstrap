package radin.core.semantics.types.compound;

import radin.core.lexical.Token;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.utility.Reference;

import java.util.List;

public interface CXCallable {
    
    List<CXMethod> getAllMethods();
    
    CXMethod getMethod(Token name, ParameterTypeList parameterTypeList, Reference<Boolean> isVirtual);
}
