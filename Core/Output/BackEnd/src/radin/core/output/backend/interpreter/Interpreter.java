package radin.core.output.backend.interpreter;

import radin.core.JodinLogger;
import radin.core.SymbolTable;
import radin.core.errorhandling.AbstractCompilationError;
import radin.core.errorhandling.CompilationError;
import radin.core.errorhandling.ICompilationErrorCollector;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.output.midanalysis.MethodTASNTracker;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.tags.ConstructorCallTag;
import radin.core.output.tags.PriorConstructorTag;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.ICXWrapper;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.ICXCompoundType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.semantics.types.primitives.*;
import radin.core.utility.ICompilationSettings;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;

public class Interpreter {
    
    private JodinLogger logger = ICompilationSettings.ilog;
    
    public abstract class Instance<T extends CXType> {
        private T type;
        
        public Instance(T type) {
            this.type = type;
        }
        
        public T getType() {
            return type;
        }
        
        public PointerInstance<T> toPointer() {
            return new PointerInstance<>(getType(), this);
        }
        
        @Override
        public String toString() {
            return "Instance{" +
                    "type=" + type +
                    '}';
        }
        
        abstract void copyFrom(Instance<?> other);
        
        abstract Instance<T> copy();
    
        abstract Instance<?> castTo(CXType castingTo);
    }
    
    public class PrimitiveInstance<R, P extends AbstractCXPrimitiveType> extends Instance<P> {
        private R backingValue;
        private boolean unsigned;
        
        public PrimitiveInstance(P type, R backingValue, boolean unsigned) {
            super(type);
            this.backingValue = backingValue;
            if(backingValue == null) logger.warning("Shouldn't set backing value to null");
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
    
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PrimitiveInstance<?, ?> that = (PrimitiveInstance<?, ?>) o;
            return unsigned == that.unsigned &&
                    Objects.equals(backingValue, that.backingValue);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(backingValue, unsigned);
        }
    
        @Override
        Instance<P> copy() {
            return new PrimitiveInstance<R, P>(getType(), backingValue, unsigned);
        }
        
        
    }
    
    
    
    
    
    public class ArrayInstance<R extends CXType, T extends ArrayType> extends PrimitiveInstance<ArrayList<Instance<R>>,
            T> {
        private int size;
        private R subType;
        
        public ArrayInstance(T type, R subtype, int size) {
            super(type, new ArrayList<>(size), true);
            this.subType = subtype;
            for (int i = 0; i < size; i++) {
                getBackingValue().add((Instance<R>) createNewInstance(subtype));
            }
            this.size = size;
        }
        
        public ArrayInstance(T type, R subType, ArrayList<Instance<R>> other) {
            super(type, other, true);
            this.subType = subType;
        }
        
        public Instance<R> getAt(int index) {
            return getBackingValue().get(index);
        }
        
        public void setAt(int index, Instance<R> value) {
            getBackingValue().set(index, value);
        }
        
        public PointerInstance<R> asPointer() {
            return new PointerInstance<>(subType, getBackingValue(), 0);
        }
        
        public int getSize() {
            return size;
        }
        
        public R getSubType() {
            return subType;
        }
        
        public int size() {
            return size;
        }
        
        @Override
        public String toString() {
            if(subType.equals(CXPrimitiveType.CHAR)) {
                StringBuilder output = new StringBuilder("\"");
                for (int i = 0; ((PrimitiveInstance<Character, ?>) getAt(i)).backingValue.charValue() != 0 ; i++) {
                    output.append(((PrimitiveInstance<Character, ?>) getAt(i)).backingValue);
                }
                return output + "\"";
            }
            return "ArrayInstance{" +
                    "type=" + getType() +
                    ", size=" + size +
                    '}';
        }
        
        @Override
        void copyFrom(Instance<?> other) {
            if(other instanceof ArrayInstance) {
                this.setBackingValue(((ArrayInstance<R, T>) other).getBackingValue());
            } else {
                assert other instanceof PrimitiveInstance;
                if(((PrimitiveInstance<?, ?>) other).getBackingValue().toString().equals("0")) {
                    this.setBackingValue(null);
                } else {
                    throw new IllegalStateException();
                }
            }
        }
    
    
        @Override
        Instance<T> copy() {
            return new ArrayInstance<>(getType(), getSubType(), getBackingValue());
        }
    }
    
    
    public class PointerInstance<R extends CXType> extends ArrayInstance<R, PointerType> {
        private int index = 0;
        
