package radin.core.semantics;

import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.utility.ICompilationSettings;

import java.util.HashMap;

import static radin.core.semantics.AbstractSyntaxNode.cleanNameToType;

public enum ASTNodeType {
    /**
     * An operator
     */
    operator("op"),
    /**
     * A binary operation
     */
    binop("binop"),
    /**
     * A unary operation
     */
    uniop("uniop"),
    /**
     * Represents a declaration of a variable.
     * <P>
     * Must be a {@link TypedAbstractSyntaxNode} with a {@link TypedAbstractSyntaxNode#getCxType()} equal to the
     * declaring type
     */
    declaration("dec"),
    /**
     * Assign a variable to a value
     */
    assignment("assign"),
    /**
     * The type of the assignment, whether it's a normal assign or an operation assign
     */
    assignment_type("assign_type"),
    /**
     * Represents a ternary operation
     */
    ternary("?:"),
    /**
     * Gets the e-th value of an array
     */
    array_reference("arr_get"),
    /**
     * Represents a operation after a value
     */
    postop("postop"),
    /**
     * Represents either a number or a character
     */
    literal("literal"),
    /**
     * Represents an identifier
     */
    id("id"),
    /**
     * Represents a string literal
     *
     * If {@link ICompilationSettings#autoCreateStrings()} is true, then the type of these objects are std::String*,
     * otherwise they are const char*
     */
    string("string"),
    /**
     * Represents a series of Expressions
     */
    sequence("sequence"),
    /**
     * Represents a typename
     */
    typename("type"),
    /**
     * Represents the parameter declarations of a function
     */
    parameter_list("params"),
    /**
     * Represents a stand-alone function call
     * <P>Children consists of:
     * <ol>
     *     <li>{@link #id}</li>
     *     <li>{@link #sequence}</li>
     * </ol>
     * </P>
     */
    function_call("func_call"),
    /**
     * Represents a method call on a class object
     *
     * <P>Children consists of:
     * <ol>
     *     <li>Some target object</li>
     *     <li>{@link #id}</li>
     *     <li>{@link #sequence}</li>
     * </ol>
     * </P>
     */
    method_call("method_call"),
    /**
     * Represents a field get of a compound type object
     */
    field_get("field_get"),
    /**
     * Represents an if statement
     * <p>
     * Children consist of
     * <ol>
     *     <li>An expression</li>
     *     <li>A statement</li>
     *     <li>A statement, or {@link #empty} if there's no else present</li>
     * </ol>
     */
    if_cond("if"),
    /**
     * Represents a while statment
     * <p>
     * Children consist of
     * <ol>
     *     <li>An expression</li>
     *     <li>A statement</li>
     * </ol>
     */
    while_cond("while"),
    /**
     * Represents a do-while statement
     * <p>
     * Children consist of
     * <ol>
     *     <li>A statement</li>
     *     <li>An expression</li>
     * </ol>
     */
    do_while_cond("do_while"),
    /**
     * Represents a for statement
     * <p>
     * Children consist of
     * <ol>
     *     <li>A statement or {@link #empty}</li>
     *     <li>A statement or {@link #empty}</li>
     *     <li>An expression or {@link #empty}</li>
     *     <li>A statement</li>
     * </ol>
     */
    for_cond("for"),
    /**
     * Represents a return statement
     * <p>
     *     Must have a child that is either an expression or {@link #empty}.
     *     If the child is empty, the statement is equivalent to {@code return;}
     * </p>
     */
    _return("return"),
    /**
     * Represents a function definition
     * <p>
     * Children consist of
     * <ol>
     *     <li>{@link #id}</li>
     *     <li>{@link #parameter_list}</li>
     *     <li>{@link #compound_statement}</li>
     * </ol>
     * <p>
     * Must be a {@link TypedAbstractSyntaxNode} with a {@link TypedAbstractSyntaxNode#getCxType()} equal to the
     * declaring type
     */
    function_definition("function_definition"),
    /**
     *
     */
    basic_compound_type_dec("struct/union_dec"),
    /**
     * Represents a list of specifiers
     */
    specifiers("specifiers"),
    /**
     * Represents a specifier (type)
     */
    specifier("specifier"),
    /**
     * Represents a qualifier
     */
    qualifier("qualifier"),
    qualifiers("qualifiers"),
    qualifiers_and_specifiers("qualifiers_and_specifiers"),
    class_level_decs("class_level_declarations"),
    class_type_definition("class_definition"),
    class_type_declaration("class_declaration"),
    class_type_name("class_type_name"),
    compound_type_reference("compound_type_reference"),
    typedef("typedef"),
    top_level_decs("top_level_decs"),
    indirection("indirection"),
    addressof("address_of"),
    cast("cast"),
    empty("empty"),
    array_type("array"),
    pointer_type("pointer"),
    abstract_declarator("abstract_declarator"),
    struct("struct"),
    union("union"),
    _class("class"),
    basic_compound_type_fields("struct/union_field_declarations"),
    basic_compound_type_field("struct/union_field_declaration"),
    declarations("declarations"),
    initialized_declaration("initialized_declaration"),
    compound_statement("compound_statement"),
    sizeof("sizeof"),
    constructor_call("constructor_call"),
    function_description("function_description"),
    visibility("visibility"),
    class_level_declaration("class_level_declaration"),
    constructor_definition("constructor_definition"),
    _virtual("virtual"),
    _super("super"),
    inherit("inherit"),
    namespaced("namespaced"),
    implement("implement"),
    implementing("implementing"),
    using("using"),
    alias("alias"),
    _import("import"),
    compilation_tag("compilation_tag"),
    compilation_tag_list("compilation_tag_list"),
    constructor_description("constructor_description"),
    typeid("typeid"),
    syntax("syntax"),
    _true("true"),
    _false("false"),
    ast("ast"),
    generic("generic"),
    trait("trait"),
    id_list("id_list")
    ;
    
    
    private String name;
    
    ASTNodeType(String name) {
        this.name = name;
        cleanNameToType.putIfAbsent(name, this);
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    
    
    
}
