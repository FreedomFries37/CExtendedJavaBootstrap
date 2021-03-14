package radin.output.typeanalysis;

import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.utility.Option;
import radin.output.typeanalysis.errors.IdentifierDoesNotExistError;

public interface IVariableTypeTracker {
    
    enum NameType {
        LOCAL,
        GLOBAL
    }
    
    /**
     * Adds a function to track
     * @param identifier The relative id
     * @param functionPointer
     * @return
     */
    CXIdentifier addFunction(CXIdentifier identifier, CXFunctionPointer functionPointer, boolean isDefinition);

    /**
     * Adds a function to track
     * @param identifier The relative id
     * @param returnType the return type
     */
    void addFunction(CXIdentifier identifier, CXType returnType, boolean isDefinition);
    
    /**
     * Adds a local variable to the tracker
     * @param name
     * @param type
     */
    void addLocalVariable(String name, CXType type);
    
    /**
     * Adds a global variable to the tracker
     * @param identifier
     * @param type
     * @return
     */
    CXIdentifier addGlobalVariable(CXIdentifier identifier, CXType type);
    

    
    /**
     * Determine if a name refers to a local or global variable, where local variables have precedence
     * @param name
     * @return the type for the name
     */
    default NameType getVariableTypeForName(String name) {
        if(localVariableExists(name)) return NameType.LOCAL;
        if(globalVariableExists(CXIdentifier.from(name))) return NameType.GLOBAL;
        throw new IdentifierDoesNotExistError(name);
    }

    /**
     * Checks to see if `name` is a valid identifier
     * @param name
     * @return Where `name` exists, whether its local or global
     */
    default boolean idExists(String name) {
        CXIdentifier id = CXIdentifier.from(name);
        return localVariableExists(name) || globalVariableExists(id) || functionExists(id);
    }
    
    /**
     * Checks whether such an identifier exists, which can be a function or a global variable
     * @param id
     * @return
     */
    default boolean idExists(CXIdentifier id) {
        return globalVariableExists(id) || functionExists(id);
    }
    
    /**
     * Checks if a local variable exists
     * @param id
     * @return
     */
    boolean localVariableExists(String id);
    
    /**
     * Checks if a function exists
     * @param id
     * @return
     */
    boolean functionExists(CXIdentifier id);
    
    /**
     * Checks if a function exists
     * @param id
     * @return
     */
    boolean globalVariableExists(CXIdentifier id);
    
    /**
     * Resolves a potentially relative identifier into an absolute identifier
     * @param id a potentially relative identifier
     * @return
     */
    CXIdentifier resolveIdentifier(CXIdentifier id);
    Option<CXIdentifier> tryResolveIdentifier(CXIdentifier id);
    
    /**
     * Checks to see if a string name can be interpreted as an identifier
     * @param name
     * @return Either None if not a global/function, or a full path
     */
    Option<CXIdentifier> tryResolveFromName(String name);

    
    /**
     * Gets the type of a local variable
     * @param name the name of the local variable
     * @return
     */
    CXType getLocalVariableType(String name);
    
    /**
     * Gets a function pointer for an id
     * @param id
     * @return
     */
    CXFunctionPointer getFunctionType(CXIdentifier id);
    
    /**
     * Gets the type of a global variable
     * @param id
     * @return
     */
    CXType getGlobalVariableType(CXIdentifier id);
    
    default CXType getType(String name) {
        switch (getVariableTypeForName(name)) {
    
            case LOCAL: {
                return getLocalVariableType(name);
            }
            case GLOBAL:  {
                CXIdentifier resolved = resolveIdentifier(CXIdentifier.from(name));
                return getGlobalVariableType(resolved);
            }
            default: {
                throw new IdentifierDoesNotExistError(name);
            }
        }
    }
    default CXType getType(CXIdentifier identifier) {
        return getGlobalVariableType(identifier);
    }
    
    
    
    
    
    
}
