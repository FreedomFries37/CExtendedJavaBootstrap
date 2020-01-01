package radin.interphase.lexical;

public enum TokenType {
    t_char("char"),
    t_const("const"),
    t_do("do"),
    t_double("double"),
    t_else("else"),
    t_float("float"),
    t_for("for"),
    t_if("if"),
    t_int("int"),
    t_long("long"),
    t_return("return"),
    t_short("short"),
    t_static("static"),
    t_struct("struct"),
    t_typedef("typedef"),
    t_union("union"),
    t_unsigned("unsigned"),
    t_void("void"),
    t_while("while"),
    t_class("class"),
    t_public("public"),
    t_private("private"),
    t_literal("literal"),
    t_id("id"),
    t_string("string"),
    t_operator_assign("?="),
    t_lshift("<<"),
    t_rshift(">>"),
    t_inc("++"),
    t_dec("--"),
    t_arrow("->"),
    t_dand("&&"),
    t_dor("||"),
    t_lte("<="),
    t_gte(">="),
    t_eq("=="),
    t_neq("!="),
    t_semic(";"),
    t_lcurl("{"),
    t_rcurl("}"),
    t_comma(","),
    t_colon(":"),
    t_assign("="),
    t_lpar("("),
    t_rpar(")"),
    t_lbrac("["),
    t_rbrac("]"),
    t_dot("."),
    t_and("&"),
    t_bang("!"),
    t_not("~"),
    t_minus("-"),
    t_add("+"),
    t_star("*"),
    t_fwslash("/"),
    t_percent("%"),
    t_lt("<"),
    t_gt(">"),
    t_crt("^"),
    t_bar("|"),
    t_qmark("?"),
    t_ellipsis("..."),
    t_namespace("::"),
    t_new("new"),
    t_super("super"),
    t_virtual("virtual"),
    t_eof("EOF"),
    t_typename("typename"),
    t_sizeof("sizeof"),
    t_internal("internal");
    
    private String str;
    
    TokenType(String str) {
        this.str = str;
    }
    
    public String getStr() {
        return str;
    }
    
    @Override
    public String toString() {
        return str;
    }
}
