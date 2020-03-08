package radin.core.output.backend.interpreter;

import radin.core.SymbolTable;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.errorhandling.ICompilationErrorCollector;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.ICXWrapper;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.ICXCompoundType;
import radin.core.semantics.types.primitives.*;

import java.util.*;

public class Interpreter implements ICompilationErrorCollector {
    
    abstract class Instance<T extends CXType> {
        private T type;
        
        public Instance(T type) {
            this.type = type;
        }
        
        public T getType() {
            return type;
        }
        
        public PointerInstance<T> toPointer() {
            return new PointerInstance<>(getType().toPointer(), this);
        }
    
        @Override
        public String toString() {
            return "Instance{" +
                    "type=" + type +
                    '}';
        }
        
        abstract void copyFrom(Instance<?> other);
    }
    
    class PrimitiveInstance<R, P extends AbstractCXPrimitiveType> extends Instance<P> {
        private R backingValue;
        private boolean unsigned;
        
        public PrimitiveInstance(P type, R backingValue, boolean unsigned) {
            super(type);
            this.backingValue = backingValue;
            this.unsigned = unsigned;
        }
        
        public R getBackingValue() {
            return backingValue;
        }
        
        public void setBackingValue(R backingValue) {
            this.backingValue = backingValue;
        }
    
        @Override
        public String toString() {
            return "(" + getType() + ") " + backingValue;
        }
    
        @Override
        void copyFrom(Instance<?> other) {
            assert other instanceof PrimitiveInstance;
            
            this.backingValue = ((PrimitiveInstance<R, P>) other).backingValue;
        }
    }
    
    
    
    
    
    class ArrayInstance<R extends CXType, T extends ArrayType> extends PrimitiveInstance<ArrayList<Instance<R>>, T> {
        private int size;
        
        public ArrayInstance(T type, int size) {
            super(type, new ArrayList<>(size), true);
            for (int i = 0; i < size; i++) {
                getBackingValue().add(null);
            }
        }
        
        public ArrayInstance(T type, ArrayList<Instance<R>> other) {
            super(type, other, true);
        }
        
        public Instance<R> getAt(int index) {
            return getBackingValue().get(index);
        }
        
        public void setAt(int index, Instance<R> value) {
            getBackingValue().set(index, value);
        }
        
        public PointerInstance<R> asPointer() {
            return new PointerInstance<>(new PointerType(getType().getBaseType()), getBackingValue(), 0);
        }
        
        public int size() {
            return size;
        }
    
        @Override
        public String toString() {
            return "ArrayInstance{" +
                    "type=" + getType() +
                    ", size=" + size +
                    '}';
        }
        
        
    }
    
    
    class PointerInstance<R extends CXType> extends ArrayInstance<R, PointerType> {
        private int index = 0;
        public PointerInstance(PointerType type, Instance<R> pointer) {
            super(type, 1);
            setAt(0, pointer);
        }
        
        public PointerInstance(PointerType type, ArrayList<Instance<R>> backing, int offset) {
            super(type, backing);
            index = offset;
        }
        
        
        public PointerInstance<R> getPointerOfOffset(int offset) {
            return new PointerInstance<>(getType(), getBackingValue(), index + offset);
        }
        
        public PointerInstance(PointerType type) {
            super(type, 1);
        }
        
        
        public Instance<R> getPointer() {
            return getAt(index);
        }
        
        public void setPointer(Instance<R> pointer) {
            setAt(index, pointer);
        }
    
        @Override
        public String toString() {
            return "PointerInstance{" +
                    "type=" + getType() +
                    '}';
        }
        
        
    }
    
    public class CompoundInstance<T extends CXCompoundType> extends Instance<T> {
        private HashMap<String, Instance<?>> fields;
        
        public CompoundInstance(T type) {
            super(type);
            this.fields = new HashMap<>();
            for (ICXCompoundType.FieldDeclaration field : type.getFields()) {
                fields.put(field.getName(), createNewInstance(field.getType()));
            }
        }
        
        public <R extends CXType, I extends Instance<R>> I get(String key) {
            return (I) fields.get(key);
        }
    
        @Override
        void copyFrom(Instance<?> other) {
            assert other instanceof CompoundInstance;
    
            for (String s : fields.keySet()) {
                this.fields.replace(s, ((CompoundInstance<CXCompoundType>) other).fields.get(s));
            }
        }
    }
    
    public <R, T extends AbstractCXPrimitiveType> PrimitiveInstance<R, T> createNewInstance(T type, R backing) {
        PrimitiveInstance<R, T> instance = (PrimitiveInstance<R, T>) createNewInstance(type);
        instance.setBackingValue(backing);
        return instance;
    }
    
