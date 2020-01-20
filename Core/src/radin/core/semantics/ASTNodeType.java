package radin.core.semantics;

public enum ASTNodeType {
    operator("op"),
    binop("binop"),
    uniop("uniop"),
    declaration("dec"),
    assignment("assign"),
    assignment_type("assign_type"),
    ternary("?:"),
    array_reference("[E]"),
    postop("postop"),
    literal("literal"),
    id("id"),
    string("string"),
    sequence("sequence"),
    typename("type"),
    parameter_list("params"),
    function_call("func_call"),
    method_call("method_call"),
    field_get("field_get"),
    if_cond("if"),
    while_cond("while"),
    do_while_cond("do_while"),
    for_cond("for"),
    _return("return"),
    function_definition("func_dec"),
    basic_compound_type_dec("struct/union_dec"),
    specifiers("specifiers"),
    specifier("specifier"),
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
    typeid("typeid")
    ;
    
    
    private String name;
    
    ASTNodeType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
