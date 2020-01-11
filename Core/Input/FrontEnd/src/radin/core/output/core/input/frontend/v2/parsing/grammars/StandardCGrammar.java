package radin.core.output.core.input.frontend.v2.parsing.grammars;

import radin.core.output.core.input.frontend.v2.parsing.structure.GrammarBuilder;
import radin.core.lexical.TokenType;

public class StandardCGrammar extends GrammarBuilder<TokenType> {
    
    public StandardCGrammar() {
        inherit(new ExpressionGrammar());
        
        addProduction("StorageClassSpecifier", TokenType.t_typedef);
        addProduction("StorageClassSpecifier", TokenType.t_static);
        
        addProduction("TypeSpecifier", TokenType.t_void);
        addProduction("TypeSpecifier", TokenType.t_char);
        addProduction("TypeSpecifier", TokenType.t_short);
        addProduction("TypeSpecifier", TokenType.t_int);
        addProduction("TypeSpecifier", TokenType.t_long);
        addProduction("TypeSpecifier", TokenType.t_float);
        addProduction("TypeSpecifier", TokenType.t_double);
        addProduction("TypeSpecifier", TokenType.t_unsigned);
        addProduction("TypeSpecifier", symbol("StructOrUnionSpecifier"));
        // addProduction("TypeSpecifier", symbol("EnumSpecifier"));
        addProduction("TypeSpecifier", TokenType.t_typename);
        
        addProduction("StructOrUnionSpecifier", symbol("StructOrUnion"), TokenType.t_id, TokenType.t_lcurl, symbol("StructDeclarationList"), TokenType.t_rcurl);
        addProduction("StructOrUnionSpecifier", symbol("StructOrUnion"), TokenType.t_lcurl, symbol("StructDeclarationList"), TokenType.t_rcurl);
        addProduction("StructOrUnionSpecifier", symbol("StructOrUnion"), TokenType.t_id);
        
        addProduction("StructOrUnion", TokenType.t_struct);
        addProduction("StructOrUnion", TokenType.t_union);
        
        addProduction("StructDeclarationList", symbol("StructDeclaration"));
        addProduction("StructDeclarationList", "StructDeclarationList", symbol("StructDeclaration"));
        
        addProduction("StructDeclaration", symbol("SpecifierQualifierList"), symbol("StructDeclaratorList"), TokenType.t_semic);
        
        addProduction("SpecifierQualifierList", symbol("TypeSpecifier"), "SpecifierQualifierList");
        addProduction("SpecifierQualifierList", symbol("TypeSpecifier"));
        addProduction("SpecifierQualifierList", symbol("TypeQualifier"), "SpecifierQualifierList");
        addProduction("SpecifierQualifierList", symbol("TypeQualifier"));
        
        addProduction("StructDeclaratorList", symbol("StructDeclarator"));
        addProduction("StructDeclaratorList", "StructDeclaratorList", symbol("StructDeclarator"));
        
        addProduction("StructDeclarator", symbol("Declarator"));
        addProduction("StructDeclarator", symbol("Declarator"), TokenType.t_colon, "ConstantExpression");
        addProduction("StructDeclarator", TokenType.t_colon, "ConstantExpression");
        
        addProduction("TypeQualifier", TokenType.t_const);
        
        addProduction("Declarator", symbol("Pointer"), symbol("DirectDeclarator"));
        addProduction("Declarator", symbol("DirectDeclarator"));
        
        addProduction("DirectDeclarator", TokenType.t_id);
        addProduction("DirectDeclarator", TokenType.t_lpar, "Declarator", TokenType.t_rpar);
        addProduction("DirectDeclarator", "DirectDeclarator", TokenType.t_lbrac, "ConstantExpression", TokenType.t_rbrac);
        addProduction("DirectDeclarator", "DirectDeclarator", TokenType.t_lbrac, TokenType.t_rbrac);
        addProduction("DirectDeclarator", "DirectDeclarator", TokenType.t_lpar, symbol("ParameterTypeList"), TokenType.t_rpar);
        addProduction("DirectDeclarator", "DirectDeclarator", TokenType.t_lpar, symbol("IdentifierList"), TokenType.t_rpar);
        addProduction("DirectDeclarator", "DirectDeclarator", TokenType.t_lpar, TokenType.t_rpar);
        
        addProduction("Pointer", TokenType.t_star);
        addProduction("Pointer", TokenType.t_star, symbol("TypeQualifierList"));
        addProduction("Pointer", TokenType.t_star, "Pointer");
        addProduction("Pointer", TokenType.t_star, symbol("TypeQualifierList"), "Pointer");
        
        addProduction("TypeQualifierList", "TypeQualifier");
        addProduction("TypeQualifierList", "TypeQualifierList", "TypeQualifier");
        
        addProduction("ParameterTypeList", symbol("ParameterList"));
        addProduction("ParameterTypeList", symbol("ParameterList"), TokenType.t_ellipsis);
    
        addProduction("ParameterList", "ParameterList", symbol("ParameterDeclaration"));
        addProduction("ParameterList", symbol("ParameterDeclaration"));
        
        addProduction("ParameterDeclaration", symbol("DeclarationSpecifiers"), "Declarator");
        addProduction("ParameterDeclaration", symbol("DeclarationSpecifiers"), symbol("AbstractDeclarator"));
        addProduction("ParameterDeclaration", symbol("DeclarationSpecifiers"));
        
        addProduction("IdentifierList", TokenType.t_id);
        addProduction("IdentifierList", "IdentifierList", TokenType.t_comma, TokenType.t_id);
        
        addProduction("TypeName", "SpecifierQualifierList");
        addProduction("TypeName", "SpecifierQualifierList", "AbstractDeclarator");
        
        addProduction("AbstractDeclarator", "Pointer");
        addProduction("AbstractDeclarator", symbol("DirectAbstractDeclarator"));
        addProduction("AbstractDeclarator", "Pointer", symbol("DirectAbstractDeclarator"));
        
        addProduction("DirectAbstractDeclarator", TokenType.t_lpar, "AbstractDeclarator", TokenType.t_rpar);
        addProduction("DirectAbstractDeclarator", TokenType.t_lbrac, "ConstantExpression", TokenType.t_rbrac);
        addProduction("DirectAbstractDeclarator", "DirectAbstractDeclarator", TokenType.t_lbrac, "ConstantExpression", TokenType.t_rbrac);
        addProduction("DirectAbstractDeclarator", TokenType.t_lbrac, TokenType.t_rbrac);
        addProduction("DirectAbstractDeclarator", "DirectAbstractDeclarator", TokenType.t_lbrac, TokenType.t_rbrac);
        addProduction("DirectAbstractDeclarator", TokenType.t_lpar, symbol("ParameterTypeList"), TokenType.t_rpar);
        addProduction("DirectAbstractDeclarator","DirectAbstractDeclarator", TokenType.t_lpar, TokenType.t_rpar);
        addProduction("DirectAbstractDeclarator", "DirectAbstractDeclarator", TokenType.t_lpar,
                symbol("ParameterTypeList"), TokenType.t_rpar);
        
        addProduction("DeclarationSpecifiers", symbol("StorageClassSpecifier"));
        addProduction("DeclarationSpecifiers", symbol("StorageClassSpecifier"), "DeclarationSpecifiers");
        addProduction("DeclarationSpecifiers", symbol("TypeSpecifier"));
        addProduction("DeclarationSpecifiers", symbol("TypeSpecifier"), "DeclarationSpecifiers");
        addProduction("DeclarationSpecifiers", symbol("TypeQualifier"));
        addProduction("DeclarationSpecifiers", symbol("TypeQualifier"), "DeclarationSpecifiers");
        
        addProduction("Declaration", "DeclarationSpecifiers", TokenType.t_semic);
        addProduction("Declaration", "DeclarationSpecifiers", symbol("InitDeclaratorList"), TokenType.t_semic);
        
        addProduction("InitDeclaratorList", symbol("InitDeclarator"));
        addProduction("InitDeclaratorList", symbol("InitDeclaratorList"), TokenType.t_comma, symbol("InitDeclarator"));
        
        addProduction("InitDeclarator", "Declarator");
        addProduction("InitDeclarator", "Declarator", TokenType.t_assign, symbol("Initializer"));
        
        addProduction("Initializer", "AssignmentExpression");
        addProduction("Initializer", TokenType.t_lcurl, symbol("InitializerList"), TokenType.t_rcurl);
        addProduction("Initializer", TokenType.t_lcurl, symbol("InitializerList"), TokenType.t_comma, TokenType.t_rcurl);
        
        addProduction("InitializerList", "Initializer");
        addProduction("InitializerList", "Initializer", TokenType.t_comma, "Initializer");
        
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
        
        
        
        addProduction("CompoundStatement", TokenType.t_lcurl, TokenType.t_rcurl);
        addProduction("CompoundStatement", TokenType.t_lcurl, symbol("StatementList"), TokenType.t_rcurl);
        
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
        
        addProduction("LabeledStatement", TokenType.t_id, TokenType.t_colon, "Statement");
        
        
        addProduction("ExpressionStatement", TokenType.t_semic);
        addProduction("ExpressionStatement", "Expression", TokenType.t_semic);
        
        addProduction("SelectionStatement", TokenType.t_if, TokenType.t_lpar, "Expression", TokenType.t_rpar, "Statement");
        addProduction("SelectionStatement", TokenType.t_if, TokenType.t_lpar, "Expression", TokenType.t_rpar, "Statement", TokenType.t_else, "Statement");
        
        addProduction("IterationStatement", TokenType.t_while, TokenType.t_lpar, "Expression", TokenType.t_rpar, "Statement");
        addProduction("IterationStatement", TokenType.t_do, "Statement", TokenType.t_while, TokenType.t_lpar, "Expression", TokenType.t_rpar, TokenType.t_semic);
        addProduction("IterationStatement", TokenType.t_for, TokenType.t_lpar, "ExpressionStatement", "ExpressionStatement", TokenType.t_rpar,
                "Statement");
        addProduction("IterationStatement", TokenType.t_for, TokenType.t_lpar, "ExpressionStatement", "ExpressionStatement",
                "Expression", TokenType.t_rpar, "Statement");
        
        addProduction("JumpStatement", TokenType.t_return, TokenType.t_semic);
        addProduction("JumpStatement", TokenType.t_return, "Expression", TokenType.t_semic);
        
        
        
        setStartingSymbol(symbol("ExternalDeclaration"));
    }
}
