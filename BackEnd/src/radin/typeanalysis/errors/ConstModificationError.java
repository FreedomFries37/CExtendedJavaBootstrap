package radin.typeanalysis.errors;

import radin.interphase.semantics.types.CXType;
import radin.typeanalysis.errors.IllegalAccessError;

import javax.xml.stream.util.EventReaderDelegate;

public class ConstModificationError extends Error {
    
    public ConstModificationError(String id, CXType type) {
        super("Can't modify " + type + " " + id);
    }
    
    public ConstModificationError(CXType type) {
        super("Can't modify " + type);
    }
}
