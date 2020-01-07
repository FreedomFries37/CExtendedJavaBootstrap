package radin.v2.parsing.grammars;

import radin.core.lexical.TokenType;
import radin.v2.parsing.structure.GrammarBuilder;

import static radin.core.lexical.TokenType.*;

public class StandardCGrammar extends GrammarBuilder<TokenType> {
    
    public StandardCGrammar() {
        inherit(new ExpressionGrammar());
        
        addProduction("StorageClassSpecifier", t_typedef);
        addProduction("StorageClassSpecifier", t_static);
        
        addProduction("TypeSpecifier", t_void);
        addProduction("TypeSpecifier", t_char);
        addProduction("TypeSpecifier", t_short);
        addProduction("TypeSpecifier", t_int);
        addProduction("TypeSpecifier", t_long);
        addProduction("TypeSpecifier", t_float);
        addProduction("TypeSpecifier", t_double);
        addProduction("TypeSpecifier", t_unsigned);
        addProduction("TypeSpecifier", symbol("StructOrUnionSpecifier"));
        // addProduction("TypeSpecifier", symbol("EnumSpecifier"));
        addProduction("TypeSpecifier", t_typename);
        
        addProduction("StructOrUnionSpecifier", symbol("StructOrUnion"), t_id, t_lcurl, symbol("StructDeclarationList"), t_rcurl);
        addProduction("StructOrUnionSpecifier", symbol("StructOrUnion"), t_lcurl, symbol("StructDeclarationList"), t_rcurl);
        addProduction("StructOrUnionSpecifier", symbol("StructOrUnion"), t_id);
        
        addProduction("StructOrUnion", t_struct);
        addProduction("StructOrUnion", t_union);
        
        addProduction("StructDeclarationList", symbol("StructDeclaration"));
        addProduction("StructDeclarationList", "StructDeclarationList", symbol("StructDeclaration"));
        
        addProduction("StructDeclaration", symbol("SpecifierQualifierList"), symbol("StructDeclaratorList"), t_semic);
        
        addProduction("SpecifierQualifierList", symbol("TypeSpecifier"), "SpecifierQualifierList");
        addProduction("SpecifierQualifierList", symbol("TypeSpecifier"));
        addProduction("SpecifierQualifierList", symbol("TypeQualifier"), "SpecifierQualifierList");
        addProduction("SpecifierQualifierList", symbol("TypeQualifier"));
        
        addProduction("StructDeclaratorList", symbol("StructDeclarator"));
        addProduction("StructDeclaratorList", "StructDeclaratorList", symbol("StructDeclarator"));
        
        addProduction("StructDeclarator", symbol("Declarator"));
        addProduction("StructDeclarator", symbol("Declarator"), t_colon, "ConstantExpression");
        addProduction("StructDeclarator", t_colon, "ConstantExpression");
        
        addProduction("TypeQualifier", t_const);
        
        addProduction("Declarator", symbol("Pointer"), symbol("DirectDeclarator"));
        addProduction("Declarator", symbol("DirectDeclarator"));
        
        addProduction("DirectDeclarator", t_id);
        addProduction("DirectDeclarator", t_lpar, "Declarator", t_rpar);
        addProduction("DirectDeclarator", "DirectDeclarator", t_lbrac, "ConstantExpression", t_rbrac);
        addProduction("DirectDeclarator", "DirectDeclarator", t_lbrac, t_rbrac);
        addProduction("DirectDeclarator", "DirectDeclarator", t_lpar, symbol("ParameterTypeList"), t_rpar);
        addProduction("DirectDeclarator", "DirectDeclarator", t_lpar, symbol("IdentifierList"), t_rpar);
        addProduction("DirectDeclarator", "DirectDeclarator", t_lpar, t_rpar);
        
        addProduction("Pointer", t_star);
        addProduction("Pointer", t_star, symbol("TypeQualifierList"));
        addProduction("Pointer", t_star, "Pointer");
        addProduction("Pointer", t_star, symbol("TypeQualifierList"), "Pointer");
        
        addProduction("TypeQualifierList", "TypeQualifier");
        addProduction("TypeQualifierList", "TypeQualifierList", "TypeQualifier");
        
        addProduction("ParameterTypeList", symbol("ParameterList"));
        addProduction("ParameterTypeList", symbol("ParameterList"), t_ellipsis);
    
        addProduction("ParameterList", "ParameterList", symbol("ParameterDeclaration"));
        addProduction("ParameterList", symbol("ParameterDeclaration"));
        
        addProduction("ParameterDeclaration", symbol("DeclarationSpecifiers"), "Declarator");
        addProduction("ParameterDeclaration", symbol("DeclarationSpecifiers"), symbol("AbstractDeclarator"));
        addProduction("ParameterDeclaration", symbol("DeclarationSpecifiers"));
        
        addProduction("IdentifierList", t_id);
        addProduction("IdentifierList", "IdentifierList", t_comma, t_id);
        
        addProduction("TypeName", "SpecifierQualifierList");
        addProduction("TypeName", "SpecifierQualifierList", "AbstractDeclarator");
        
        addProduction("AbstractDeclarator", "Pointer");
        addProduction("AbstractDeclarator", symbol("DirectAbstractDeclarator"));
        addProduction("AbstractDeclarator", "Pointer", symbol("DirectAbstractDeclarator"));
        
        addProduction("DirectAbstractDeclarator", t_lpar, "AbstractDeclarator", t_rpar);
        addProduction("DirectAbstractDeclarator", t_lbrac, "ConstantExpression", t_rbrac);
        addProduction("DirectAbstractDeclarator", "DirectAbstractDeclarator", t_lbrac, "ConstantExpression", t_rbrac);
        addProduction("DirectAbstractDeclarator", t_lbrac, t_rbrac);
        addProduction("DirectAbstractDeclarator", "DirectAbstractDeclarator", t_lbrac, t_rbrac);
        addProduction("DirectAbstractDeclarator", t_lpar, symbol("ParameterTypeList"), t_rpar);
        addProduction("DirectAbstractDeclarator","DirectAbstractDeclarator", t_lpar, t_rpar);
        addProduction("DirectAbstractDeclarator", "DirectAbstractDeclarator", t_lpar,
                symbol("ParameterTypeList"), t_rpar);
        
        addProduction("DeclarationSpecifiers", symbol("StorageClassSpecifier"));
        addProduction("DeclarationSpecifiers", symbol("StorageClassSpecifier"), "DeclarationSpecifiers");
        addProduction("DeclarationSpecifiers", symbol("TypeSpecifier"));
        addProduction("DeclarationSpecifiers", symbol("TypeSpecifier"), "DeclarationSpecifiers");
        addProduction("DeclarationSpecifiers", symbol("TypeQualifier"));
        addProduction("DeclarationSpecifiers", symbol("TypeQualifier"), "DeclarationSpecifiers");
        
        addProduction("Declaration", "DeclarationSpecifiers", t_semic);
        addProduction("Declaration", "DeclarationSpecifiers", symbol("InitDeclaratorList"), t_semic);
        
        addProduction("InitDeclaratorList", symbol("InitDeclarator"));
        addProduction("InitDeclaratorList", symbol("InitDeclaratorList"), t_comma, symbol("InitDeclarator"));
        
        addProduction("InitDeclarator", "Declarator");
        addProduction("InitDeclarator", "Declarator", t_assign, symbol("Initializer"));
        
        addProduction("Initializer", "AssignmentExpression");
        addProduction("Initializer", t_lcurl, symbol("InitializerList"), t_rcurl);
        addProduction("Initializer", t_lcurl, symbol("InitializerList"), t_comma, t_rcurl);
        
        addProduction("InitializerList", "Initializer");
        addProduction("InitializerList", "Initializer", t_comma, "Initializer");
        
        //addProduction("ExternalDeclaration", "Declaration");
        addProduction("ExternalDeclaration", symbol("FunctionDefinition"));
        
        addProduction("TranslationUnit", "ExternalDeclaration");
        addProduction("TranslationUnit", "TranslationUnit", "ExternalDeclaration");
        /*
        addProduction("FunctionDefinition", "DeclarationSpecifiers", "Declarator", "DeclarationList", symbol(
                "CompoundStatement"));
        addProduction("FunctionDefinition", "DeclarationSpecifiers", "Declarator", symbol("CompoundStatement"));
        addProduction("FunctionDefinition", "Declarator","DeclarationList", symbol("CompoundStatement"));
        */
        addProduction("FunctionDefinition", "DeclarationSpecifiers", "Declarator", symbol("CompoundStatement"));
        
        
        
        addProduction("CompoundStatement", t_lcurl, t_rcurl);
        addProduction("CompoundStatement", t_lcurl, symbol("StatementList"), t_rcurl);
        
        addProduction("StatementList", symbol("Statement"));
        addProduction("StatementList", "StatementList", symbol("Statement"));
        addProduction("StatementList", symbol("Declaration"));
        addProduction("StatementList", "StatementList", symbol("Declaration"));
        
        addProduction("Statement", symbol("LabeledStatement"));
        addProduction("Statement", symbol("CompoundStatement"));
        addProduction("Statement", symbol("ExpressionStatement"));
        addProduction("Statement", symbol("SelectionStatement"));
        addProduction("Statement", symbol("IterationStatement"));
        addProduction("Statement", symbol("JumpStatement"));
        
        addProduction("LabeledStatement", t_id, t_colon, "Statement");
        
        
        addProduction("ExpressionStatement", t_semic);
        addProduction("ExpressionStatement", "Expression", t_semic);
        
        addProduction("SelectionStatement", t_if, t_lpar, "Expression", t_rpar, "Statement");
        addProduction("SelectionStatement", t_if, t_lpar, "Expression", t_rpar, "Statement", t_else, "Statement");
        
        addProduction("IterationStatement", t_while, t_lpar, "Expression", t_rpar, "Statement");
        addProduction("IterationStatement", t_do, "Statement", t_while, t_lpar, "Expression", t_rpar, t_semic);
        addProduction("IterationStatement", t_for, t_lpar, "ExpressionStatement", "ExpressionStatement", t_rpar,
                "Statement");
        addProduction("IterationStatement", t_for, t_lpar, "ExpressionStatement", "ExpressionStatement",
                "Expression", t_rpar, "Statement");
        
        addProduction("JumpStatement", t_return, t_semic);
        addProduction("JumpStatement", t_return, "Expression", t_semic);
        
        
        
        setStartingSymbol(symbol("ExternalDeclaration"));
    }
}
