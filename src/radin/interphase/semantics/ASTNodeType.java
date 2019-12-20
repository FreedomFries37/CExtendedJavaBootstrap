package radin.interphase.semantics;

public enum ASTNodeType {
    reserved("reserved"),
    _void("void"),
    operator("op"),
    binop("binop"),
    uniop("uniop"),
    declaration("dec"),
    assignment("="),
    literal("literal"),
    id("id"),
    typename("type"),
    object_interaction("object_interact"),
    parameter_list("params"),
    function_call("func_call"),
    method_call("func_call"),
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
    class_level_decs("class_level_declaration"),
    class_type_dec("class_dec"),
    typedef_dec("typedef"),
    top_level_decs("top_level_decs"),
    interactable("interactable"),
    dereference("dereference")
    ;
    
    
    private String name;
    
    ASTNodeType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