        public PointerInstance(R type, Instance<R> pointer) {
            super(type.toPointer(), type, new ArrayList<>(Collections.singletonList(null)));
            setAt(0, pointer);
        }
        
        public PointerInstance(R type, ArrayList<Instance<R>> backing, int offset) {
            super(type.toPointer(), type, backing);
            index = offset;
        }
        
        
        public PointerInstance<R> getPointerOfOffset(int offset) {
            return new PointerInstance<R>(getSubType(), getBackingValue(), index + offset);
        }
        
        public PointerInstance(PointerType type) {
            this((R) type.getSubType(), null);
        }
        
        
        public Instance<R> getPointer() {
            return getAt(index);
        }
        
        public void setPointer(Instance<R> pointer) {
            setAt(index, pointer);
        }
        
        @Override
        public String toString() {
            return (getBackingValue() == null || getBackingValue().get(0) == null ? "[NULL] " : "") + "PointerInstance{" +
                    "type=" + getType() +
                    '}';
        }
    
        @Override
        Instance<PointerType> copy() {
            return new PointerInstance<>(getSubType(), getBackingValue(), 0);
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
    
        @Override
        Instance<T> copy() {
            CompoundInstance<T> tCompoundInstance = new CompoundInstance<>(getType());
            for (Map.Entry<String, Instance<?>> stringInstanceEntry : fields.entrySet()) {
                tCompoundInstance.fields.put(stringInstanceEntry.getKey(), stringInstanceEntry.getValue());
            }
            return tCompoundInstance;
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
            return new ArrayInstance<>(((ArrayType) type), ((ArrayType) type).getBaseType(), 15);
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
        logger.fine("Value was pushed to stack: " + val);
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
                if(symbol.getValue().getTreeType() != ASTNodeType.empty) {
                    try {
                        Instance<?> newInstance = createNewInstance(symbol.getKey().getType());
                        if (!invoke(symbol.getValue())) throw new IllegalStateException();
                        logger.fine("Added " + symbol.getKey().getToken() + " with value " + memStack.peek());
                        addAutoVariable(symbol.getKey().getToken().getImage(), newInstance);
                        newInstance.copyFrom(pop());
                    } catch (FunctionReturned functionReturned) {
                        throw new IllegalStateException();
                    }
                } else {
                    Instance<?> newInstance = createNewInstance(symbol.getKey().getType());
                    logger.fine("Added " + symbol.getKey().getToken() + " with default value " + newInstance);
                    addAutoVariable(symbol.getKey().getToken().getImage(),
                            newInstance);
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
            case literal:{
                AbstractCXPrimitiveType primitiveType = (AbstractCXPrimitiveType) node.getCXType();
                if(primitiveType.isFloatingPoint()) {
                    Double d = Double.parseDouble(node.getToken().getImage());
                    return createNewInstance(primitiveType, d);
                } else if (primitiveType.isChar()) {
                    char c = node.getToken().getImage().charAt(1);
                    return createNewInstance(primitiveType, c);
                } else if(primitiveType.isIntegral()) {
                    long l = Long.parseLong(node.getToken().getImage());
                    return createNewInstance(primitiveType, l);
                }
            }
            case string: {
                String image = node.getToken().getImage();
                return createCharPointerFromString(image.substring(1, image.length() - 1));
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
        return new ArrayInstance<>(new ArrayType(type), type, size);
    }
    
    public ArrayInstance<CXType, ArrayType> createArrayOfType(CXType type, int size) {
        return new ArrayInstance<>(new ArrayType(type), type, size);
    }
        
        public ArrayInstance<CXPrimitiveType,ArrayType> createCharArrayFromString(String s) {
        ArrayInstance<CXPrimitiveType, ArrayType> output = new ArrayInstance<>(new ArrayType(CXPrimitiveType.CHAR),
                CXPrimitiveType.CHAR,
                s.length() + 1);
        for (int i = 0; i < output.size() - 1; i++) {
            output.setAt(
                    i,
                    createNewInstance(CXPrimitiveType.CHAR, s.charAt(i))
            );
        }
        output.setAt(output.size - 1, createNewInstance(CXPrimitiveType.CHAR, '\0'));
        logger.fine("Created Jodin-Style string "+ output);
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
    
    private Instance<?> opOnObjects(TokenType op, Instance<?> lhs, Instance<?> rhs) {
        switch (op) {
            case t_lte:
                return createNewInstance(CXPrimitiveType.CHAR, memStack.indexOf(lhs) <= memStack.indexOf(rhs)? 1 : 0);
            case t_gte:
                return createNewInstance(CXPrimitiveType.CHAR, memStack.indexOf(lhs) >= memStack.indexOf(rhs)? 1 : 0);
                //return lhs >= rhs? 1 : 0;
            case t_eq:
                return createNewInstance(CXPrimitiveType.CHAR, lhs.equals(rhs)? 1 : 0);
                // return lhs == rhs? 1 : 0;
            case t_neq:
                return createNewInstance(CXPrimitiveType.CHAR, !lhs.equals(rhs)? 1 : 0);
            case t_lt:
                return createNewInstance(CXPrimitiveType.CHAR, memStack.indexOf(lhs) < memStack.indexOf(rhs)? 1 : 0);
    
            case t_gt:
                return createNewInstance(CXPrimitiveType.CHAR, memStack.indexOf(lhs) > memStack.indexOf(rhs)? 1 : 0);
    
            default:
                throw new UnsupportedOperationException();
        }
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
                
                
                logger.fine("Performing " + op + " on " + lhs + " and " + rhs);
                Instance<?> createdValue;
                if(lhs instanceof PointerInstance && rhs instanceof PointerInstance) {
                    logger.finer("Comparing two pointers");
                    createdValue = opOnObjects(op.getType(), lhs, rhs);
                }else if(lhs.getBackingValue() == null && rhs.getType().isIntegral() && rhs.getBackingValue().longValue() == 0) {
                    logger.finer("Comparing an integral to a ptr");
                    createdValue = new PrimitiveInstance<>(LongPrimitive.create(), opOnIntegral(
                            op.getType(),
                            0L,
                            rhs.backingValue.longValue()
                    ),rhs.unsigned);
                    // createdValue = null;
                } else if(rhs.getBackingValue() == null && lhs.getType().isIntegral() && lhs.getBackingValue().longValue() == 0) {
                    logger.finer("Comparing a ptr to an integral");
                    createdValue = new PrimitiveInstance<>(LongPrimitive.create(), opOnIntegral(
                            op.getType(),
                            lhs.backingValue.longValue(),
                            0L
                    ),
                            lhs.unsigned);
                }else if (lhs.getType().isFloatingPoint()) {
                    logger.finer("Operation is on floating points");
                    createdValue = new PrimitiveInstance<>(lhs.getType(),
                            opOnFloatingPoint(op.getType(), lhs.backingValue.doubleValue(),
                                    rhs.backingValue.doubleValue()),
                            lhs.unsigned);
                } else {
                    logger.finer("Operation is on integrals");
                    createdValue = new PrimitiveInstance<>(lhs.getType(), opOnIntegral(op.getType(),
                            lhs.getBackingValue().longValue(), rhs.getBackingValue().longValue()), lhs.unsigned);
                }
                push(createdValue);
                
            }
            break;
            case uniop:
                break;
            case declaration: {
                String id = input.getASTChild(ASTNodeType.id).getToken().getImage();
                addAutoVariable(id, createNewInstance(((TypedAbstractSyntaxNode) input.getASTNode()).getCxType()));
            }
                break;
            case assignment: {
                if (!invoke(input.getChild(0))) return false;
                // LHS should be pushed
                Instance<?> lhs = pop();
                if (!invoke(input.getChild(2))) return false;
                Instance<?> rhs = pop().copy();
                
                Token assignmentToken = input.getASTChild(ASTNodeType.assignment_type).getToken();
                logger.fine("Assigning " + rhs + " to " + lhs + " using " + assignmentToken);
                if (assignmentToken.getType() == TokenType.t_assign) {
                    lhs.copyFrom(rhs);
                } else if (assignmentToken.getType() == TokenType.t_operator_assign) {
                    
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
                    if (!invoke(entry)) return false;
                }
                break;
            case typename:
                break;
            case parameter_list:
                break;
            case function_call: {
               
                String funcCall = input.getASTChild(ASTNodeType.id).getToken().getImage();
                if(funcCall.equals("calloc")) {
                    if (!invoke(input.getASTChild(ASTNodeType.sequence))) return false;
                    CXType cxType =
                            ((TypedAbstractSyntaxNode) input.getASTChild(ASTNodeType.sequence).getASTChild(ASTNodeType.sizeof).getASTNode()).getCxType();
                    PrimitiveInstance<Number, ?> size = (PrimitiveInstance<Number, ?>)pop();
                    
                    returnValue = createArrayOfType(cxType, size.backingValue.intValue());
                    break;
                }
    
    
                TypeAugmentedSemanticNode function = getSymbol(input.getASTChild(ASTNodeType.id).getToken().getImage());
                if (function == null) {
    
                    throw new CompilationError("Symbol " + funcCall + " not " +
                            "defined", input.getASTChild(ASTNodeType.id).getToken());
                } else {
                    if (!invoke(input.getASTChild(ASTNodeType.sequence))) return false;
                    try {
                        logger.info("Calling function: " + input.getASTChild(ASTNodeType.id).getToken().getImage());
                        if (!invoke(function)) return false;
                    } catch (FunctionReturned functionReturned) {
                        if (returnValue != null) {
                            push(returnValue);
                        }
                        returnValue = null;
                    }
                }
            }
                break;
            case method_call:
                // owner is pushed to stack
            {
                if (!invoke(input.getChild(0))) return false;
                CompoundInstance<CXClassType> classTypeInstance = ((CompoundInstance<CXClassType>) pop());
                createClosure();
                addAutoVariable("this", classTypeInstance);
                Token idToken = input.getASTChild(ASTNodeType.id).getToken();
                int memstackPrevious = memStack.size();
                if (!invoke(input.getASTChild(ASTNodeType.sequence))) return false;
                Stack<CXType> types = new Stack<>();
                Stack<Instance<?>> instances = new Stack<>();
                while (memStack.size() > memstackPrevious) {
                    Instance<?> pop = pop();
                    types.push(pop.getType());
                    instances.push(pop);
                }
                for (Instance<?> instance : instances) {
                    memStack.push(instance);
                }
                TypeAugmentedSemanticNode method = dynamicMethodLookup(classTypeInstance.getType(), idToken, types);
                try {
                    if (!invoke(method)) return false;
                } catch (FunctionReturned functionReturned) {
                    push(returnValue);
                    returnValue = null;
                }
                endClosure();
            }
            break;
            case field_get:
                if(!invoke(input.getChild(0))) return false;
                // owner on stack
            {
                CompoundInstance<?> compoundInstance = (CompoundInstance<?>) pop();
                String field = input.getChild(1).getToken().getImage();
                
                // push field
                push(compoundInstance.get(field));
            }
            break;
            case if_cond: {
                if (!invoke(input.getChild(0))) return false;
                PrimitiveInstance<Number, ?> pop = (PrimitiveInstance<Number, ?>) pop();
                boolean cond;
                if (pop.getBackingValue() instanceof Double || pop.getBackingValue() instanceof Float) {
                    cond = pop.getBackingValue().doubleValue() != 0;
                } else {
                    cond = pop.getBackingValue().intValue() != 0;
                }
                if (cond) {
                    if (!invoke(input.getChild(1))) return false;
                } else if (input.getChild(2).getASTType() != ASTNodeType.empty) {
                    if (!invoke(input.getChild(2))) return false;
                }
            }
                break;
            case while_cond:
                while (true) {
                    if (!invoke(input.getChild(0))) return false;
                    PrimitiveInstance<Number, ?> pop = (PrimitiveInstance<Number, ?>) pop();
                    boolean cond;
                    if (pop.getBackingValue() instanceof Double || pop.getBackingValue() instanceof Float) {
                        cond = pop.getBackingValue().doubleValue() != 0;
                    } else {
                        cond = pop.getBackingValue().intValue() != 0;
                    }
                    if(!cond) break;
                    if(!invoke(input.getChild(1))) return false;
                }
                
                break;
            case do_while_cond:
                while (true) {
                    if(!invoke(input.getChild(0))) return false;
                    if (!invoke(input.getChild(1))) return false;
                    PrimitiveInstance<Number, ?> pop = (PrimitiveInstance<Number, ?>) pop();
                    boolean cond;
                    if (pop.getBackingValue() instanceof Double || pop.getBackingValue() instanceof Float) {
                        cond = pop.getBackingValue().doubleValue() != 0;
                    } else {
                        cond = pop.getBackingValue().intValue() != 0;
                    }
                    if(!cond) break;
                }
                break;
            case for_cond:
                if(!invoke(input.getChild(0))) return false;
                while (true) {
                    if (!invoke(input.getChild(1))) return false;
                    PrimitiveInstance<Number, ?> pop = (PrimitiveInstance<Number, ?>) pop();
                    boolean cond;
                    if (pop.getBackingValue() instanceof Double || pop.getBackingValue() instanceof Float) {
                        cond = pop.getBackingValue().doubleValue() != 0;
                    } else {
                        cond = pop.getBackingValue().intValue() != 0;
                    }
                    if(!cond) break;
                    if(!invoke(input.getChild(1))) return false;
                    if(!invoke(input.getChild(2))) return false;
                }
                break;
            case _return:
                if(input.getChildren().size() > 0) {
                    if(!invoke(input.getChild(0))) return false;
                    returnValue = pop();
                    logger.fine("Function to return " + returnValue);
                }
                throw new FunctionReturned();
            case constructor_definition:
            case function_definition:
                createClosure();
                List<TypeAugmentedSemanticNode> parameters = input.getASTChild(ASTNodeType.parameter_list).getChildren();
                for (int i = parameters.size() - 1; i >= 0; i--) {
                    addAutoVariable(
                            parameters.get(i).getASTChild(ASTNodeType.id).getToken().getImage(),
                            pop().copy()
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
                if(!invoke(input.getChild(0))) return false;
    
                Instance<?> og = pop();
                if(og instanceof PrimitiveInstance && !(og instanceof PointerInstance)) {
                    if( ((PrimitiveInstance<Number, ?>) og).backingValue.doubleValue() == 0) {
                        throw new Error("Can't dereference a null pointer");
                    }
                } else if(og instanceof PointerInstance) {
                    if(((PointerInstance) og).getBackingValue() == null) {
                        throw new Error("Can't dereference a null pointer");
                    }
                }
                PointerInstance<?> pointerInstance = (PointerInstance<?>) og;
                logger.finer("Getting indirection of " + pointerInstance.getType() + " => " + pointerInstance.getAt(0));
                push(pointerInstance.getAt(0));
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
                for (TypeAugmentedSemanticNode child : input.getChildren()) {
                    if(!invoke(child)) return false;
                }
                break;
            case initialized_declaration:
                if(!invoke(input.getChild(0))) return false;
                String id = input.getChild(0).getASTChild(ASTNodeType.id).getToken().getImage();
                if(!invoke(input.getChild(1))) return false;
                Instance<CXType> autoVariable = getAutoVariable(id);
                autoVariable.copyFrom(pop());
                break;
            case compound_statement:
                startLexicalScope();
                for (TypeAugmentedSemanticNode directChild : input.getDirectChildren()) {
                    if(!invoke(directChild)) return false;
                }
                endLexicalScope();
                break;
            case sizeof:
                push(createNewInstance(LongPrimitive.create(), input.getCXType().getDataSize(environment)));
                break;
            case constructor_call: {
                CXClassType subType = (CXClassType) ((PointerType) input.getCXType()).getSubType();
                PointerInstance<CXClassType> classTypeInstance =
                        (PointerInstance<CXClassType>) createNewInstance(subType).toPointer();
                createClosure();
                addAutoVariable("this", classTypeInstance);
                int memstackPrevious = memStack.size();
                if (!invoke(input.getASTChild(ASTNodeType.sequence))) return false;
                /*Stack<CXType> types = new Stack<>();
                Stack<Instance<?>> instances = new Stack<>();
                while (memStack.size() > memstackPrevious) {
                    Instance<?> pop = pop();
                    types.push(pop.getType());
                    instances.push(pop);
                }
                for (Instance<?> instance : instances) {
                    memStack.push(instance);
                }
                TypeAugmentedSemanticNode constructor =
                        dynamicConstructorLookup(((CXClassType) ((PointerType) input.getCXType()).getSubType()),
                        types);
                try {
                    if (!invoke(constructor)) return false;
                } catch (FunctionReturned functionReturned) {
                }
               
                 */
                //push(classTypeInstance);
                if(input.containsCompilationTag(PriorConstructorTag.class)) {
                    PriorConstructorTag prior = input.getCompilationTag(PriorConstructorTag.class);
                    if(!invoke(prior.getSequence())) return false;
                    if(!invoke(MethodTASNTracker.getInstance().get(prior.getPriorConstructor()))) return false;
                    //push(classTypeInstance);
                }
                ConstructorCallTag compilationTag = input.getCompilationTag(ConstructorCallTag.class);
                CXConstructor cxConstructor = compilationTag.getConstructor();
                logger.info("Calling constructor " + cxConstructor.getParent());
                TypeAugmentedSemanticNode cons = MethodTASNTracker.getInstance().get(cxConstructor);
                if(!invoke(cons)) return false;
                
                endClosure();
                push(classTypeInstance);
            }
                break;
            case function_description:
                break;
            case visibility:
                break;
            case class_level_declaration:
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
                push(createNewInstance(CXPrimitiveType.CHAR, 1));
                break;
            case _false:
                push(createNewInstance(CXPrimitiveType.CHAR, 0));
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
    
    public TypeAugmentedSemanticNode dynamicMethodLookup(CXClassType clazz, Token id, List<CXType> inputTypes) {
        ParameterTypeList parameterTypeList = new ParameterTypeList(inputTypes);
        CXMethod method = clazz.getMethod(id, parameterTypeList, null);
        return MethodTASNTracker.getInstance().get(method);
    }
    
    public TypeAugmentedSemanticNode dynamicConstructorLookup(CXClassType clazz, List<CXType> inputTypes) {
        ParameterTypeList parameterTypeList = new ParameterTypeList(inputTypes);
        CXMethod method = clazz.getConstructor(parameterTypeList);
        return MethodTASNTracker.getInstance().get(method);
    }
}