    public <T extends CXType> Instance<?> createNewInstance(T type) {
        if(type instanceof ICXWrapper) return createNewInstance(((ICXWrapper) type).getWrappedType());
        
        if (type instanceof PointerType) {
            return new PointerInstance<>(((PointerType) type));
        }else if (type instanceof ArrayType) {
            // TODO: implement proper array sizing
            return new ArrayInstance<>(((ArrayType) type), 15);
        }else if (type instanceof AbstractCXPrimitiveType) {
            boolean unsigned = false;
            AbstractCXPrimitiveType fixed = ((AbstractCXPrimitiveType) type);
            if(fixed instanceof UnsignedPrimitive) {
                unsigned = true;
                fixed = ((UnsignedPrimitive) fixed).getPrimitiveCXType();
            }
            
            if(unsigned) return new PrimitiveInstance<>(fixed, 0, true);
            return new PrimitiveInstance<>(fixed, 0, false);
        } else if (type instanceof CXCompoundType) {
            return new CompoundInstance<>(((CXCompoundType) type));
        }
        return null;
    }
    
    private TypeEnvironment environment;
    private SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> symbols;
    
    private Stack<HashMap<String, Instance<?>>> autoVariables = new Stack<>();
    private Instance<?> returnValue;
    private Stack<Instance<?>> memStack = new Stack<>();
    private Stack<Integer> previousMemStackSize = new Stack<>();
    
    
    
    public void addAutoVariable(String name, Instance<?> value) {
        autoVariables.peek().put(name, value);
    }
    
    public <T extends CXType> Instance<T> getAutoVariable(String name) {
        return ((Instance<T>) autoVariables.peek().get(name));
    }
    
    public void createClosure() {
        autoVariables.push(new HashMap<>());
        previousMemStackSize.push(memStack.size());
    }
    
    public void startLexicalScope() {
        autoVariables.push(new HashMap<>(autoVariables.peek()));
    }
    
    public void endLexicalScope() {
        autoVariables.pop();
    }
    
    public void endClosure() {
        autoVariables.pop();
        int previousSize = previousMemStackSize.pop();
        while (memStack.size() > previousSize) {
            memStack.pop();
        }
    }
    
    public void push(Instance<?> val) {
        memStack.push(val);
    }
    
    public Instance<?> pop() {
        return memStack.pop();
    }
    
    public Interpreter(TypeEnvironment environment, SymbolTable<CXIdentifier, TypeAugmentedSemanticNode> symbols) {
        this.environment = environment;
        this.symbols = symbols;
        autoVariables.add(new HashMap<>());
        for (Map.Entry<SymbolTable<CXIdentifier, TypeAugmentedSemanticNode>.Key, TypeAugmentedSemanticNode> symbol : this.symbols) {
            if(symbol.getValue().getASTType() == ASTNodeType.function_definition) {
            
            } else if(symbol.getValue().getASTType() != ASTNodeType.constructor_definition) {
                // is a value;
                try {
                    if(!invoke(symbol.getValue())) throw new IllegalStateException();
                    addAutoVariable(symbol.getKey().getToken().getImage(), pop());
                } catch (FunctionReturned functionReturned) {
                    throw new IllegalStateException();
                }
            }
        }
    }
    
    protected Instance<?> getInstance(TypeAugmentedSemanticNode node) {
        switch (node.getASTType()) {
            case id: {
                String name = node.getToken().getImage();
                for (int i = autoVariables.size() - 1; i >= 0; i--) {
                    if(autoVariables.get(i).containsKey(name)) {
                        return autoVariables.get(i).get(name);
                    }
                }
            }
        }
        return null;
    }
    
    private class FunctionReturned extends Throwable {
    
    }
    
    private TypeAugmentedSemanticNode getSymbol(String s) {
        return symbols.get(new CXIdentifier(new Token(TokenType.t_id, s), false));
    }
    
    public <T extends CXType> ArrayInstance<T, ArrayType> createArray(T type, int size) {
        return new ArrayInstance<>(new ArrayType(type), size);
    }
    
    public ArrayInstance<CXPrimitiveType,ArrayType> createCharArrayFromString(String s) {
        ArrayInstance<CXPrimitiveType, ArrayType> output = new ArrayInstance<>(new ArrayType(CXPrimitiveType.CHAR),
                s.length() + 1);
        for (int i = 0; i < output.size() - 1; i++) {
            output.setAt(
                    i,
                    createNewInstance(CXPrimitiveType.CHAR, s.charAt(i))
            );
        }
        output.setAt(output.size - 1, createNewInstance(CXPrimitiveType.CHAR, '0'));
        return output;
    }
    
