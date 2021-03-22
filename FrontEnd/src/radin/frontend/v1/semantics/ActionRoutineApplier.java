package radin.frontend.v1.semantics;

import radin.core.errorhandling.AbstractCompilationError;
import radin.core.errorhandling.CompilationError;
import radin.core.semantics.types.primitives.EnumType;
import radin.frontend.directastparsing.ASTParser;
import radin.frontend.v1.MissingCategoryNodeError;
import radin.frontend.v1.parsing.CategoryNode;
import radin.frontend.v1.parsing.LeafNode;
import radin.frontend.v1.parsing.ParseNode;
import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TokenStoringAbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.exceptions.InvalidPrimitiveException;
import radin.core.semantics.exceptions.TypeDoesNotExist;
import radin.core.semantics.generics.CXParameterizedType;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.semantics.types.compound.AbstractCXClassType;
import radin.core.semantics.types.primitives.ArrayType;
import radin.core.semantics.types.primitives.PointerType;
import radin.input.ISemanticAnalyzer;
import radin.frontend.v1.InheritMissingError;
import radin.core.semantics.types.wrapped.CXDeferredClassDefinition;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;

import java.util.*;
import java.util.stream.Collectors;

import static radin.core.lexical.TokenType.t_id;
import static radin.core.lexical.TokenType.t_typename;

public class ActionRoutineApplier implements ISemanticAnalyzer<ParseNode, AbstractSyntaxNode> {
    
    
    public static final Token $_GLOBAL_$ = new Token(t_id, "$GLOBAL$");
    private static final AbstractSyntaxNode GLOBAL_NODE = getGlobal();
    private Stack<CategoryNode> catNodeStack;
    private Stack<String> stringErrors;
    private List<AbstractCompilationError> errors;
    private List<AbstractSyntaxNode> successOrder;
    private TypeEnvironment environment;
    
    
    
    
    private long runCount = 0;
    
    public ActionRoutineApplier() {
        catNodeStack = new Stack<>();
        successOrder = new LinkedList<>();
        stringErrors = new Stack<>();
        environment = new TypeEnvironment();
        errors = new LinkedList<>();
    }
    
    public ActionRoutineApplier(TypeEnvironment environment) {
        catNodeStack = new Stack<>();
        successOrder = new LinkedList<>();
        stringErrors = new Stack<>();
        this.environment = environment;
        errors = new LinkedList<>();
    }
    
    private static AbstractSyntaxNode getGlobal() {
        return new AbstractSyntaxNode(ASTNodeType.id,
                $_GLOBAL_$);
    }
    
    @Override
    public void reset() {
        environment.resetNamespace();
        environment.resetToNone();
    }
    
    @Override
    public void clearErrors() {
        errors.clear();
    }
    
    private CategoryNode currentCategoryNode() {
        return catNodeStack.peek();
    }
    
    public List<AbstractSyntaxNode> getSuccessOrder() {
        return successOrder;
    }
    
    private CategoryNode getCatNode(String category, int count) {
        return currentCategoryNode().getCategoryNode(category, count);
    }
    
    private CategoryNode getCatNode(String category) {
        return getCatNode(category, 1);
    }
    
    public boolean noTypeErrors() {
        return environment.noTypeErrors();
    }
    
    private boolean error(String message) {
        if(stringErrors.peek() == null) {
            stringErrors.pop();
            stringErrors.push(message);
        }
        return false;
    }
    
    @Override
    public AbstractSyntaxNode invoke(ParseNode input) {
        if(!enactActionRoutine(input)) return null;
        try {
            return input.getSynthesized();
        } catch (SynthesizedMissingException e) {
            return null;
        }
    }
    
    @Override
    public long getRunCount() {
        return runCount;
    }
    
    @Override
    public void resetRunCount() {
        runCount = 0;
    }
    
    @Override
    public AbstractSyntaxNode analyze(ParseNode tree) {
        try {
            if (!enactActionRoutine(tree)) return null;
        } catch (AbstractCompilationError e) {
            errors.add(e);
            return null;
        } catch (Error e) {
            errors.add(new CompilationError(e, findFirstToken(tree)));
            return null;
        }
        try {
            return tree.getSynthesized();
        } catch (SynthesizedMissingException e) {
            error("Failure to analyze");
        }
        return null;
    }
    
    @Override
    public TypeEnvironment getEnvironment() {
        return environment;
    }
    
    public List<AbstractCompilationError> getErrors() {
        List<AbstractCompilationError> output = new LinkedList<>(getStringErrors());
        output.addAll(errors);
        return output;
    }
    
    public List<AbstractCompilationError> getStringErrors() {
        return stringErrors.stream().filter(Objects::nonNull).map((o) -> new CompilationError(o, null)).collect(Collectors.toList());
    }
    
    public static class ActionRoutineApplierFailure extends AbstractCompilationError {
        public ActionRoutineApplierFailure(ParseNode on, String message) {
            super("Although " + on + " is defined and parsable, semantics are broken for it",
                    findFirstToken(on), message);
        }
    }
    
    private static Token findFirstToken(ParseNode node) {
        if (node instanceof LeafNode) {
            return ((LeafNode) node).getToken();
        } else {
            CategoryNode categoryNode = (CategoryNode) node;
            for (ParseNode directChild : categoryNode.getDirectChildren()) {
                Token firstToken = findFirstToken(directChild);
                if(firstToken != null) return firstToken;
            }
        }
        return null;
    }
    
    public boolean enactActionRoutine(ParseNode node) {
        ++runCount;
        try {
            if(node instanceof LeafNode) {
                boolean b = enactActionRoutine((LeafNode) node);
                if(b) {
                    if(!successOrder.contains(node.getSynthesized())) successOrder.add(node.getSynthesized());
                }
                return b;
            }
            else if(node instanceof CategoryNode) {
                
                CategoryNode catNode = (CategoryNode) node;
                catNodeStack.push(catNode);
                stringErrors.push(null);
                boolean b = enactActionRoutine(catNode);
                if(!b) {
                    
                    ICompilationSettings.debugLog.finer("Failed to enact action routine for " +
                            String.format("%-30s", node) +
                            (node.hasChildren()? "(CHILDREN = " + ((CategoryNode) node).getAllChildren() + ")" : "") +
                            (stringErrors.peek() == null ? "" : String.format("  %60s", "Error: " + stringErrors.peek())));
                    if(stringErrors.peek() == null) {
                        throw new ActionRoutineApplierFailure(node, "Failed to enact action routine for " + node);
                    } else {
                        throw new ActionRoutineApplierFailure(node, stringErrors.peek());
                    }
                } else {
                    if(!successOrder.contains(node.getSynthesized())) successOrder.add(node.getSynthesized());
                }
                stringErrors.pop();
                catNodeStack.pop();
                return b;
            }
        } catch (SynthesizedMissingException e) {
            System.err.println(e);
        }
        
        return false;
    }
    
