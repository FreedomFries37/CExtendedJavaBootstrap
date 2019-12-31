package radin.interphase.semantics.types.methods;

import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.types.*;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.compound.CXFunctionPointer;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;
import radin.interphase.semantics.types.wrapped.PointerType;


import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CXMethod implements CXEquivalent {
    
    private static String methodThisParameterName = "__this";
    
    private CXClassType parent;
    
    private Visibility visibility;
    private boolean isVirtual;
    private CXType returnType;
    private String name;
    private List<CXParameter> parameters;
    private AbstractSyntaxNode methodBody;
    
    private boolean fixedMethodBody;
    
    public CXMethod(CXClassType parent, Visibility visibility, String name, boolean isVirtual, CXType returnType, List<CXParameter> parameters,
                    AbstractSyntaxNode methodBody) {
        this.parent = parent;
        this.visibility = visibility;
        this.name = name;
        this.isVirtual = isVirtual;
        this.returnType = returnType;
        this.parameters = parameters;
        
        
        this.methodBody = methodBody;
        fixedMethodBody = false;
    }
    
    public void setParent(CXClassType parent) {
        this.parent = parent;
    }
    
    public CXClassType getParent() {
        return parent;
    }
    
    public Visibility getVisibility() {
        return visibility;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isVirtual() {
        return isVirtual;
    }
    
    public CXType getReturnType() {
        return returnType;
    }
    
    public List<CXType> getParameterTypes() {
        List<CXType> output = new LinkedList<>();
        for (CXParameter parameterType : parameters) {
            output.add(parameterType.getType());
        }
        return output;
    }
    
    public ParameterTypeList getParameterTypeList() {
        return new ParameterTypeList(getParameterTypes());
    }
    
    public List<CXParameter> getParameters() {
        return parameters;
    }
    
    public AbstractSyntaxNode getMethodBody() {
        if(!fixedMethodBody) {
            fixMethodBody();
        }
        return methodBody;
    }
    
    public CXFunctionPointer getFunctionPointer() {
        List<CXType> parameterTypes = new LinkedList<>(getParameterTypes());
        parameterTypes.add(0, new PointerType(CXPrimitiveType.VOID));
        return new CXFunctionPointer(returnType, parameterTypes);
    }
    
    public PointerType getThisType() {
        CXCompoundTypeNameIndirection cxCompoundTypeNameIndirection =
                new CXCompoundTypeNameIndirection(CXCompoundTypeNameIndirection.CompoundType._class, parent);
        return new PointerType(cxCompoundTypeNameIndirection);
    }
    
    public PointerType getSuperType() {
        CXCompoundTypeNameIndirection cxCompoundTypeNameIndirection =
                new CXCompoundTypeNameIndirection(CXCompoundTypeNameIndirection.CompoundType._class, parent.getParent());
        return new PointerType(cxCompoundTypeNameIndirection);
    }
    
    @Override
    public String generateCDeclaration() {
        StringBuilder output = new StringBuilder();
        output.append(returnType.generateCDefinition(getCFunctionName()));
        output.append('(');
        if(parameters.size() > 0) {
            boolean first = true;
            for (CXParameter parameter : parameters) {
                if (first) first = false;
                else output.append(", ");
                
                output.append(parameter.getType().generateCDefinition());
            }
            
        }else {
            output.append("void");
        }
        output.append(");");
        return output.toString();
    }
    
    
    
    public String getCFunctionName() {
        
        return parent.getCTypeName() + "_" + name + "_" + getParameterMangle();
    }
    
    public String getCMethodName() {
        return  name + "_" + getParameterMangle();
    }
    
    public String methodCall(String thisValue, String sequence) {
        return getCMethodName() + "(" + thisValue + "," + sequence + ")";
    }
    
    public String methodCall(String thisValue) {
        return getCMethodName() + "(" + thisValue + ")";
    }
    
    public String methodAsFunctionCall(String thisValue) {
        return getCFunctionName() + "(" + thisValue + ")";
    }
    
    public String methodAsFunctionCall(String thisValue, String sequence) {
        return getCFunctionName() + "(" + thisValue + "," + sequence + ")";
    }
    
    
    
    
    @Override
    public String generateCDefinition() {
        StringBuilder output = new StringBuilder();
        output.append(returnType.generateCDefinition(getCFunctionName()));
        output.append('(');
        boolean first = true;
        for (CXParameter parameter : parameters) {
            if(first) first = false;
            else output.append(", ");
            
            output.append(parameter.toString());
        }
        
        output.append(')');
        // TODO: generate blocks
        
        return output.toString();
    }
    
    public String getParameterMangle() {
        List<CXParameter> parameters = new LinkedList<>(this.parameters);
        parameters.add(0, new CXParameter(new PointerType(CXPrimitiveType.VOID), methodThisParameterName));
        return getParameterMangle(parameters);
    }
    
    public static String getParameterMangle(List<CXParameter> parameters) {
        return parameters.stream().map(
                (CXParameter c) -> c.getType().generateCDefinition()
                        .replace(" ", "")
                        .replace("(", "L")
                        .replace(")", "")
                        .replace("[","R")
                        .replace("]", "")
                        .replace("*", "p")).collect(Collectors.joining());
    }
    
    public CXMethod createSuperMethod(CXClassType child_class, String vtablename) {
        
        
        String name = "super_" + this.getName();
        
        AbstractSyntaxNode oldDec = new AbstractSyntaxNode(ASTNodeType.declarations,
                new TypeAbstractSyntaxNode(
                        ASTNodeType.declaration,
                        getFunctionPointer(),
                        new AbstractSyntaxNode(
                                ASTNodeType.id,
                                new Token(TokenType.t_id, "old")
                        )
                ));
        AbstractSyntaxNode fieldGet = new AbstractSyntaxNode(ASTNodeType.field_get,
                new AbstractSyntaxNode(ASTNodeType.field_get,
                        new AbstractSyntaxNode(ASTNodeType.indirection,
                                thisAST()
                        ),
                        variableAST("vtable")
                ),
                variableAST(getName())
        );
        AbstractSyntaxNode saveOld = new AbstractSyntaxNode(
                ASTNodeType.assignment,
                new AbstractSyntaxNode(
                        ASTNodeType.id,
                        new Token(TokenType.t_id, "old")
                ),
                new AbstractSyntaxNode(ASTNodeType.assignment_type, new Token(TokenType.t_assign)),
                fieldGet
        );
        AbstractSyntaxNode reassign = new AbstractSyntaxNode(
                ASTNodeType.assignment,
                fieldGet,
                new AbstractSyntaxNode(ASTNodeType.assignment_type, new Token(TokenType.t_assign)),
                variableAST(getCFunctionName())
        );
        AbstractSyntaxNode output =new AbstractSyntaxNode(ASTNodeType.declarations,
                new TypeAbstractSyntaxNode(
                        ASTNodeType.declaration,
                        returnType,
                        variableAST("output")
                )
        );
        List<AbstractSyntaxNode> superMethodCallParameters = new LinkedList<>();
        for (CXParameter parameter : parameters) {
            superMethodCallParameters.add(variableAST(parameter.getName()));
        }
        AbstractSyntaxNode sequenceNode = new AbstractSyntaxNode(ASTNodeType.sequence, superMethodCallParameters);
        AbstractSyntaxNode superMethodCall = new AbstractSyntaxNode(
                ASTNodeType.method_call,
                fieldGet,
                variableAST(vtablename),
                sequenceNode
        );
        AbstractSyntaxNode saveOutput = new AbstractSyntaxNode(
                ASTNodeType.assignment,
                variableAST("output"),
                new AbstractSyntaxNode(ASTNodeType.assignment_type, new Token(TokenType.t_assign)),
                superMethodCall
        );
        AbstractSyntaxNode returnFunction = new AbstractSyntaxNode(
                ASTNodeType.assignment,
                fieldGet,
                new AbstractSyntaxNode(ASTNodeType.assignment_type, new Token(TokenType.t_assign)),
                variableAST("old")
        );
        AbstractSyntaxNode returnOutput = new AbstractSyntaxNode(
                ASTNodeType._return,
                variableAST("old")
        );
        AbstractSyntaxNode compound = new AbstractSyntaxNode(ASTNodeType.compound_statement, oldDec, saveOld,
                reassign, output, saveOutput,
                returnFunction, returnOutput);
        
        return new CXMethod(child_class, Visibility._private, name, false, returnType, parameters, compound);
    }
    
    private AbstractSyntaxNode thisAST() {
        return new AbstractSyntaxNode(
                ASTNodeType.id,
                new Token(TokenType.t_id, "this")
        );
    }
    
    private AbstractSyntaxNode variableAST(String id) {
        return new AbstractSyntaxNode(
                ASTNodeType.id,
                new Token(TokenType.t_id, id)
        );
    }
    
    protected void fixMethodBody() {
        AbstractSyntaxNode define = new AbstractSyntaxNode(ASTNodeType.declarations,
                new TypeAbstractSyntaxNode(
                        ASTNodeType.declaration,
                        new PointerType(parent),
                        new AbstractSyntaxNode(
                                ASTNodeType.id,
                                new Token(TokenType.t_id, "this")
                        )
                )
        );
        AbstractSyntaxNode cast = new AbstractSyntaxNode(
                ASTNodeType.assignment,
                new AbstractSyntaxNode(
                        ASTNodeType.id,
                        new Token(TokenType.t_id, "this")
                ),
                new AbstractSyntaxNode(ASTNodeType.assignment_type, new Token(TokenType.t_assign)),
                new TypeAbstractSyntaxNode(
                        ASTNodeType.cast,
                        new PointerType(parent),
                        new AbstractSyntaxNode(
                                ASTNodeType.id,
                                new Token(TokenType.t_id, "__this")
                        )
                )
        );
        if(parent.getParent() != null) {
        
            AbstractSyntaxNode defineS = new AbstractSyntaxNode(ASTNodeType.declarations,
                    new TypeAbstractSyntaxNode(
                            ASTNodeType.declaration,
                            new PointerType(parent),
                            variableAST("super")
                    )
            );
            AbstractSyntaxNode assignS = new AbstractSyntaxNode(
                    ASTNodeType.assignment,
                    variableAST("super"),
                    new AbstractSyntaxNode(ASTNodeType.assignment_type, new Token(TokenType.t_assign)),
                    variableAST("this")
            );
            this.methodBody = new AbstractSyntaxNode(this.methodBody, true,define, cast, defineS, assignS);
        }
        else this.methodBody = new AbstractSyntaxNode(this.methodBody, true, define, cast);
        
        
        
        
        
        fixedMethodBody = true;
    }
}
