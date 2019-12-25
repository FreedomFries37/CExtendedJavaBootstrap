package radin.semantics;

import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;
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
    
    public ActionRoutineApplier() {
        catNodeStack = new Stack<>();
        successOrder = new LinkedList<>();
        errors = new Stack<>();
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
        switch (node.getToken().getType()) {
            case t_id: {
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.id, node.getToken()));
                break;
            }
            case t_literal: {
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.literal, node.getToken()));
                break;
            }
            case t_string: {
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.string, node.getToken()));
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
                node.setInherit(new AbstractSyntaxNode(ASTNodeType.operator, node.getToken()));
            }
        }
        
        node.setSynthesized(node.getInherit());
        return true;
    }
    
    
    
    private boolean enactActionRoutine(CategoryNode node) {
        boolean cont = true;
        
        
        while(cont) {
            try {
                switch (node.getCategory()) {
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
                    case "TopLevelDeclaration": {
                        node.setSynthesized(node.getChild(0).getSynthesized());
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
                        } else if (node.firstIs(TokenType.t_minus, TokenType.t_not, TokenType.t_bang,
                                TokenType.t_and, TokenType.t_add, TokenType.t_inc, TokenType.t_dec)) {
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
                                        new AbstractSyntaxNode(ASTNodeType.postop, node.getChild(0).getSynthesized())
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
                        
                        node.setSynthesized(
                                new AbstractSyntaxNode(ASTNodeType.cast, type, factor)
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
                                TokenType.t_float, TokenType.t_double, TokenType.t_unsigned)) {
                            node.setSynthesized(
                                    new AbstractSyntaxNode(ASTNodeType.specifier, ((LeafNode) node.getChild(0)).getToken())
                            );
                            return true;
                        } else if(node.firstIs("StructOrUnionSpecifier")) {
                            node.setSynthesized(getCatNode("StructOrUnionSpecifier").getSynthesized());
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
                    
                    }
                    default:
                        error("No Action Routine for " + node.getCategory());
                        cont = false;
                }
                
            } catch (SynthesizedMissingException e) {
                if(!enactActionRoutine(e.node)) return false;
            } catch (InheritMissingError | MissingCategoryNodeError e) {
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
    
    private static AbstractSyntaxNode getGlobal() {
        return new AbstractSyntaxNode(ASTNodeType.id,
                $_GLOBAL_$);
    }
}
