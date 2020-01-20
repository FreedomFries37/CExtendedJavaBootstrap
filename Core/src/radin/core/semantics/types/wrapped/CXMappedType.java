package radin.core.semantics.types.wrapped;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.exceptions.TypeDoesNotExist;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.ICXWrapper;
import radin.core.utility.ICompilationSettings;

public abstract class CXMappedType extends CXType implements ICXWrapper {
    
    
    protected Token corresponding;
    protected TypeEnvironment environment;
    protected CXType actual;
    
    public CXMappedType(Token corresponding, TypeEnvironment environment) {
        this.corresponding = corresponding;
        this.environment = environment;
    }
    
    public boolean update() {
        if(actual != null) return true;
        String old = toString();
        try {
            
            CXType type = getType();
            if(type == this) return false;
            if(type == null) return false;
            actual = type;
            ICompilationSettings.debugLog.info(old + " updated to " + actual);
            return true;
        }catch (TypeNotPresentException e) {
            ICompilationSettings.debugLog.throwing(CXMappedType.class.getSimpleName(),
                    "update()",
                    e);
            return false;
        } catch (TypeDoesNotExist e) {
            ICompilationSettings.debugLog.finer(old + " not yet updated");
            return false;
        }
        
    }
    
    protected abstract CXType getType();
    
    @Override
    public boolean isPrimitive() {
        if(!update()) throw new BadDelayedTypeAccessError();
        return actual.isPrimitive();
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        if(!update()) throw new BadDelayedTypeAccessError();
        return actual.getDataSize(e);
    }
    
    @Override
    public CXType getWrappedType() {
        return actual;
    }
    
    public class BadDelayedTypeAccessError extends AbstractCompilationError {
    
        public BadDelayedTypeAccessError() {
            super(corresponding, "This type never created");
        }
    }
}