    public PointerInstance<CXPrimitiveType> createCharPointerFromString(String s) {
        return createCharArrayFromString(s).asPointer();
    }
    
    
    
    public int run(String[] args) {
        try {
            createClosure();
            
            TypeAugmentedSemanticNode main = getSymbol("main");
            push(createNewInstance(CXPrimitiveType.INTEGER, args.length));
            ArrayInstance<PointerType, ArrayType> argv = createArray(CXPrimitiveType.CHAR.toPointer(), args.length);
            for (int i = 0; i < args.length; i++) {
                argv.setAt(i, createCharPointerFromString(args[i]));
            }
            push(argv);
            if(!invoke(main)) return -1;
            endClosure();
        } catch (FunctionReturned e) {
            return ((PrimitiveInstance<Integer, CXPrimitiveType>) returnValue).backingValue;
        }
        return -1;
    }
    
    private Number opOnFloatingPoint(TokenType op, double lhs, double rhs) {
        switch (op) {
            case t_dand:
                return lhs != 0 && rhs != 0? 1 : 0;
            case t_dor:
                return lhs != 0 || rhs != 0? 1 : 0;
            case t_lte:
                return lhs <= rhs? 1 : 0;
            case t_gte:
                return lhs >= rhs? 1 : 0;
            case t_eq:
                return lhs == rhs? 1 : 0;
            case t_neq:
                return lhs != rhs? 1 : 0;
            case t_minus:
                return lhs - rhs;
            case t_add:
                return lhs + rhs;
            case t_star:
                return lhs * rhs;
            case t_fwslash:
                return lhs / rhs;
            case t_percent:
                return lhs % rhs;
            case t_lt:
                return lhs < rhs? 1 : 0;
            case t_gt:
                return lhs > rhs? 1 : 0;
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    private Number opOnIntegral(TokenType op, long lhs, long rhs) {
        switch (op) {
            case t_dand:
                return lhs != 0 && rhs != 0? 1 : 0;
            case t_dor:
                return lhs != 0 || rhs != 0? 1 : 0;
            case t_lte:
                return lhs <= rhs? 1 : 0;
            case t_gte:
                return lhs >= rhs? 1 : 0;
            case t_eq:
                return lhs == rhs? 1 : 0;
            case t_neq:
                return lhs != rhs? 1 : 0;
            case t_minus:
                return lhs - rhs;
            case t_add:
                return lhs + rhs;
            case t_star:
                return lhs * rhs;
            case t_fwslash:
                return lhs / rhs;
            case t_percent:
                return lhs % rhs;
            case t_lt:
                return lhs < rhs? 1 : 0;
            case t_gt:
                return lhs > rhs? 1 : 0;
            case t_lshift:
                return lhs << rhs;
            case t_rshift:
                return lhs >> rhs;
            case t_bar:
                return lhs | rhs;
            case t_and:
                return lhs & rhs;
            case t_crt:
                return lhs ^ rhs;
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    
    public Boolean invoke(TypeAugmentedSemanticNode input) throws FunctionReturned {
        switch (input.getASTType()) {
            case operator:
                break;
            case binop: {
                Token op = input.getChild(0).getToken();
                if (!invoke(input.getChild(1))) return false;
                PrimitiveInstance<? extends Number, ?> lhs =
                        (PrimitiveInstance<? extends Number, ?>) pop();
                if (!invoke(input.getChild(2))) return false;
                PrimitiveInstance<? extends Number, ?> rhs =
                        (PrimitiveInstance<? extends Number, ?>) pop();
    
                if (lhs.getType().isFloatingPoint()) {
                    push(new PrimitiveInstance<>(lhs.getType(),
                            opOnFloatingPoint(op.getType(), lhs.backingValue.doubleValue(),
                                    rhs.backingValue.doubleValue()),
                            lhs.unsigned));
                } else {
                    push(new PrimitiveInstance<>(lhs.getType(), opOnIntegral(op.getType(),
                            lhs.getBackingValue().longValue(), rhs.getBackingValue().longValue()), lhs.unsigned));
                }
            }
                break;
            case uniop:
                break;
            case declaration:
                break;
            case assignment: {
                if (!invoke(input.getChild(0))) return false;
                // LHS should be pushed
                Instance<?> lhs = pop();
                if (!invoke(input.getChild(2))) return false;
                Instance<?> rhs = pop();
    
                Token assignmentToken = input.getASTChild(ASTNodeType.assignment_type).getToken();
                if(assignmentToken.getType() == TokenType.t_eq) {
                    lhs.copyFrom(rhs);
                } else if(assignmentToken.getType() == TokenType.t_operator_assign) {
                
                } else return false;
            }
                break;
            case assignment_type:
                break;
            case ternary:
                break;
            case array_reference:
                break;
            case postop:
                break;
            case literal:
                push(getInstance(input));
                break;
            case id:
                push(getInstance(input));
                break;
            case string:
                push(getInstance(input));
                break;
            case sequence:
                for (TypeAugmentedSemanticNode entry : input.getDirectChildren()) {
                    if(!invoke(entry)) return false;
                }
                break;
            case typename:
                break;
            case parameter_list:
                break;
            case function_call:
                
                if(!invoke(input.getASTChild(ASTNodeType.sequence))) return false;
                TypeAugmentedSemanticNode function = getSymbol(input.getASTChild(ASTNodeType.id).getToken().getImage());
                try {
                    if (!invoke(function)) return false;
                } catch (FunctionReturned functionReturned) {
                    push(returnValue);
                    returnValue = null;
                }
                
                break;
            case method_call:
                // owner is pushed to stack
                if(!invoke(input.getChild(0))) return false;
                CompoundInstance<CXClassType> classTypeInstance = ((CompoundInstance<CXClassType>) pop());
                createClosure();
                addAutoVariable("this", classTypeInstance);
                
                
                endClosure();
                break;
            case field_get:
                if(!invoke(input.getChild(0))) return false;
                // owner on stack
            {
                CompoundInstance<?> compoundInstance = (CompoundInstance<?>) pop();
                String field = input.getASTChild(ASTNodeType.id).getToken().getImage();
                
                // push field
                push(compoundInstance.get(field));
            }
                break;
            case if_cond:
                break;
            case while_cond:
                break;
            case do_while_cond:
                break;
            case for_cond:
                break;
            case _return:
                if(input.getChildren().size() > 0) {
                    returnValue = getInstance(input.getChild(0));
                }
                throw new FunctionReturned();
            case function_definition:
                createClosure();
                List<TypeAugmentedSemanticNode> parameters = input.getASTChild(ASTNodeType.parameter_list).getChildren();
                for (int i = parameters.size() - 1; i >= 0; i--) {
                    addAutoVariable(
                            parameters.get(i).getASTChild(ASTNodeType.id).getToken().getImage(),
                            pop()
                    );
                }
                
                if(!invoke(input.getASTChild(ASTNodeType.compound_statement))) return false;
                
                endClosure();
                break;
            case basic_compound_type_dec:
                break;
            case specifiers:
                break;
            case specifier:
                break;
            case qualifier:
                break;
            case qualifiers:
                break;
            case qualifiers_and_specifiers:
                break;
            case class_level_decs:
                break;
            case class_type_definition:
                break;
            case class_type_declaration:
                break;
            case class_type_name:
                break;
            case compound_type_reference:
                startLexicalScope();
                for (TypeAugmentedSemanticNode child : input.getChildren()) {
                    if(!invoke(child)) return false;
                }
                endLexicalScope();
                break;
            case typedef:
                break;
            case top_level_decs:
                break;
            case indirection:
                break;
            case addressof:
                break;
            case cast:
                break;
            case empty:
                break;
            case array_type:
                break;
            case pointer_type:
                break;
            case abstract_declarator:
                break;
            case struct:
                break;
            case union:
                break;
            case _class:
                break;
            case basic_compound_type_fields:
                break;
            case basic_compound_type_field:
                break;
            case declarations:
                break;
            case initialized_declaration:
                break;
            case compound_statement:
                startLexicalScope();
                for (TypeAugmentedSemanticNode directChild : input.getDirectChildren()) {
                    if(!invoke(directChild)) return false;
                }
                endLexicalScope();
                break;
            case sizeof:
                break;
            case constructor_call:
                break;
            case function_description:
                break;
            case visibility:
                break;
            case class_level_declaration:
                break;
            case constructor_definition:
                break;
            case _virtual:
                break;
            case _super:
                break;
            case inherit:
                break;
            case namespaced:
                break;
            case implement:
                break;
            case implementing:
                break;
            case using:
                break;
            case alias:
                break;
            case _import:
                break;
            case compilation_tag:
                break;
            case compilation_tag_list:
                break;
            case constructor_description:
                break;
            case typeid:
                break;
            case syntax:
                break;
            case _true:
                break;
            case _false:
                break;
            case ast:
                break;
            case generic:
                break;
            case trait:
                break;
            case id_list:
                break;
            case parameterized_types:
                break;
            case parameter_type:
                break;
        }
        return true;
    }
    
    @Override
    public List<AbstractCompilationError> getErrors() {
        return null;
    }
}
