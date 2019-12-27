package radin.semantics;

import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.TypeEnvironment;
import radin.interphase.semantics.exceptions.InvalidPrimitiveException;
import radin.interphase.semantics.types.ArrayType;
import radin.interphase.semantics.types.CXType;
import radin.interphase.semantics.types.CompoundTypeReference;
import radin.interphase.semantics.types.TypeAbstractSyntaxNode;
import radin.parsing.CategoryNode;
import radin.parsing.LeafNode;
import radin.parsing.ParseNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ActionRoutineApplier {
    
    public static final Token $_GLOBAL_$ = new Token(TokenType.t_id, "$GLOBAL$");
    private static final AbstractSyntaxNode GLOBAL_NODE = getGlobal();
    
    private CategoryNode currentCategoryNode() {
        return catNodeStack.peek();
    }
    
    private Stack<CategoryNode> catNodeStack;
    private Stack<String> errors;
    private List<AbstractSyntaxNode> successOrder;
    
    private TypeEnvironment environment;
    
    public ActionRoutineApplier() {
        catNodeStack = new Stack<>();
        successOrder = new LinkedList<>();
        errors = new Stack<>();
        environment = new TypeEnvironment();
    }
    
    public ActionRoutineApplier(TypeEnvironment environment) {
        catNodeStack = new Stack<>();
        successOrder = new LinkedList<>();
        errors = new Stack<>();
        this.environment = environment;
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
        if(errors.peek() == null) {
            errors.pop();
            errors.push(message);
        }
        return false;
    }
    
    public boolean enactActionRoutine(ParseNode node) {
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
                errors.push(null);
                boolean b = enactActionRoutine(catNode);
                if(!b) {
                    System.out.println("Failed to enact action routine for " +
                            String.format("%-30s", node) +
                            (node.hasChildren()? "(CHILDREN = " + ((CategoryNode) node).getAllChildren() + ")" : "") +
                            (errors.peek() == null ? "" : String.format("  %60s", "Error: " + errors.peek())));
                    
                } else {
                    if(!successOrder.contains(node.getSynthesized())) successOrder.add(node.getSynthesized());
                }
                errors.pop();
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
        }
        
        node.setSynthesized(node.getInherit());
        return true;
    }
    
    
    
    private boolean enactActionRoutine(CategoryNode node) {
        boolean cont = true;
        
        
        while(cont) {
            try {
                switch (node.getCategory()) {
                    case "TopLevelDeclaration": {
                        if(node.hasChildCategory("FunctionDefinition")) {
                            getCatNode("FunctionDefinition").setInherit(
                                    AbstractSyntaxNode.EMPTY
                            );
                        }
                        // MUST CONTINUE THROUGH INTO NEXT SECTION
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
                            AbstractSyntaxNode typeName = getCatNode("TypeName").getSynthesized();
                            CXType type = environment.getType(typeName);
                            
                            node.setSynthesized(
                                    new TypeAbstractSyntaxNode(ASTNodeType.sizeof, type)
                            );
                            return true;
                        }
                        
                        
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
                        if (node.firstIs(TokenType.t_id)) {
                            AbstractSyntaxNode name = node.getLeafNode(TokenType.t_id).getSynthesized();
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
                            
                            AbstractSyntaxNode typename = node.getLeafNode(TokenType.t_typename).getSynthesized();
                            
                            CXType type = environment.getType(typename);
                            AbstractSyntaxNode args = getCatNode("ArgsList").getSynthesized();
                            
                            node.setSynthesized(
                                    new TypeAbstractSyntaxNode(ASTNodeType.constructor_call, type, args)
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
                                
                                
                                AbstractSyntaxNode name = node.getLeafNode(TokenType.t_id).getSynthesized();
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
                            
                            if(object.equals(GLOBAL_NODE)) {
                                node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.function_call, name,
                                        call));
                            }
                            else node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.method_call, object, name,
                                    call));
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
                        
                        
                        node.setSynthesized(new AbstractSyntaxNode(ASTNodeType.args_list, array));
                        return true;
                    }
                    case "CastExpression": {
                        AbstractSyntaxNode factor = getCatNode("Factor")
                                .getSynthesized();
                        AbstractSyntaxNode type = getCatNode("TypeName").getSynthesized();
                        CXType cxtype = environment.getType(type);
                        node.setSynthesized(
                                new TypeAbstractSyntaxNode(ASTNodeType.cast, cxtype, factor)
                        );
                        
                        return true;
                    }
                    case "TypeName": {
                        
                        CategoryNode abstractDeclarator = getCatNode("AbstractDeclarator");
                        abstractDeclarator
                                .setInherit(
                                        getCatNode("SpecsAndQuals")
                                                .getSynthesized()
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
                        
                        if(node.firstIs(TokenType.t_id, TokenType.t_char, TokenType.t_int, TokenType.t_long,
                                TokenType.t_float, TokenType.t_double, TokenType.t_unsigned, TokenType.t_void)) {
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.specifier, ((LeafNode) node.getChild(0)).getToken())
                            );
                            return true;
                        } else if(node.firstIs("StructOrUnionSpecifier")) {
                            node.setSynthesized(getCatNode("StructOrUnionSpecifier").getSynthesized());
                            return true;
                        } else if(node.firstIs(TokenType.t_typename)) {
                            CXType type = environment.getType(node.getLeafNode(TokenType.t_typename).getSynthesized());
                            node.setSynthesized(
                                    new TypeAbstractSyntaxNode(ASTNodeType.specifier, type)
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
                        if(node.hasChildren()) {
                            AbstractSyntaxNode dir;
                            if(node.firstIs(TokenType.t_lbrac)) {
                                dir = new AbstractSyntaxNode(ASTNodeType.array_type);
                            } else return false;
                            
                            getCatNode("DirectAbstractDeclarator", 2).setInherit(dir);
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.abstract_declarator,
                                            node.getInherit(),
                                            getCatNode("DirectAbstractDeclarator", 2)
                                                    .getSynthesized()
                                    )
                            );
                            
                        } else {
                            node.setSynthesized(node.getInherit());
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
                        AbstractSyntaxNode typeAST = getCatNode("TypeName").getSynthesized();
                        AbstractSyntaxNode id = node.getLeafNode(TokenType.t_id).getSynthesized();
                        
                        
                        CXType type = environment.addTypeDefinition(typeAST, id.getToken().getImage());
                        
                        node.setSynthesized(
                                new TypeAbstractSyntaxNode(ASTNodeType.typedef, type, id)
                        );
                        return true;
                    }
                    case "StructOrUnionSpecifier": {
                        AbstractSyntaxNode structOrUnion = node.getCategoryNode("StructOrUnion").getSynthesized();
                        LeafNode nameNode = node.getLeafNode(TokenType.t_id);
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
                                new TypeAbstractSyntaxNode(mid.getType(), type, mid.getChild(0))
                        );
                        
                        return true;
                    }
                    case "ClassSpecifier": {
                        LeafNode nameNode = node.getLeafNode(TokenType.t_id);
                        if(nameNode == null) nameNode = node.getLeafNode(TokenType.t_typename);
                        AbstractSyntaxNode nameAST = nameNode != null? nameNode.getSynthesized() : null;
                        AbstractSyntaxNode specifierInner = new AbstractSyntaxNode(
                                ASTNodeType.compound_type_reference,
                                new AbstractSyntaxNode(ASTNodeType._class),
                                nameAST
                        );
                        AbstractSyntaxNode outer = new AbstractSyntaxNode(ASTNodeType.specifier, specifierInner);
                        CXType type = environment.getType(outer);
                        node.setSynthesized(
                                new TypeAbstractSyntaxNode(ASTNodeType.specifier, type, specifierInner)
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
                        AbstractSyntaxNode declarationSpecifiers = getCatNode("DeclarationSpecifiers").getSynthesized();
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
                        TypeAbstractSyntaxNode declarator = ((TypeAbstractSyntaxNode) getCatNode("Declarator").getSynthesized());
                        
                        node.setSynthesized(
                                new TypeAbstractSyntaxNode(ASTNodeType.basic_compound_type_field,
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
                        AbstractSyntaxNode name = node.getLeafNode(TokenType.t_id).getSynthesized();
                        
                        getCatNode("DirectDeclaratorTail").setInherit(mid, 0);
                        getCatNode("DirectDeclaratorTail").setInherit(name, 1);
                        
                        TypeAbstractSyntaxNode directDeclaratorTail = (TypeAbstractSyntaxNode) getCatNode("DirectDeclaratorTail").getSynthesized();
                        CXType type = directDeclaratorTail.getCxType();
                        node.setSynthesized(
                                directDeclaratorTail
                        );
                        return true;
                    }
                    case "DirectDeclaratorTail": {
                        System.out.println(node.getAllChildren());
                        CXType type = environment.getType(node.getInherit(0));
                        if(node.hasChildren()) {
                            //node.printTreeForm();
                            if(node.firstIs("ParameterTypeList")) {
                                AbstractSyntaxNode parameterTypeList = getCatNode("ParameterTypeList").getSynthesized();
                                
                                node.setSynthesized(
                                        new TypeAbstractSyntaxNode(
                                                ASTNodeType.function_description,
                                                type,
                                                node.getInherit(1),
                                                parameterTypeList
                                        )
                                );
                                
                            } else if(node.firstIs(TokenType.t_lbrac)) {
                                CXType newType = new ArrayType(type);
                                node.setSynthesized(
                                        new TypeAbstractSyntaxNode(
                                                ASTNodeType.declaration,
                                                newType,
                                                node.getInherit(1)
                                        )
                                );
                            } else
                                return false;
                        } else {
                            node.setSynthesized(
                                    new TypeAbstractSyntaxNode(
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
                        AbstractSyntaxNode declarationSpecifiers = getCatNode("DeclarationSpecifiers").getSynthesized();
                        CategoryNode declarator = getCatNode("Declarator");
                        declarator.setInherit(
                                declarationSpecifiers
                        );
                        TypeAbstractSyntaxNode declaratorAST = ((TypeAbstractSyntaxNode) declarator.getSynthesized());
                        AbstractSyntaxNode compound = getCatNode("CompoundStatement").getSynthesized();
                        
                        AbstractSyntaxNode parameters;
                        if(declaratorAST.hasChild(ASTNodeType.parameter_list)) {
                            parameters = declaratorAST.getChild(ASTNodeType.parameter_list);
                        } else {
                            parameters = new AbstractSyntaxNode(ASTNodeType.parameter_list);
                        }
                        
                        node.setSynthesized(
                                new TypeAbstractSyntaxNode(
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
                        
                        System.out.println(node.getAllChildren());
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
                            System.out.println(node.getAllChildren());
                            
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
                        
                        AbstractSyntaxNode specifiers = getCatNode("DeclarationSpecifiers").getSynthesized();
                        CategoryNode declarator = getCatNode("Declarator");
                        declarator.setInherit(specifiers);
                        
                        node.setSynthesized(
                                declarator.getSynthesized()
                        );
                        
                        return true;
                    }
                    case "ClassDeclaration": {
                        
                        AbstractSyntaxNode name = node.getLeafNode(TokenType.t_id).getSynthesized();
    
                        environment.addTypeDefinition(new CompoundTypeReference(CompoundTypeReference.CompoundType._class, name.getToken().getImage()) ,name.getToken().getImage());
    
                        AbstractSyntaxNode declarations = getCatNode("ClassDeclarationList").getSynthesized();
                        
                        
                        AbstractSyntaxNode classDefinition;
                        if(node.hasChildCategory("Inherit")) {
                            AbstractSyntaxNode inherit = new AbstractSyntaxNode(ASTNodeType.inherit,
                                    getCatNode("Inherit").getLeafNode(TokenType.t_typename).getSynthesized());
                            classDefinition =
                                    new AbstractSyntaxNode(ASTNodeType.class_type_definition, name, inherit, declarations);
                        } else {
                            classDefinition =
                                    new AbstractSyntaxNode(ASTNodeType.class_type_definition, name, declarations);
                        }
                        
                        CXType cxClass = environment.getType(classDefinition);
                        environment.addTypeDefinition(cxClass ,name.getToken().getImage());
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
                        AbstractSyntaxNode visibility;
                        if(node.hasChildCategory("Visibility")) {
                            visibility = getCatNode("Visibility").getSynthesized();
                        } else {
                            visibility = new AbstractSyntaxNode(ASTNodeType.visibility, new Token(TokenType.t_internal));
                        }
    
                        System.out.println(node.getAllChildren());
                        AbstractSyntaxNode inner;
                        if(node.hasChildCategory("Declaration")) {
                            inner = getCatNode("Declaration").getSynthesized();
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
    
                        return true;
                    }
                    case "ConstructorDefinition": {
                        AbstractSyntaxNode typename = node.getLeafNode(TokenType.t_typename).getSynthesized();
                        AbstractSyntaxNode parameterList = getCatNode("ParameterList").getSynthesized();
                        AbstractSyntaxNode compoundStatement = getCatNode("CompoundStatement").getSynthesized();
                        
                        
                        CXType type = environment.getType(typename);
                        if(node.hasChildCategory("ArgsList")) {
                            AbstractSyntaxNode priorAST = node.getChild(2).getSynthesized();
                            AbstractSyntaxNode argsAST = node.getCategoryNode("ArgsList").getSynthesized();
    
                            node.setSynthesized(
                                    new TypeAbstractSyntaxNode(ASTNodeType.constructor_definition, type, typename,
                                            parameterList, priorAST, argsAST, compoundStatement)
                            );
                        } else {
                            node.setSynthesized(
                                    new TypeAbstractSyntaxNode(ASTNodeType.constructor_definition, type, typename,
                                            parameterList, compoundStatement)
                            );
                        }
                        return true;
                    }
                    default:
                        error("No Action Routine for " + node.getCategory());
                        cont = false;
                }
                
            } catch (SynthesizedMissingException e) {
                if(!enactActionRoutine(e.node)) return false;
            } catch (InheritMissingError | MissingCategoryNodeError | InvalidPrimitiveException e) {
                e.printStackTrace();
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
        return childExpressions.toArray(new AbstractSyntaxNode[childExpressions.size()]);
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
        return childExpressions.toArray(new AbstractSyntaxNode[childExpressions.size()]);
    }
    
    private static AbstractSyntaxNode getGlobal() {
        return new AbstractSyntaxNode(ASTNodeType.id,
                $_GLOBAL_$);
    }
}