    private boolean enactActionRoutine(LeafNode node) {
        Token token = node.getToken();
        switch (token.getType()) {
            case t_id: {
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.id, token));
                break;
            }
            case t_literal: {
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.literal, token));
                break;
            }
            case t_string: {
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.string, token));
                break;
            }
            case t_qmark:
            case t_dor:
            case t_dand:
            case t_bar:
            case t_not:
            case t_and:
            case t_eq:
            case t_neq:
            case t_lt:
            case t_lte:
            case t_gt:
            case t_gte:
            case t_lshift:
            case t_rshift:
            case t_add:
            case t_minus:
            case t_star:
            case t_fwslash:
            case t_percent:
            case t_bang:
            case t_inc:
            case t_dec: {
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.operator, token));
                break;
            }
            case t_struct:
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.struct));
                break;
            case t_union:
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.union));
                break;
            case t_class:
                node.setInherit(new AbstractSyntaxNode(ASTNodeType._class));
                break;
            case t_typename:
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.typename, token));
                break;
            case t_assign:
            case t_operator_assign:
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.assignment_type, token));
                break;
            case t_private:
            case t_public:
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.visibility, token));
                break;
            case t_virtual:
                node.setInherit(new AbstractSyntaxNode(ASTNodeType._virtual));
                break;
            case t_super:
                node.setInherit(new AbstractSyntaxNode(ASTNodeType._super));
                break;
            case t_unsigned:
            case t_long:
            case t_short:
            case t_int:
            case t_char:
            case t_void:
            case t_double:
            case t_float:
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.specifier, token));
                break;
            case t_const:
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.qualifier, token));
                break;
        }
        
        node.setSynthesized(node.getInherit());
        return true;
    }
    
    private boolean enactActionRoutine(CategoryNode node) {
        boolean cont = true;
        
        
        while(cont) {
            try {
                switch (node.getCategory()) {
                    case "AST": {
                        if (node.getInherit() instanceof TokenStoringAbstractSyntaxNode) {
                            TokenStoringAbstractSyntaxNode tokenStoringAbstractSyntaxNode =
                                    ((TokenStoringAbstractSyntaxNode) node.getInherit());
                            ASTParser astParser = new ASTParser(environment, tokenStoringAbstractSyntaxNode.getTokens());
                            
                            
                            CategoryNode parse = astParser.parse();
                            node.setSynthesized(parse.getSynthesized());
                        } else return false;
                        return true;
                    }
                    case "TopLevelDeclaration": {
                        if(node.hasChildCategory("FunctionDefinition")) {
                            getCatNode("FunctionDefinition").setInherit(
                                    AbstractSyntaxNode.EMPTY
                            );
                        }
                        if(node.hasChildCategory("CompilationTagList")) {
                            AbstractSyntaxNode list = getCatNode("CompilationTagList").getSynthesized();
                            node.getChild(1).setCompilationTagList(list);
                            AbstractSyntaxNode synthesized = node.getChild(1).getSynthesized();
                            if(synthesized instanceof TypedAbstractSyntaxNode) {
                                node.setSynthesized(
                                        new TypedAbstractSyntaxNode(synthesized, ((TypedAbstractSyntaxNode) synthesized).getCxType(), list)
                                );
                            }
                            else {
                                node.setSynthesized(
                                        new AbstractSyntaxNode(synthesized, list)
                                );
                            }
                            return true;
                        }
                        
                        node.setSynthesized(node.getChild(0).getSynthesized());
                        
                        return true;
                    }
                    
                    case "AssignOperator":
                    case "TopExpression":
                    case "Statement":
                    case "StructOrUnion":
                    case "AssignmentExpression":
                    case "ParameterTypeList":
                    case "Visibility": {
                        node.setSynthesized(node.getChild(0).getSynthesized());
                        return true;
                    }
                    case "Program": {
                        node.setSynthesized(getCatNode("TopLevelDecsList").getSynthesized());
                        return true;
                    }
                    case "TopLevelDecsList": {
                        AbstractSyntaxNode[] array = foldList(node, "TopLevelDeclaration",
                                "TopLevelDecsList", "TopLevelDecsTail");
                        
                        
                        node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.top_level_decs, array));
                        return true;
                    }
                    case "ExpressionStatement": {
                        if(!node.hasChildren()) {
                            node.setSynthesized(
                                    AbstractSyntaxNode.EMPTY
                            );
                        } else {
                            node.setSynthesized(node.getChild(0).getSynthesized());
                        }
                        return true;
                    }
                    
                    case "Expression": {
                        AbstractSyntaxNode doubleOrS = node.getCategoryNode("DoubleOr").getSynthesized();
                        CategoryNode doubleOrTail = node.getCategoryNode("DoubleOrTail");
                        doubleOrTail.setInherit(doubleOrS);
                        AbstractSyntaxNode doubleOrTailS = doubleOrTail.getSynthesized();
                        CategoryNode expressionTail = node.getCategoryNode("ExpressionTail");
                        expressionTail.setInherit(doubleOrTailS);
                        node.setSynthesized(expressionTail.getSynthesized());
                        return true;
                    }
                    case "ExpressionTail": {
                        if(node.hasChildren()) {
                            CategoryNode e1 = node.getCategoryNode("Expression", 1), e2 = node.getCategoryNode(
                                    "Expression", 2);
                            node.setSynthesized(new AbstractSyntaxNode(
                                    ASTNodeType.ternary,
                                    node.getInherit(),
                                    e1.getSynthesized(),
                                    e2.getSynthesized()
                            ));
                        } else {
                            node.setSynthesized(node.getInherit());
                        }
                        return true;
                    }
                    case "DoubleOr": {
                        AbstractSyntaxNode doubleAndS = getCatNode("DoubleAnd").getSynthesized();
                        CategoryNode doubleAndTail = getCatNode("DoubleAndTail");
                        doubleAndTail.setInherit(doubleAndS);
                        node.setSynthesized(doubleAndTail.getSynthesized());
                        return true;
                    }
                    case "DoubleAnd": {
                        AbstractSyntaxNode doubleAndS = getCatNode("Or").getSynthesized();
                        CategoryNode doubleAndTail = getCatNode("OrTail");
                        doubleAndTail.setInherit(doubleAndS);
                        node.setSynthesized(doubleAndTail.getSynthesized());
                        return true;
                    }
                    case "Or": {
                        AbstractSyntaxNode doubleAndS = getCatNode("Not").getSynthesized();
                        CategoryNode doubleAndTail = getCatNode("NotTail");
                        doubleAndTail.setInherit(doubleAndS);
                        node.setSynthesized(doubleAndTail.getSynthesized());
                        return true;
                    }
                    case "Not": {
                        AbstractSyntaxNode doubleAndS = getCatNode("And").getSynthesized();
                        CategoryNode doubleAndTail = getCatNode("AndTail");
                        doubleAndTail.setInherit(doubleAndS);
                        node.setSynthesized(doubleAndTail.getSynthesized());
                        return true;
                    }
                    case "And": {
                        AbstractSyntaxNode doubleAndS = getCatNode("Equation").getSynthesized();
                        CategoryNode doubleAndTail = getCatNode("EquationTail");
                        doubleAndTail.setInherit(doubleAndS);
                        node.setSynthesized(doubleAndTail.getSynthesized());
                        return true;
                    }
                    case "Equation": {
                        AbstractSyntaxNode doubleAndS = getCatNode("C").getSynthesized();
                        CategoryNode doubleAndTail = getCatNode("CTail");
                        doubleAndTail.setInherit(doubleAndS);
                        node.setSynthesized(doubleAndTail.getSynthesized());
                        return true;
                    }
                    case "C": {
                        AbstractSyntaxNode doubleAndS = getCatNode("G").getSynthesized();
                        CategoryNode doubleAndTail = getCatNode("GTail");
                        doubleAndTail.setInherit(doubleAndS);
                        node.setSynthesized(doubleAndTail.getSynthesized());
                        return true;
                    }
                    case "G": {
                        AbstractSyntaxNode doubleAndS = getCatNode("T").getSynthesized();
                        CategoryNode doubleAndTail = getCatNode("TTail");
                        doubleAndTail.setInherit(doubleAndS);
                        node.setSynthesized(doubleAndTail.getSynthesized());
                        return true;
                    }
                    case "T": {
                        AbstractSyntaxNode doubleAndS = getCatNode("Factor").getSynthesized();
                        CategoryNode doubleAndTail = getCatNode("FactorTail");
                        doubleAndTail.setInherit(doubleAndS);
                        node.setSynthesized(doubleAndTail.getSynthesized());
                        return true;
                    }
                    case "Factor": {
                        if(node.firstIs("Atom")) {
                            getCatNode("AtomTail").setInherit(getCatNode("Atom").getSynthesized());
                            node.setSynthesized(getCatNode("AtomTail").getSynthesized());
                            return true;
                        } else if(node.firstIs("EnumMember")) {
                            node.setSynthesized(getCatNode("EnumMember").getSynthesized());
                            return true;
                        } if(node.firstIs(TokenType.t_string, TokenType.t_literal)) {
                            node.setSynthesized(node.getChild(0).getSynthesized());
                            return true;
                        } else if (node.firstIs(TokenType.t_minus, TokenType.t_not, TokenType.t_bang, TokenType.t_add, TokenType.t_inc, TokenType.t_dec)) {
                            AbstractSyntaxNode operatorNode = node.getChild(0).getSynthesized();
                            AbstractSyntaxNode factorSynth = getCatNode("Factor", 2).getSynthesized();
                            getCatNode("Factor", 1).setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.uniop, operatorNode, factorSynth)
                            );
                            return true;
                        } else if(node.firstIs(TokenType.t_star)) {
                            AbstractSyntaxNode factor = getCatNode("Factor", 2)
                                    .getSynthesized();
                            AbstractSyntaxNode deref = new AbstractSyntaxNode(ASTNodeType.indirection, factor);
                            node.setSynthesized(deref);
                            return true;
                        } else if(node.firstIs(TokenType.t_and)) {
                            
                            AbstractSyntaxNode factor = getCatNode("Factor", 2)
                                    .getSynthesized();
                            AbstractSyntaxNode addressOf = new AbstractSyntaxNode(ASTNodeType.addressof, factor);
                            node.setSynthesized(addressOf);
                            return true;
                        } else if(node.firstIs("CastExpression")) {
                            node.setSynthesized(getCatNode("CastExpression").getSynthesized());
                            return true;
                        } else if(node.firstIs(TokenType.t_sizeof)) {
                            AbstractSyntaxNode typeName = getCatNode("CanonicalType").getSynthesized();
                            getCatNode("AbstractDeclarator").setInherit(typeName);
                            typeName = getCatNode("AbstractDeclarator").getSynthesized();
                            CXType type = environment.getType(typeName);
                            if(type instanceof PointerType && UniversalCompilerSettings.getInstance().getSettings().isInRuntimeCompilationMode()) {
                                if(((PointerType) type).innerMostType() instanceof AbstractCXClassType) {
                                    type = ((PointerType) type).getSubType();
                                }
                            }
                            
                            node.setSynthesized(
                                    new TypedAbstractSyntaxNode(ASTNodeType.sizeof, type)
                            );
                            return true;
                        } else if(node.firstIs(TokenType.t_true)) {
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType._true)
                            );
                            return true;
                        } else if(node.firstIs(TokenType.t_false)) {
                            node.setSynthesized(new AbstractSyntaxNode(ASTNodeType._false));
                            return true;
                        } else if (node.firstIs(TokenType.t_lbrac)) {
                            AbstractSyntaxNode members = getCatNode("ArgsList").getSynthesized();
                            node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.inline_array, members));
                            return true;
                        }
                        
                        node.printTreeForm();
                        return error("Invalid Factor Layout: " + node.getAllChildren());
                    }
                    case "DoubleOrTail":
                    case "DoubleAndTail":
                    case "OrTail":
                    case "NotTail":
                    case "AndTail":
                    case "EquationTail":
                    case "CTail":
                    case "GTail":
                    case "TTail":
                    case "FactorTail": {
                        if(node.hasChildren()) {
                            AbstractSyntaxNode operatorNode = node.getChild(0).getSynthesized();
                            String current = node.getCategory();
                            String currentNonTail = node.getCategory().replace("Tail", "");
                            //getCatNode(currentNonTail).setInherit(node.getInherit());
                            AbstractSyntaxNode created = new AbstractSyntaxNode(ASTNodeType.binop, operatorNode,
                                    node.getInherit(), getCatNode(currentNonTail).getSynthesized());
                            CategoryNode secondTail = getCatNode(current, 2);
                            secondTail.setInherit(created);
                            node.setSynthesized(secondTail.getSynthesized());
                        } else {
                            node.setSynthesized(node.getInherit());
                        }
                        return true;
                    }
                    case "Atom": {
                        if (node.hasChildCategory("NamespacedId")) {
                            CategoryNode namespacedId = node.getCategoryNode("NamespacedId");
                            namespacedId.setInherit(AbstractSyntaxNode.EMPTY);
                            AbstractSyntaxNode name = namespacedId.getSynthesized();
                            AbstractSyntaxNode interact;
                            CategoryNode functionCall = getCatNode("FunctionCall");
                            functionCall.setInherit(GLOBAL_NODE, 0);
                            functionCall.setInherit(name, 1);
                            if(functionCall.hasChildren()) {
                                interact = functionCall.getSynthesized();
                                //interact = new AbstractSyntaxNode(ASTNodeType.function_call, name, call);
                            } else {
                                interact = name;
                            }

                            node.setSynthesized(interact);
                            return true;
                        } else if (node.firstIs(t_id)) {
                            AbstractSyntaxNode name = node.getLeafNode(t_id).getSynthesized();
                            AbstractSyntaxNode interact;
                            CategoryNode functionCall = getCatNode("FunctionCall");
                            functionCall.setInherit(GLOBAL_NODE, 0);
                            functionCall.setInherit(name, 1);
                            if(functionCall.hasChildren()) {
                                interact = functionCall.getSynthesized();
                                //interact = new AbstractSyntaxNode(ASTNodeType.function_call, name, call);
                            } else {
                                interact = name;
                            }
                            
                            node.setSynthesized(interact);
                            return true;
                        } else if (node.hasChildCategory("Expression")) {
                            node.setSynthesized(node.getCategoryNode("Expression").getSynthesized());
                            return true;
                        } else if(node.firstIs(TokenType.t_new)) {
                            
                            AbstractSyntaxNode typename = node.getChild(1).getSynthesized();
                            
                            CXType type = environment.getType(typename);
                            AbstractSyntaxNode args = getCatNode("ArgsList").getSynthesized();
                            
                            TypedAbstractSyntaxNode synthesized = new TypedAbstractSyntaxNode(ASTNodeType.constructor_call, type, args);
                            synthesized.setToken(node.getLeafNode(TokenType.t_new).getToken());
                            node.setSynthesized(
                                    synthesized
                            );
                            
                            return true;
                        } else if(node.firstIs(TokenType.t_super)) {
                            
                            if(getCatNode("FunctionCall").hasChildren()) throw new IllegalArgumentException();
                            
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType._super)
                            );
                            return true;
                        }
                        return false;
                    }
                    case "AtomTail": {
                        if(node.hasChildren()) {
                            if(node.firstIs(TokenType.t_dot, TokenType.t_arrow)) {
                                AbstractSyntaxNode object;
                                if(node.firstIs(TokenType.t_dot)) {
                                    object = node.getInherit();
                                } else {
                                    object = new AbstractSyntaxNode(ASTNodeType.indirection, node.getInherit());
                                }
                                
                                
                                AbstractSyntaxNode name = node.getLeafNode(t_id).getSynthesized();
                                CategoryNode functionCall = getCatNode("FunctionCall");
                                functionCall.setInherit(object, 0);
                                functionCall.setInherit(name, 1);
                                AbstractSyntaxNode interaction = functionCall.getSynthesized();
                                // interaction = new AbstractSyntaxNode(ASTNodeType.method_call, object, name, call);
                                /*} else {
                                    // field call
                                    interaction = new AbstractSyntaxNode(ASTNodeType.field_get, object, name);
                                }
                                
                                 */
                                
                                getCatNode("AtomTail", 2).setInherit(interaction);
                                node.setSynthesized(getCatNode("AtomTail", 2).getSynthesized());
                                return true;
                            } else if(node.firstIs(TokenType.t_lbrac)) {
                                CategoryNode expr = getCatNode("Expression");
                                AbstractSyntaxNode arrayAccess = new AbstractSyntaxNode(ASTNodeType.array_reference,
                                        node.getInherit(), expr.getSynthesized());
                                getCatNode("AtomTail", 2).setInherit(arrayAccess);
                                node.setSynthesized(getCatNode("AtomTail", 2).getSynthesized());
                                return true;
                            } else if(node.firstIs(TokenType.t_inc, TokenType.t_dec)) {
                                node.setSynthesized(
                                        new AbstractSyntaxNode(ASTNodeType.postop,
                                                node.getInherit(), node.getChild(0).getSynthesized())
                                );
                                return true;
                            }
                            
                            
                            
                            return false;
                        } else {
                            node.setSynthesized(node.getInherit());
                        }
                        
                        
                        
                        return true;
                    }
                    case "FunctionCall": {
                        if(node.hasChildren()) {
                            AbstractSyntaxNode object = node.getInherit(0);
                            AbstractSyntaxNode name = node.getInherit(1);
                            
                            AbstractSyntaxNode call = getCatNode("ArgsList").getSynthesized();
                            
                            AbstractSyntaxNode synth;
                            if(object.equals(GLOBAL_NODE)) {
                                synth = new AbstractSyntaxNode(ASTNodeType.function_call, name,
                                        call);
                            } /*else if(object.getType() == ASTNodeType.indirection && object.hasChild(ASTNodeType
                            ._super)) {
                                
                                AbstractSyntaxNode inner = new AbstractSyntaxNode(ASTNodeType.field_get, object,
                                        name);
                                
                                node.setSynthesized(
                                        new AbstractSyntaxNode(ASTNodeType.function_call, inner, call)
                                );
                            }
                            */
                            else synth = new AbstractSyntaxNode(ASTNodeType.method_call, object, name,
                                    call);

                            if(node.hasChildCategory("GenericInstanceInitParameters")) {
                                CategoryNode initParameters = node.getCategoryNode("GenericInstanceInitParameters");
                                initParameters.setInherit(synth);
                                synth = initParameters.getSynthesized();
                            }

                            node.setSynthesized(synth);
                        } else {
                            node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.field_get, node.getInherit(0),
                                    node.getInherit(1)));
                        }
                        
                        return true;
                    }
                    case "ArgsList": {
                        String EntryCategory = "Expression";
                        String HeadCatName = "ArgsList";
                        String TailCatName = "ArgsListTail";
                        
                        AbstractSyntaxNode[] array = foldList(node, EntryCategory, HeadCatName, TailCatName);
                        
                        
                        node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.sequence, array));
                        return true;
                    }
                    case "CastExpression": {
                        AbstractSyntaxNode factor = getCatNode("Factor")
                                .getSynthesized();
                        AbstractSyntaxNode type = getCatNode("TypeName").getSynthesized();
                        CXType cxtype = environment.getType(type);
                        node.setSynthesized(
                                new TypedAbstractSyntaxNode(ASTNodeType.cast, cxtype, factor)
                        );
                        
                        return true;
                    }
                    case "TypeName": {
                        
                        CategoryNode abstractDeclarator = getCatNode("AbstractDeclarator");
                        AbstractSyntaxNode canonicalType = getCatNode("CanonicalType")
                                .getSynthesized();
                        abstractDeclarator
                                .setInherit(
                                        canonicalType
                                );
                        
                        node.setSynthesized(abstractDeclarator.getSynthesized());
                        
                        
                        return true;
                    }
                    case "SpecsAndQuals": {
                        AbstractSyntaxNode sOrQ;
                        if(node.firstIs("Qualifier")) {
                            sOrQ = getCatNode("Qualifier").getSynthesized();
                        } else if(node.firstIs("Specifier")) {
                            sOrQ = getCatNode("Specifier").getSynthesized();
                        } else if(node.firstIs("NamespacedType")) {
                            sOrQ = getCatNode("NamespacedType").getSynthesized();
                        } else return error("Typename without valid specifier or qualifier");
                        
                        //getCatNode("SpecsAndQualsTail").setInherit(sOrQ);
                        
                        node.setSynthesized(
                                AbstractSyntaxNode.unroll(ASTNodeType.qualifiers_and_specifiers,
                                        sOrQ,
                                        getCatNode("SpecsAndQualsTail")
                                                .getSynthesized()
                                )
                        );
                        
                        
                        return true;
                    }
                    case "SpecsAndQualsTail": {
                        
                        if(node.hasChildren()) {
                            node.setSynthesized(
                                    getCatNode("SpecsAndQuals").getSynthesized()
                            );
                        } else {
                            node.setSynthesized(AbstractSyntaxNode.EMPTY);
                        }
                        
                        return true;
                    }
                    case "Qualifier": {
                        node.setSynthesized(
                                new AbstractSyntaxNode(ASTNodeType.qualifier, ((LeafNode) node.getChild(0)).getToken())
                        );
                        return true;
                    }
                    case "Specifier": {
                        
                        if(node.firstIs(t_id, TokenType.t_char, TokenType.t_int, TokenType.t_long,
                                TokenType.t_float, TokenType.t_double, TokenType.t_unsigned, TokenType.t_void)) {
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.specifier, ((LeafNode) node.getChild(0)).getToken())
                            );
                            return true;
                        } else if(node.firstIs("StructOrUnionSpecifier")) {
                            node.setSynthesized(getCatNode("StructOrUnionSpecifier").getSynthesized());
                            return true;
                        } else if(node.firstIs(TokenType.t_typename)) {
                            // TODO: Fix this interaction
                            
                            CXType type = environment.getType(node.getLeafNode(TokenType.t_typename).getSynthesized());
                            node.setSynthesized(
                                    new TypedAbstractSyntaxNode(ASTNodeType.specifier, type)
                            );
                            return true;
                        } else if(node.firstIs("ClassSpecifier")) {
                            node.setSynthesized(
                                    getCatNode("ClassSpecifier").getSynthesized()
                            );
                            return true;
                        }
                        
                        return false;
                    }
                    case "AbstractDeclarator": {
                        if(node.hasChildren()) {
                            if(node.hasChildCategory("Pointer")) {
                                CategoryNode pointer = getCatNode("Pointer");
                                pointer.setInherit(node.getInherit());
                                node.getCategoryNode("DirectAbstractDeclarator")
                                        .setInherit(
                                                pointer.getSynthesized()
                                        );
                            } else {
                                node.getCategoryNode("DirectAbstractDeclarator")
                                        .setInherit(node.getInherit());
                            }
                            node.setSynthesized(
                                    node.getCategoryNode("DirectAbstractDeclarator").getSynthesized()
                            );
                        } else {
                            node.setSynthesized(node.getInherit());
                        }
                        return true;
                    }
                    case "DirectAbstractDeclarator": {
                        CXType type = environment.getType(node.getInherit());
                        if(node.hasChildren()) {
                            AbstractSyntaxNode dir;
                            if(node.firstIs(TokenType.t_lbrac)) {
                                CXType newType;
                                AbstractSyntaxNode expression = AbstractSyntaxNode.EMPTY;
                                
                                
                                if(node.hasChildCategory("Expression")) {
                                    expression = node.getCategoryNode("Expression").getSynthesized();
                                    newType = new ArrayType(type, expression);
                                } else {
                                    newType = new ArrayType(type);
                                }
                                
                                node.getCategoryNode("DirectAbstractDeclarator", 2).setInherit(
                                        new TypedAbstractSyntaxNode(
                                                ASTNodeType.abstract_declarator,
                                                newType,
                                                expression
                                        ),
                                        0
                                );
                                node.getCategoryNode("DirectAbstractDeclarator", 2).setInherit(node.getInherit());
                                // node.setSynthesized(node.getCategoryNode("DirectAbstractDeclarator", 2)
                                // .getSynthesized());
                                dir = new TypedAbstractSyntaxNode(ASTNodeType.array_type, newType);
                            } else return false;
                            
                            getCatNode("DirectAbstractDeclarator", 2).setInherit(dir);
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.abstract_declarator,
                                            getCatNode("DirectAbstractDeclarator", 2)
                                                    .getSynthesized()
                                    )
                            );
                            
                        } else {
                            node.setSynthesized(node.getInherit().addType(type));
                        }
                        
                        return true;
                    }
                    case "Pointer": {
                        boolean one = false;
                        if(node.hasChildCategory("QualifierList")) {
                            one = true;
                            // TODO: add this
                        }
                        
                        if(node.hasChildCategory("Pointer")) {
                            one = true;
                            getCatNode("Pointer", 2).setInherit(
                                    new AbstractSyntaxNode(ASTNodeType.pointer_type, node.getInherit())
                            );
                            node.setSynthesized(getCatNode("Pointer", 2).getSynthesized());
                        }
                        
                        if(!one) {
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.pointer_type, node.getInherit())
                            );
                        }
                        return true;
                    }
                    case "TypeDef": {
                        AbstractSyntaxNode typeAST = getCatNode("CanonicalType").getSynthesized();
                        CategoryNode abstractDeclarator = getCatNode("AbstractDeclarator");
                        abstractDeclarator.setInherit(typeAST);
                        AbstractSyntaxNode abstractDec = abstractDeclarator.getSynthesized();
                        CXType type;
                        AbstractSyntaxNode id;
                        if(node.hasChildToken(t_id)) {
                            id = node.getLeafNode(t_id).getSynthesized();
                            type = environment.addTypeDefinition(abstractDec, id.getToken().getImage());
                        } else {
                            type = environment.getType(abstractDec);
                            id = node.getLeafNode(t_typename).getSynthesized();
                            CXType original = environment.getType(id);
                            if(!original.isExact(type, environment)) return error("Conflicting types for typedef of " + node.getLeafNode(t_typename).getToken().getImage());
                        }
                        


                        node.setSynthesized(
                                new TypedAbstractSyntaxNode(ASTNodeType.typedef, type, id)
                        );
                        return true;
                    }
                    case "StructOrUnionSpecifier": {
                        AbstractSyntaxNode structOrUnion = node.getCategoryNode("StructOrUnion").getSynthesized();
                        LeafNode nameNode = node.getLeafNode(t_id);
                        if(nameNode == null) nameNode = node.getLeafNode(TokenType.t_typename);
                        AbstractSyntaxNode nameAST = nameNode != null? nameNode.getSynthesized() : null;
                        AbstractSyntaxNode mid;
                        if(node.hasChildCategory("StructDeclarationList")) {
                            AbstractSyntaxNode declarations = getCatNode("StructDeclarationList").getSynthesized();
                            if(nameAST == null) {
                                mid = new AbstractSyntaxNode(ASTNodeType.basic_compound_type_dec, structOrUnion,
                                        declarations);
                            } else
                                mid = new AbstractSyntaxNode(ASTNodeType.basic_compound_type_dec, structOrUnion,
                                        nameAST,
                                        declarations);
                        } else {
                            mid = new AbstractSyntaxNode(ASTNodeType.compound_type_reference,
                                    structOrUnion, nameAST);
                        }
                        
                        mid = new AbstractSyntaxNode(ASTNodeType.specifier, mid);
                        
                        CXType type = environment.getType(mid);
                        
                        node.setSynthesized(
                                new TypedAbstractSyntaxNode(mid.getTreeType(), type, mid.getChild(0))
                        );
                        
                        return true;
                    }
                    case "ClassSpecifier": {
                        LeafNode nameNode = node.getLeafNode(t_id);
                        if(nameNode == null) nameNode = node.getLeafNode(TokenType.t_typename);
                        
                        AbstractSyntaxNode nameAST = nameNode != null? nameNode.getSynthesized() : null;
                        AbstractSyntaxNode specifierInner = new AbstractSyntaxNode(
                                ASTNodeType.compound_type_reference,
                                new AbstractSyntaxNode(ASTNodeType._class),
                                nameAST
                        );
                        AbstractSyntaxNode outer = new AbstractSyntaxNode(ASTNodeType.specifier, specifierInner);
                        CXDeferredClassDefinition type = ((CXDeferredClassDefinition) environment.getType(outer));
                        ICompilationSettings.debugLog.info("Added deferred type for " + type.getIdentifier().fullInfo());
                        node.setSynthesized(
                                new TypedAbstractSyntaxNode(ASTNodeType.specifier, type, specifierInner)
                        );
                        return true;
                    }
                    case "StructDeclarationList": {
                        
                        String EntryCategory = "StructDeclaration";
                        String HeadCatName = "StructDeclarationList";
                        String TailCatName = "StructDeclarationListTail";
                        
                        AbstractSyntaxNode[] array = foldList(node, EntryCategory, HeadCatName, TailCatName);
                        
                        
                        node.setSynthesized(AbstractSyntaxNode.bringUpChildren(
                                new AbstractSyntaxNode(ASTNodeType.basic_compound_type_fields, array)
                        ));
                        return true;
                        
                    }
                    case "Declaration": {
                        AbstractSyntaxNode declarationSpecifiers = getCatNode("CanonicalType").getSynthesized();
                        if(!(declarationSpecifiers instanceof TypedAbstractSyntaxNode)) {
                            declarationSpecifiers = declarationSpecifiers.addType(
                                    environment.getType(declarationSpecifiers)
                            );
                        }
                        if(node.hasChildCategory("InitDeclaratorList")) {
                            getCatNode("InitDeclaratorList").setInherit(declarationSpecifiers);
                            node.setSynthesized(
                                    getCatNode("InitDeclaratorList").getSynthesized()
                            );
                        } else {
                            node.setSynthesized(
                                    declarationSpecifiers
                            );
                        }
                        return true;
                    }
                    case "DeclarationSpecifiers": {
                        node.setSynthesized(getCatNode("SpecsAndQuals").getSynthesized());
                        return true;
                    }
                    case "StructDeclaration": {
                        AbstractSyntaxNode specsAndQuals = getCatNode("SpecsAndQuals").getSynthesized();
                        CategoryNode declaratorList = getCatNode("StructDeclaratorList");
                        declaratorList.setInherit(specsAndQuals);
                        
                        
                        node.setSynthesized(
                                declaratorList.getSynthesized()
                        );
                        
                        return true;
                    }
                    case "StructDeclaratorList": {
                        String EntryCategory = "StructDeclarator";
                        String HeadCatName = "StructDeclaratorList";
                        String TailCatName = "StructDeclaratorListTail";
                        
                        AbstractSyntaxNode[] array = foldList(node, EntryCategory, HeadCatName, TailCatName, node.getInherit());
                        
                        
                        node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.basic_compound_type_fields, array));
                        return true;
                    }
                    case "StructDeclarator": {
                        getCatNode("Declarator").setInherit(node.getInherit());
                        TypedAbstractSyntaxNode declarator = ((TypedAbstractSyntaxNode) getCatNode("Declarator").getSynthesized());
                        
                        node.setSynthesized(
                                new TypedAbstractSyntaxNode(ASTNodeType.basic_compound_type_field,
                                        declarator.getCxType(), declarator.getChildList())
                        );
                        return true;
                    }
                    case "InitDeclaratorList": {
                        String EntryCategory = "InitDeclarator";
                        String HeadCatName = "InitDeclaratorList";
                        String TailCatName = "InitDeclaratorListTail";
                        
                        AbstractSyntaxNode[] array = foldList(node, EntryCategory, HeadCatName, TailCatName, node.getInherit());
                        
                        
                        node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.declarations, array));
                        return true;
                    }
                    case "InitDeclarator": {
                        getCatNode("Declarator").setInherit(node.getInherit());
                        
                        if(node.hasChildCategory("Initializer")) {
                            getCatNode("Initializer").setInherit(getCatNode("Declarator").getSynthesized());
                            node.setSynthesized(
                                    getCatNode("Initializer").getSynthesized()
                            );
                        } else {
                            node.setSynthesized(
                                    getCatNode("Declarator").getSynthesized()
                            );
                        }
                        
                        return true;
                    }
                    case "Declarator": {
                        AbstractSyntaxNode mid = node.getInherit();
                        if(node.hasChildCategory("Pointer")) {
                            getCatNode("Pointer").setInherit(mid);
                            mid = getCatNode("Pointer").getSynthesized();
                        }
                        
                        getCatNode("DirectDeclarator").setInherit(mid);
                        node.setSynthesized(
                                getCatNode("DirectDeclarator").getSynthesized()
                        );
                        return true;
                    }
                    case "DirectDeclarator": {
                        AbstractSyntaxNode mid = node.getInherit();
                        node.getChild(0).setInherit(node.getInherit());
                        AbstractSyntaxNode name = node.getChild(0).getSynthesized();
                        
                        getCatNode("DirectDeclaratorTail").setInherit(mid, 0);
                        getCatNode("DirectDeclaratorTail").setInherit(name, 1);
                        TypedAbstractSyntaxNode directDeclaratorTail = (TypedAbstractSyntaxNode) getCatNode("DirectDeclaratorTail").getSynthesized();
                        if(directDeclaratorTail.getTreeType() == ASTNodeType.function_description) {
                            // directDeclaratorTail.printTreeForm();
                            
                            if(directDeclaratorTail.getChildList().size() == 2 &&
                                    directDeclaratorTail.getChild(0).getTreeType() == ASTNodeType.declaration && directDeclaratorTail.getChild(1).getTreeType() == ASTNodeType.parameter_list) {
                                
                                if(((TypedAbstractSyntaxNode) directDeclaratorTail.getChild(0)).getCxType().is(new PointerType(directDeclaratorTail.getCxType()), environment)) {
                                    
                                    // this a function pointer
                                    
                                    // System.out.println("function pointer");
                                    List<CXType> parameters = new LinkedList<>();
                                    for (AbstractSyntaxNode abstractSyntaxNode : directDeclaratorTail.getChild(1).getChildList()) {
                                        assert abstractSyntaxNode instanceof TypedAbstractSyntaxNode;
                                        parameters.add(((TypedAbstractSyntaxNode) abstractSyntaxNode).getCxType());
                                    }
                                    
                                    CXType functionPointerType = new CXFunctionPointer(directDeclaratorTail.getCxType()
                                            , parameters);
                                    
                                    AbstractSyntaxNode id = directDeclaratorTail.getChild(0).getChild(0);
                                    node.setSynthesized(
                                            new TypedAbstractSyntaxNode(ASTNodeType.declaration, functionPointerType,
                                                    id)
                                    );
                                    
                                    return true;
                                    
                                }
                            }
                            
                            
                        }
                        node.setSynthesized(
                                directDeclaratorTail
                        );
                        
                        return true;
                    }
                    case "DirectDeclaratorTail": {
                        // System.out.println(node.getAllChildren());
                        CXType type = environment.getType(node.getInherit(0));
                        if(node.hasChildren()) {
                            //node.printTreeForm();
                            if(node.firstIs(TokenType.t_lpar)) {
                                AbstractSyntaxNode parameterTypeList;
                                if(node.getDirectChildren().size() == 3)
                                    parameterTypeList = node.getChild(1).getSynthesized();
                                else parameterTypeList = new AbstractSyntaxNode(ASTNodeType.parameter_list);
                                
                                
                                node.getCategoryNode("DirectDeclaratorTail", 2).setInherit(
                                        new TypedAbstractSyntaxNode(
                                                ASTNodeType.function_description,
                                                type,
                                                node.getInherit(1),
                                                parameterTypeList
                                        ),
                                        0
                                );
                                node.getCategoryNode("DirectDeclaratorTail", 2).setInherit(node.getInherit(1), 1);
                                node.setSynthesized(node.getCategoryNode("DirectDeclaratorTail", 2).getSynthesized());
                            } else if(node.firstIs(TokenType.t_lbrac)) {
                                CXType newType;
                                AbstractSyntaxNode expression = AbstractSyntaxNode.EMPTY;
                                
                                
                                if(node.hasChildCategory("Expression")) {
                                    expression = node.getCategoryNode("Expression").getSynthesized();
                                    newType = new ArrayType(type, expression);
                                } else {
                                    newType = new ArrayType(type);
                                }
                                
                                node.getCategoryNode("DirectDeclaratorTail", 2).setInherit(
                                        new TypedAbstractSyntaxNode(
                                                ASTNodeType.declaration,
                                                newType,
                                                node.getInherit(1),
                                                expression
                                        ),
                                        0
                                );
                                node.getCategoryNode("DirectDeclaratorTail", 2).setInherit(node.getInherit(1), 1);
                                node.setSynthesized(node.getCategoryNode("DirectDeclaratorTail", 2).getSynthesized());
                            } else
                                return false;
                        } else {
                            if(node.getInherit(0).getTreeType() == ASTNodeType.declaration || node.getInherit(0).getTreeType() == ASTNodeType.function_description)
                                node.setSynthesized(
                                        node.getInherit(0)
                                );
                            else
                                node.setSynthesized(
                                        new TypedAbstractSyntaxNode(
                                                ASTNodeType.declaration,
                                                type,
                                                node.getInherit(1)
                                        )
                                );
                        }
                        
                        return true;
                    }
                    case "Initializer": {
                        AbstractSyntaxNode assignmentExpression = getCatNode("AssignmentExpression").getSynthesized();
                        node.setSynthesized(
                                new AbstractSyntaxNode(ASTNodeType.initialized_declaration,
                                        //((TypeAbstractSyntaxNode) node.getInherit()).getCxType(),
                                        node.getInherit(),
                                        assignmentExpression)
                        );
                        return true;
                    }
                    case "FunctionDefinition": {
                        AbstractSyntaxNode declarationSpecifiers = getCatNode("CanonicalType").getSynthesized();
                        CategoryNode declarator = getCatNode("Declarator");
                        declarator.setInherit(
                                declarationSpecifiers
                        );
                        TypedAbstractSyntaxNode declaratorAST = ((TypedAbstractSyntaxNode) declarator.getSynthesized());
                        AbstractSyntaxNode compound = getCatNode("CompoundStatement").getSynthesized();
                        
                        AbstractSyntaxNode parameters;
                        if(declaratorAST.hasChild(ASTNodeType.parameter_list)) {
                            parameters = declaratorAST.getChild(ASTNodeType.parameter_list);
                        } else {
                            parameters = new AbstractSyntaxNode(ASTNodeType.parameter_list);
                        }
                        
                        node.setSynthesized(
                                new TypedAbstractSyntaxNode(
                                        ASTNodeType.function_definition,
                                        declaratorAST.getCxType(),
                                        node.getInherit(),
                                        declaratorAST.getChild(ASTNodeType.id),
                                        parameters,
                                        compound)
                        );
                        return true;
                    }
                    case "CompoundStatement": {
                        if(node.hasChildren()) {
                            node.setSynthesized(
                                    getCatNode("StatementList").getSynthesized()
                            );
                        } else {
                            node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.compound_statement));
                        }
                        return true;
                    }
                    case "StatementList": {
                        String EntryCategory = "Statement";
                        String HeadCatName = "StatementList";
                        String TailCatName = "StatementListTail";
                        
                        AbstractSyntaxNode[] array = foldList(node, EntryCategory, HeadCatName, TailCatName);
                        
                        
                        node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.compound_statement, array));
                        return true;
                    }
                    
                    case "IterationStatement": {
                        AbstractSyntaxNode statement = getCatNode("Statement").getSynthesized();
                        
                        // System.out.println(node.getAllChildren());
                        if(node.firstIs(TokenType.t_for)) {
                            AbstractSyntaxNode first, second, third = AbstractSyntaxNode.EMPTY;
                            if(node.hasChildCategory("Declaration")) {
                                first = node.getCategoryNode("Declaration").getSynthesized();
                                second = node.getCategoryNode("ExpressionStatement").getSynthesized();
                            } else {
                                first = node.getCategoryNode("ExpressionStatement", 1).getSynthesized();
                                second = node.getCategoryNode("ExpressionStatement", 2).getSynthesized();
                            }
                            if(node.hasChildCategory("TopExpression")) {
                                third = node.getCategoryNode("TopExpression").getSynthesized();
                            }
                            
                            
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.for_cond, first, second, third, statement)
                            );
                            
                            return true;
                        }
                        if(node.firstIs(TokenType.t_while)) {
                            
                            AbstractSyntaxNode expression = getCatNode("TopExpression").getSynthesized();
                            
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.while_cond, expression, statement)
                            );
                            
                            return true;
                        }
                        if(node.firstIs(TokenType.t_do)) {
                            
                            AbstractSyntaxNode expression = getCatNode("TopExpression").getSynthesized();
                            
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.do_while_cond, statement, expression)
                            );
                            
                            
                            return true;
                        }
                        
                        return false;
                    }
                    case "JumpStatement": {
                        if(node.firstIs(TokenType.t_return)) {
                            AbstractSyntaxNode retValue;
                            if(node.hasChildCategory("TopExpression")) {
                                retValue = node.getCategoryNode("TopExpression").getSynthesized();
                            } else {
                                retValue = AbstractSyntaxNode.EMPTY;
                            }
                            
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType._return, retValue)
                            );
                            return true;
                        }
                        
                        return false;
                    }
                    case "SelectionStatement": {
                        if(node.firstIs(TokenType.t_if)) {
                            // System.out.println(node.getAllChildren());
                            
                            AbstractSyntaxNode topExpression = getCatNode("TopExpression").getSynthesized();
                            AbstractSyntaxNode ifYes = getCatNode("Statement", 1).getSynthesized();
                            AbstractSyntaxNode ifNo = AbstractSyntaxNode.EMPTY;
                            if(node.hasChildToken(TokenType.t_else)) {
                                ifNo = getCatNode("Statement", 2).getSynthesized();
                            }
                            
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.if_cond, topExpression, ifYes, ifNo)
                            );
                            return true;
                        }
                        
                        return false;
                    }
                    case "Assignment": {
                        AbstractSyntaxNode factor = getCatNode("Factor").getSynthesized();
                        AbstractSyntaxNode assignment = getCatNode("AssignOperator").getSynthesized();
                        AbstractSyntaxNode next = getCatNode("AssignmentExpression").getSynthesized();
                        
                        node.setSynthesized(
                                new AbstractSyntaxNode(ASTNodeType.assignment, factor, assignment, next)
                        );
                        return true;
                    }
                    case "ParameterList": {
                        String EntryCategory = "ParameterDeclaration";
                        String HeadCatName = "ParameterList";
                        String TailCatName = "ParameterListTail";
                        
                        AbstractSyntaxNode[] array = foldList(node, EntryCategory, HeadCatName, TailCatName);
                        
                        
                        node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.parameter_list, array));
                        return true;
                    }
                    case "ParameterDeclaration": {
                        
                        AbstractSyntaxNode specifiers = getCatNode("CanonicalType").getSynthesized();
                        CategoryNode declarator;
                        if(node.hasChildCategory("Declarator")) {
                            declarator =  getCatNode("Declarator");
                        } else if(node.hasChildCategory("AbstractDeclarator")){
                            declarator = getCatNode("AbstractDeclarator");
                        } else {
                            node.setSynthesized(specifiers);
                            return true;
                        }
                        
                        declarator.setInherit(specifiers);
                        
                        AbstractSyntaxNode declaratorSynthesized = declarator.getSynthesized();
                        
                        node.setSynthesized(
                                declaratorSynthesized
                        );
                        
                        return true;
                    }
                    case "ClassDeclaration": {
                        
                        AbstractSyntaxNode name = node.getLeafNode(t_id).getSynthesized();
                        
                        // environment.addTypeDefinition(new CXCompoundTypeNameIndirection
                        // (CXCompoundTypeNameIndirection.CompoundType._class, name.getToken().getImage()) ,name.getToken().getImage());
                        
                        ICompilationSettings.debugLog.finer("Adding temp " + node.getLeafNode(t_id).getToken());
                        environment.addTemp(node.getLeafNode(t_id).getToken());
                        
                        AbstractSyntaxNode declarations = getCatNode("ClassDeclarationList").getSynthesized();
                        
                        
                        AbstractSyntaxNode classDefinition;
                        if(node.hasChildCategory("Inherit")) {
                            AbstractSyntaxNode inherit = new AbstractSyntaxNode(ASTNodeType.inherit,
                                    getCatNode("Inherit").getChild(0).getSynthesized());
                            classDefinition =
                                    new AbstractSyntaxNode(ASTNodeType.class_type_definition, name, inherit, declarations);
                        } else {
                            classDefinition =
                                    new AbstractSyntaxNode(ASTNodeType.class_type_definition, name, declarations);
                        }
                        
                        CXClassType cxClass = (CXClassType) environment.getType(classDefinition);
                        CXIdentifier typeNameIdentifier = cxClass.getTypeNameIdentifier();
                        environment.getTempType(name.getToken().getImage()).update();
                        environment.removeTempType(typeNameIdentifier);
                        
                        if(node.getCompilationTagList() != null) {
                            AbstractSyntaxNode list = node.getCompilationTagList();
                            for (AbstractSyntaxNode tag : list.getChildList()) {
                                Token id = tag.getChild(ASTNodeType.id).getToken();
                                if(tag.hasChild(ASTNodeType.sequence)) {
                                    List<Object> objects = new LinkedList<>();
                                    for (AbstractSyntaxNode abstractSyntaxNode : tag.getChild(ASTNodeType.sequence).getChildList()) {
                                        objects.add(abstractSyntaxNode.getToken().getImage());
                                    }
                                    environment.getClassTargetManger().invokeAnnotation(id, cxClass,
                                            objects.toArray(new Object[0]));
                                } else {
                                    environment.getClassTargetManger().invokeAnnotation(id, cxClass);
                                }
                            }
                        }
                        
                        
                        //environment.addTypeDefinition(cxClass ,name.getToken().getImage());
                        node.setSynthesized(classDefinition.addType(cxClass));
                        return true;
                    }
                    case "ClassDeclarationList": {
                        String EntryCategory = "ClassTopLevelDeclaration";
                        String HeadCatName = "ClassDeclarationList";
                        String TailCatName = "ClassDeclarationListTail";
                        
                        AbstractSyntaxNode[] array = foldList(node, EntryCategory, HeadCatName, TailCatName);
                        
                        
                        node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.class_level_decs, array));
                        return true;
                    }
                    case "ClassTopLevelDeclaration": {
                        int base = 0;
                        AbstractSyntaxNode list = null;
                        if(node.hasChildCategory("CompilationTagList")) {
                            list = getCatNode("CompilationTagList").getSynthesized();
                            node.getChild(node.getAllChildren().size() - 1).setCompilationTagList(list);
                            base = 1;
                        }
                        AbstractSyntaxNode visibility;
                        if(node.hasChildCategory("Visibility")) {
                            visibility = getCatNode("Visibility").getSynthesized();
                        } else {
                            visibility = new AbstractSyntaxNode(ASTNodeType.visibility, new Token(TokenType.t_internal));
                        }
                        
                        // System.out.println(node.getAllChildren());
                        AbstractSyntaxNode inner;
                        if(node.hasChildCategory("Declaration")) {
                            inner = getCatNode("Declaration").getSynthesized();
                            
                            if(inner.getChild(base).getTreeType() == ASTNodeType.function_description) {
                                inner = inner.getChild(base);
                                assert inner instanceof TypedAbstractSyntaxNode;
                                CXType ret = ((TypedAbstractSyntaxNode) inner).getCxType();
                                if(node.hasChildToken(TokenType.t_virtual)) {
                                    inner = new TypedAbstractSyntaxNode(inner, true, ret,
                                            node.getLeafNode(TokenType.t_virtual).getSynthesized());
                                } else {
                                    inner = new TypedAbstractSyntaxNode(inner, true, ret,
                                            AbstractSyntaxNode.EMPTY);
                                }
                            }
                            
                        } else if(node.hasChildCategory("ConstructorDefinition")) {
                            inner = getCatNode("ConstructorDefinition").getSynthesized();
                        } else if(node.hasChildCategory("FunctionDefinition")) {
                            if(node.hasChildToken(TokenType.t_virtual)) {
                                getCatNode("FunctionDefinition").setInherit(
                                        node.getLeafNode(TokenType.t_virtual).getSynthesized()
                                );
                            } else {
                                getCatNode("FunctionDefinition").setInherit(
                                        AbstractSyntaxNode.EMPTY
                                );
                            }
                            inner = getCatNode("FunctionDefinition").getSynthesized();
                        }
                        else return error("Improper class level declaration");
                        
                        node.setSynthesized(
                                new AbstractSyntaxNode(ASTNodeType.class_level_declaration, visibility, inner)
                        );
                        if(list != null) node.getSynthesized().setCompilationTags(list);
                        
                        return true;
                    }
                    case "ConstructorDefinition": {
                        AbstractSyntaxNode typename = node.getLeafNode(TokenType.t_typename).getSynthesized();
                        AbstractSyntaxNode parameterList = getCatNode("ParameterList").getSynthesized();
                        AbstractSyntaxNode compoundStatement = AbstractSyntaxNode.EMPTY;
                        if(node.hasChildCategory("CompoundStatement")) {
                            compoundStatement = getCatNode("CompoundStatement").getSynthesized();
                        }
                        
                        CXType type = environment.getType(typename);
                        if(node.hasChildCategory("ArgsList")) {
                            AbstractSyntaxNode priorAST = node.getChild(2).getSynthesized();
                            AbstractSyntaxNode argsAST = node.getCategoryNode("ArgsList").getSynthesized();
                            
                            node.setSynthesized(
                                    new TypedAbstractSyntaxNode(ASTNodeType.constructor_definition, type, typename,
                                            parameterList, priorAST, argsAST, compoundStatement)
                            );
                        } else {
                            node.setSynthesized(
                                    new TypedAbstractSyntaxNode(ASTNodeType.constructor_definition, type, typename,
                                            parameterList, compoundStatement)
                            );
                        }
                        return true;
                    }
                    case "InIdentifier": {
                        Token id = node.getLeafNode(t_id).getToken();
                        AbstractSyntaxNode idNode = node.getLeafNode(t_id).getSynthesized();
                        boolean push = false;
                        try{
                            node.setSynthesized(
                                    node.getChild(1).getSynthesized()
                            );
                        }catch (SynthesizedMissingException e) {
                            push = true;
                        }
                        
                        if(push) environment.pushNamespace(id);

                        AbstractSyntaxNode newNode = new AbstractSyntaxNode(ASTNodeType.in_namespace, idNode, node.getChild(1).getSynthesized());

                        node.setSynthesized(newNode);
                        
                        environment.popNamespace();
                        return true;
                    }
                    case "NamespacedType": {
                        // System.out.println(node.getDirectChildren());
                        
                        if(node.hasChildToken(TokenType.t_typename)) {
                            node.setSynthesized(node.getLeafNode(TokenType.t_typename).getSynthesized());
                        } else {
                            AbstractSyntaxNode id = node.getLeafNode(t_id).getSynthesized();
                            AbstractSyntaxNode next = getCatNode("NamespacedType", 2).getSynthesized();
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.namespaced, id, next)
                            );
                        }
                        
                        return true;
                    }
                    case "Implement": {
                        AbstractSyntaxNode namespacedTypeNode = getCatNode("NamespacedType").getSynthesized();
                        CXType namespacedType = environment.getType(namespacedTypeNode);
                        TypedAbstractSyntaxNode implementing = new TypedAbstractSyntaxNode(ASTNodeType.implementing,
                                namespacedType, namespacedTypeNode);
                        
                        if(node.hasChildCategory("Implementation")) {
                            CategoryNode implementationCat = getCatNode("Implementation");
                            implementationCat.setInherit(implementing);
                            AbstractSyntaxNode implementation = implementationCat.getSynthesized();
                            
                            node.setSynthesized(
                                    new TypedAbstractSyntaxNode(ASTNodeType.implement,
                                            namespacedType,
                                            implementation
                                    )
                            );
                        } else {
                            String EntryCategory = "Implementation";
                            String HeadCatName = "ImplementList";
                            String TailCatName = "ImplementListTail";
                            
                            AbstractSyntaxNode[] array = foldList(node.getCategoryNode("ImplementList"), EntryCategory,
                                    HeadCatName, TailCatName, implementing);
                            
                            
                            node.setSynthesized(
                                    new TypedAbstractSyntaxNode(ASTNodeType.implement, namespacedType, array)
                            );
                        }
                        
                        return true;
                    }
                    case "Implementation": {
                        node.getChild(0).setInherit(node.getInherit());
                        node.setSynthesized(node.getChild(0).getSynthesized());
                        return true;
                    }
                    case "CompilationTagList": {
                        if(node.hasChildCategory("CompilationTag")) {
                            AbstractSyntaxNode tail = getCatNode("CompilationTagList", 2).getSynthesized();
                            AbstractSyntaxNode head = getCatNode("CompilationTag").getSynthesized();
                            
                            node.setSynthesized(
                                    new AbstractSyntaxNode(tail, true, head)
                            );
                        } else {
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.compilation_tag_list)
                            );
                        }
                        return true;
                    }
                    case "CompilationTag": {
                        AbstractSyntaxNode id = node.getLeafNode(t_id).getSynthesized();
                        AbstractSyntaxNode args = AbstractSyntaxNode.EMPTY;
                        
                        if(node.hasChildCategory("ArgsList")) {
                            args = getCatNode("ArgsList").getSynthesized();
                        }
                        
                        node.setSynthesized(
                                new AbstractSyntaxNode(ASTNodeType.compilation_tag, id, args)
                        );
                        
                        return true;
                    }
                    case "Using": {
                        AbstractSyntaxNode find = getCatNode("NamespacedId").getSynthesized();
                        CXIdentifier namespace = new CXIdentifier(find);
                        environment.useNamespace(namespace);
                        if(node.getDirectChildren().size() > 2) {
                            AbstractSyntaxNode inner;
                            if(node.hasChildCategory("TopLevelDecsList")) {
                                CategoryNode topLevelDecsList = getCatNode("TopLevelDecsList");
                                if(!enactActionRoutine(topLevelDecsList)) return false;
                                inner = topLevelDecsList.getSynthesized();
                            }else {
                                ParseNode other = node.getChild(2);
                                if(!enactActionRoutine(other)) return false;
                                inner = other.getSynthesized();
                            }
                            environment.stopUseNamespace(namespace);
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.using, find, inner)
                            );
                        } else {

                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.using, find)
                            );
                        }

                        return true;
                    }
                    case "Namespace": {
                        AbstractSyntaxNode id = node.getLeafNode(t_id).getSynthesized();
                        
                        if(node.hasChildCategory("Namespace")) {
                            node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.namespaced, id));
                        } else {
                            AbstractSyntaxNode next = getCatNode("Namespace", 2).getSynthesized();
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.namespaced, id, next)
                            );
                        }
                        
                        return true;
                    }
                    case "NamespacedId": {
                        AbstractSyntaxNode id = node.getLeafNode(t_id).getSynthesized();
                        AbstractSyntaxNode parent = node.getInheritOrEmpty();

                        if(node.hasChildCategory("NamespacedId")) {
                            AbstractSyntaxNode newParent;
                            if(parent == AbstractSyntaxNode.EMPTY) {
                                newParent = new AbstractSyntaxNode(ASTNodeType.namespaced_id, id);
                            } else {
                                newParent = new AbstractSyntaxNode(ASTNodeType.namespaced_id, parent, id);
                            }

                            CategoryNode child = node.getCategoryNode("NamespacedId", 2);
                            child.setInherit(newParent);
                            node.setSynthesized(child.getSynthesized());
                        } else {
                            if(parent == AbstractSyntaxNode.EMPTY) {
                                node.setSynthesized(id);
                            } else {
                                node.setSynthesized(
                                        new AbstractSyntaxNode(ASTNodeType.namespaced_id, parent, id)
                                );
                            }
                        }

                        return true;
                    }
                    case "Alias": {
                        AbstractSyntaxNode id = node.getLeafNode(t_id).getSynthesized();
                        node.setSynthesized(
                                new AbstractSyntaxNode(ASTNodeType.alias, id)
                        );
                        return true;
                    }
                    case "GenericDeclaration": {

                        // Get the TypeParameterList
                        AbstractSyntaxNode identifierList = getCatNode("TypeParameterList").getSynthesized();
                        List<AbstractSyntaxNode> parameterTypes = identifierList.getDirectChildren();


                        List<String> typedefs = new LinkedList<>();

                        for (TypedAbstractSyntaxNode parameterType : parameterTypes.stream().map((p) -> (TypedAbstractSyntaxNode) p).collect(Collectors.toList())) {
                            AbstractSyntaxNode id = parameterType.getChild(0);
                            CXType upperBound = environment.getDefaultInheritance();
                            if(parameterType.getDirectChildren().size() > 1) {
                                upperBound = environment.getType(node.getChild(1).getSynthesized());
                            }
                            String image = id.getToken().getImage();
                            typedefs.add(image);
                            if(!node.getChild(1).hasSynthesized()) {
                                CXParameterizedType parameterizedType = new CXParameterizedType((CXClassType) upperBound,
                                        id.getToken(), environment);

                                CXType cxType = environment.addTypeDefinition(parameterizedType, image);
                                parameterType.setCxType(cxType);
                            }
                        }
                        
                        node.getChild(1).setInherit(AbstractSyntaxNode.EMPTY);


                        AbstractSyntaxNode dec = node.getChild(1).getSynthesized();
                        
                        
                        for (String typedef : typedefs) {
                            environment.removeTypeDefinition(typedef);
                            
                        }
                        
                        // this should be the complete generic clause
                        AbstractSyntaxNode generic = new AbstractSyntaxNode(ASTNodeType.generic, identifierList, dec);
                        node.setSynthesized(generic);




                        
                        return true;
                    }
                    case "IdentifierList": {
                        TokenType tokenType = t_id;
                        String HeadCatName = "IdentifierList";
                        String TailCatName = "IdentifierListTail";
                        
                        AbstractSyntaxNode[] array = foldList(node, tokenType,
                                HeadCatName, TailCatName);
                        
                        
                        node.setSynthesized(
                                new AbstractSyntaxNode(ASTNodeType.id_list, array)
                        );
                        return true;
                    }
                    case "TypeParameter": {
                        AbstractSyntaxNode id = node.getLeafNode(t_id).getSynthesized();
                        if(node.hasChildCategory("Inherit")) {
                            AbstractSyntaxNode inherit = node.getCategoryNode("Inherit").getSynthesized();
                            node.setSynthesized(new TypedAbstractSyntaxNode(ASTNodeType.parameter_type, id, inherit));
                        } else {
                            node.setSynthesized(new TypedAbstractSyntaxNode(ASTNodeType.parameter_type, id));
                        }
                        return true;
                    }
                    case "TypeParameterList": {
                        String entry = "TypeParameter";
                        String HeadCatName = "TypeParameterList";
                        String TailCatName = "TypeParameterListTail";
                        
                        AbstractSyntaxNode[] array = foldList(node, entry,
                                HeadCatName, TailCatName);
                        
                        
                        node.setSynthesized(
                                new AbstractSyntaxNode(ASTNodeType.parameterized_types, array)
                        );
                        return true;
                    }
                    case "CanonicalType": {
                        List<AbstractSyntaxNode> children = new LinkedList<>();
                        for (ParseNode directChild : node.getDirectChildren()) {
                            children.add(directChild.getSynthesized());
                        }
                        
                        AbstractSyntaxNode abstractSyntaxNode;
                        if(children.get(0).getChildList().isEmpty()) {
                            abstractSyntaxNode = new AbstractSyntaxNode(ASTNodeType.qualifiers_and_specifiers,
                                    children);
                        } else {
                            abstractSyntaxNode = children.get(0);
                        }
                        CXType cxType = environment.getType(abstractSyntaxNode);
                        node.setSynthesized(
                                abstractSyntaxNode.addType(cxType)
                        );
                        return true;
                    }
                    case "GenericInstanceTypeList": {
                        node.printTreeForm();
                        return false;
                    }
                    case "GenericInstanceInitParameters": {
                        AbstractSyntaxNode next = new AbstractSyntaxNode(ASTNodeType.generic_init,
                                getCatNode("AbstractTypeNameList").getSynthesized()
                        .getChild(0)
                        .getDirectChildren()
                        );
                        node.setSynthesized(
                                next
                        );
                        return true;
                    }
                    case "AbstractTypeNameList": {
                        String entry = "TypeName";
                        String HeadCatName = "AbstractTypeNameList";
                        String TailCatName = "AbstractTypeNameListTail";

                        AbstractSyntaxNode[] array = foldList(node, entry,
                                HeadCatName, TailCatName);


                        node.setSynthesized(
                                new AbstractSyntaxNode(ASTNodeType.parameterized_types, array)
                        );
                        return true;
                    }
                    case "GenericInstanceParameters": {
                        node.printTreeForm();
                        AbstractSyntaxNode varianceList = node.getCategoryNode("VarianceList").getSynthesized();
                        varianceList.printTreeForm();
                        return false;
                    }
                    case "VarianceList": {
                        return false;
                    }
                    case "Enum": {
                        node.printTreeForm();
                        AbstractSyntaxNode identifier = node.getChild(0).getSynthesized();
                        AbstractSyntaxNode idList = getCatNode("IdentifierList").getSynthesized();
                        AbstractSyntaxNode abstractSyntaxNode = new AbstractSyntaxNode(
                                ASTNodeType._enum,
                                identifier,
                                idList
                        );
                        CXType enumType = environment.getType(abstractSyntaxNode);

                        node.setSynthesized(
                                abstractSyntaxNode.addType(enumType)
                        );
                        return true;
                        //throw new Error("Enums not yet implemented");
                    }
                    case "EnumMember": {
                        node.printTreeForm();
                        EnumType enumType = (EnumType) environment.getType(node.getChild(0).getSynthesized());
                        node.setSynthesized(
                                new TypedAbstractSyntaxNode(
                                        ASTNodeType.enum_member,
                                        enumType,
                                        node.getLeafNode(t_id).getSynthesized()
                                )
                        );
                        return true;
                    }
                    default:
                        error("No Action Routine for " + node.getCategory());
                        cont = false;
                }
                
            } catch (SynthesizedMissingException e) {
                if(!enactActionRoutine(e.node)) return false;
            } catch (InheritMissingError | MissingCategoryNodeError e) {
                e.printStackTrace();
                for (CategoryNode categoryNode : catNodeStack) {
                    System.out.println(categoryNode.getCategory());
                }
                error(e.getMessage());
                if(e instanceof MissingCategoryNodeError) {
                    node.printTreeForm();
                }
                cont = false;
            }catch ( InvalidPrimitiveException | TypeDoesNotExist e) {
                ICompilationSettings.debugLog.severe("Unexpected error in Action Routine Applier");
                ICompilationSettings.debugLog.throwing(getClass().getSimpleName(), node.getCategory(), e);
                error(e.getMessage());
                cont = false;
            }catch (AbstractCompilationError e) {
                errors.add(e);
                ICompilationSettings.debugLog.severe("Unexpected error in Action Routine Applier");
                ICompilationSettings.debugLog.throwing(getClass().getSimpleName(), node.getCategory(), e);
                cont = false;
            } catch (Error e) {
                errors.add(new CompilationError(e, findFirstToken(node)));
                ICompilationSettings.debugLog.severe("Unexpected error in Action Routine Applier");
                ICompilationSettings.debugLog.throwing(getClass().getSimpleName(), node.getCategory(), e);
                cont = false;
            }
        }
        return false;
    }
    
    private AbstractSyntaxNode[] foldList(CategoryNode listNode, String entryCategory, String headCatName, String tailCatName) throws SynthesizedMissingException {
        List<AbstractSyntaxNode> childExpressions = new ArrayList<>();
        while (listNode.hasChildren()) {
            
            childExpressions.add(listNode.getCategoryNode(entryCategory).getSynthesized());
            
            CategoryNode listNodeTail = listNode.getCategoryNode(tailCatName);
            if(!listNodeTail.hasChildren()) break;
            
            CategoryNode next = listNodeTail.getCategoryNode(headCatName);
            listNode = next;
        }
        return childExpressions.toArray(new AbstractSyntaxNode[0]);
    }
    
    private AbstractSyntaxNode[] foldList(CategoryNode listNode, TokenType entryTokenType, String headCatName,
                                          String tailCatName) throws SynthesizedMissingException {
        List<AbstractSyntaxNode> childExpressions = new ArrayList<>();
        while (listNode.hasChildren()) {
            
            childExpressions.add(listNode.getLeafNode(entryTokenType).getSynthesized());
            
            CategoryNode listNodeTail = listNode.getCategoryNode(tailCatName);
            if(!listNodeTail.hasChildren()) break;
            
            CategoryNode next = listNodeTail.getCategoryNode(headCatName);
            listNode = next;
        }
        return childExpressions.toArray(new AbstractSyntaxNode[0]);
    }
    
    private AbstractSyntaxNode[] foldList(CategoryNode listNode, String entryCategory, String headCatName,
                                          String tailCatName,
                                          AbstractSyntaxNode... entryInherits) throws SynthesizedMissingException {
        List<AbstractSyntaxNode> childExpressions = new ArrayList<>();
        while (listNode.hasChildren()) {
            
            CategoryNode entryNode = listNode.getCategoryNode(entryCategory);
            for (int i = 0; i < entryInherits.length; i++) {
                entryNode.setInherit(entryInherits[i], i);
            }
            childExpressions.add(entryNode.getSynthesized());
            
            CategoryNode listNodeTail = listNode.getCategoryNode(tailCatName);
            if(!listNodeTail.hasChildren()) break;
            
            CategoryNode next = listNodeTail.getCategoryNode(headCatName);
            listNode = next;
        }
        return childExpressions.toArray(new AbstractSyntaxNode[0]);
    }
}
