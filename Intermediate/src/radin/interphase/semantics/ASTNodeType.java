package radin.interphase.semantics;

public enum ASTNodeType {
    reserved("reserved"),
    _void("void"),
    operator("op"),
    binop("binop"),
    uniop("uniop"),
    declaration("dec"),
    assignment("?="),
    ternary("?:"),
    array_reference("[E]"),
    postop("postop"),
    literal("literal"),
    id("id"),
    string("string"),
    args_list("sequence"),
    typename("type"),
    object_interaction("object_interact"),
    parameter_list("params"),
    function_call("func_call"),
    method_call("method_call"),
    field_get("field_get"),
    if_cond("if"),
    while_cond("while"),
    do_while_cond("do_while"),
    conditional("conditional"),
    _return("return"),
    block("block"),
    statement("statement"),
    parameters_dec("params_dec"),
    function_dec("func_dec"),
    basic_compound_type_dec("struct/union_dec"),
    inheritence("inheritence"),
    specifiers("specifiers"),
    specifier("specifier"),
    qualifier("qualifier"),
    qualifiers("qualifiers"),
    qualifiers_and_specifiers("qualifiers_and_specifiers"),
    class_level_decs("class_level_declaration"),
    class_type_dec("class_dec"),
    class_type_name("class_type_name"),
    basic_compound_type_reference("compound_type_reference"),
    class_type_reference("class_type_reference"),
    typedef("typedef"),
    top_level_decs("top_level_decs"),
    interactable("interactable"),
    indirection("indirection"),
    addressof("address_of"),
    cast("cast"),
    empty("empty"),
    array_type("[]"),
    pointer_type("*"),
    abstract_declarator("abstract_declarator"),
    declarator("declarator"),
    struct("struct"),
    union("union"),
    _class("class"),
    basic_compound_type_fields("struct/union_field_declarations"),
    basic_compound_type_field("struct/union_field_declaration")
    ;
    
    
    private String name;
    
    ASTNodeType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
