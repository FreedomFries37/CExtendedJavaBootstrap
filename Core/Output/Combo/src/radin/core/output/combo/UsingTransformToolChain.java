package radin.core.output.combo;

import radin.core.chaining.IToolChain;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.midanalysis.transformation.AbstractListBasedTransformer;
import radin.core.output.typeanalysis.errors.IdentifierDoesNotExistError;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.AmbiguousIdentifierError;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.Visibility;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.ICXClassType;
import radin.core.semantics.types.compound.ICXCompoundType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.core.utility.UniversalCompilerSettings;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UsingTransformToolChain extends AbstractListBasedTransformer {
    
    /**
     * Stores the <b>actual</b> class object that is defined.
     */
    private HashMap<CXIdentifier, CXClassType> identifierCXClassDictionary;
    private TypeEnvironment environment;
    private LinkedList<AbstractCompilationError> errors;
    private IToolChain<? super CXClassType, ? extends TypeAugmentedSemanticNode> classConverter;
    private IToolChain<? super AbstractSyntaxNode, ? extends TypeAugmentedSemanticNode> astToSemanticTreeChain;
    
    public UsingTransformToolChain(IToolChain<? super CXClassType, ? extends TypeAugmentedSemanticNode> classConverter) {
        this.classConverter = classConverter;
        identifierCXClassDictionary = new HashMap<>();
        errors = new LinkedList<>();
    }
    
    @Override
    public <V> void setVariable(String variable, V value) {
        switch (variable) {
            case "environment": {
                this.environment = (TypeEnvironment) value;
                return;
            }
        }
        super.setVariable(variable, value);
    }
    
    /**
     * Determines if the identifier has already been found
     * @param identifier the identifier of the class.
     * @return the corresponding CXClass object
     */
    public boolean identifierExists(CXIdentifier identifier) {
        return identifierCXClassDictionary.containsKey(identifier);
    }
    
    /**
     * Adds a created class to storage. If a class already exists, a {@link AmbiguousIdentifierError} will be thrown.
     * A class cannot be added if it doesn't belong to a namespace, and will return false if this is attempted.
     * @param type the type being added
     * @return true, unless {@code type} is null or part of the implicit namespace
     * @throws AmbiguousIdentifierError if adding this type will cause ambiguity
     */
    public boolean addClass(CXClassType type) throws AmbiguousIdentifierError  {
        if(type == null || type.getTypeNameIdentifier().getParentNamespace() == null) return false;
        CXIdentifier identifier = type.getTypeNameIdentifier();
        if(identifierCXClassDictionary.containsKey(type)) throw new AmbiguousIdentifierError(identifier.getIdentifier(),
                Collections.singletonList(identifierCXClassDictionary.get(identifier)));
        identifierCXClassDictionary.put(identifier, type);
        return true;
    }
    
    /**
     * Gets a modified version of the class definition, that transforms it for portability and enabling no
     * multi-declaration problems
     * <p>
     *     This by default removes all function definitions and replaces them with declarations, and with
     * </p>
     * @param identifier
     * @param newEnvironment
     * @return
     */
    public CXClassType getClassDeclarationFromUsingIdentifier(CXIdentifier identifier, TypeEnvironment newEnvironment) {
        return getClassDeclarationFromUsingIdentifier(identifier, identifier, newEnvironment);
    }
    
    /**
     * Gets a modified version of the class definition, that transforms it for portability and enabling no
     * multi-declaration problems
     * <p>
     *     This by default removes all function definitions and replaces them with declarations, and with
     * </p>
     * @param identifier
     * @param alias
     * @param newEnvironment
     * @return
     */
    public CXClassType getClassDeclarationFromUsingIdentifier(CXIdentifier identifier, CXIdentifier alias, TypeEnvironment newEnvironment) {
        if(!identifierExists(identifier)) throw new IdentifierDoesNotExistError(identifier.getIdentifierString());
        CXClassType original = identifierCXClassDictionary.get(identifier);
        TypeEnvironment e = original.getEnvironment();
        
        List<ICXCompoundType.FieldDeclaration> oldFields = original.getAllFields();
        List<ICXClassType.ClassFieldDeclaration> newFields = new LinkedList<>();
        int privateAddedCount = 0;
        boolean hide = UniversalCompilerSettings.getInstance().getSettings().isHideClassPrivateDeclarations();
       
        for (ICXCompoundType.FieldDeclaration oldField : oldFields) {
            Visibility visibility = original.getVisibility(oldField.getName());
            switch (visibility) {
                case _public:
                case internal:
                    newFields.add(new ICXClassType.ClassFieldDeclaration(oldField.getType(), oldField.getName(),
                            visibility));
                    break;
                case _private:
                    if(hide) {
                        long size = oldField.getType().getDataSize(e);
                        for (long i = 0; i < size; i++) {
                            String name = String.format("__private_byte_%x", privateAddedCount++);
                            newFields.add(new ICXClassType.ClassFieldDeclaration(
                                    CXPrimitiveType.CHAR,
                                    name,
                                    Visibility._private
                            ));
                        }
                    } else {
                        newFields.add(new ICXClassType.ClassFieldDeclaration(oldField.getType(), oldField.getName(),
                                visibility));
                    }
                    break;
                
            }
        }
        List<CXMethod> methods = new LinkedList<>();
        for (CXMethod method : original.getInstanceMethods()) {
            CXMethod newMethod = new CXMethod(
                    null,
                    method.getVisibility(),
                    method.getName().getIdentifier(),
                    method.isVirtual(),
                    method.getReturnType(),
                    method.getParameters(),
                    null
            );
            methods.add(newMethod);
        }
        
        CXClassType created = new CXClassType(
                identifier,
                newFields,
                methods,
                new LinkedList<>(),
                newEnvironment
        );
    
        
        List<CXConstructor> constructors = new LinkedList<>();
        for (CXConstructor constructor : original.getConstructors()) {
            CXConstructor newConstructor = new CXConstructor(
                    created,
                    constructor.getVisibility(),
                    constructor.getParameters(),
                    null,
                    null
            );
            constructors.add(newConstructor);
        }
        created.addConstructors(constructors);
        return created;
    }
    
    @Override
    protected TypeAugmentedSemanticNode transform() {
        CXIdentifier identifier = null;
        CXClassType dec = getClassDeclarationFromUsingIdentifier(identifier, environment);
        
        TypeAugmentedSemanticNode node = classConverter.invoke(dec);
        
        replace(getHead(), node);
        return node;
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return errors;
    }
    
    @Override
    public List getRelevant() {
        return getHead().getChildren();
    }
    
    @Override
    public boolean encapsulate(Object target, Object child) {
        if(!replace(target, child)) return false;
        child.addChild(target);
        return true;
    }
    
    @Override
    public Object next(Object target) {
        headStack.push(target.getParent());
        int targetIndex = indexOf(target);
        if(targetIndex == getRelevant().size() - 1) return null;
        Object output = getRelevant().get(targetIndex + 1);
        headStack.pop();
        return output;
    }
    
    @Override
    public Object previous(Object target) {
        headStack.push(target.getParent());
        int targetIndex = indexOf(target);
        if(targetIndex == 0) return null;
        TypeAugmentedSemanticNode output = getRelevant().get(targetIndex - 1);
        headStack.pop();
        return output;
    }
    
    @Override
    protected int indexOf(Object node) {
        headStack.push(node.getParent());
        int output = getRelevant().indexOf(node);
        headStack.pop();
        return output;
    }
}
