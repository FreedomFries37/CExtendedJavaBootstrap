package radin.interphase.semantics.types.methods;

import radin.interphase.lexical.Token;
import radin.interphase.lexical.TokenType;
import radin.interphase.semantics.ASTNodeType;
import radin.interphase.semantics.AbstractSyntaxNode;
import radin.interphase.semantics.types.*;
import radin.interphase.semantics.types.compound.CXClassType;
import radin.interphase.semantics.types.compound.FunctionPointer;
import radin.interphase.semantics.types.primitives.CXPrimitiveType;


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
        this.parameters.add(
                new CXParameter(
                        new PointerType(CXPrimitiveType.VOID),
                        "__this"
                )
        );
        this.methodBody = methodBody;
        fixedMethodBody = false;
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
    
    public List<CXParameter> getParameters() {
        return parameters;
    }
    
    public AbstractSyntaxNode getMethodBody() {
        if(!fixedMethodBody) {
            fixMethodBody();
        }
        return methodBody;
    }
    
    public FunctionPointer getFunctionPointer() {
        return new FunctionPointer(returnType, getParameterTypes());
    }
    
    public PointerType getThisType() {
        CompoundTypeReference compoundTypeReference =
                new CompoundTypeReference(CompoundTypeReference.CompoundType._class, parent);
        return new PointerType(compoundTypeReference);
    }
    
    @Override
    public String generateCDeclaration() {
        StringBuilder output = new StringBuilder();
        output.append(returnType.generateCDefinition(name));
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
    
    @Override
    public String generateCDefinition() {
        StringBuilder output = new StringBuilder();
        output.append(returnType.generateCDefinition(name));
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
    
    protected void fixMethodBody() {
        AbstractSyntaxNode define = new AbstractSyntaxNode(
                ASTNodeType.declaration,
                new AbstractSyntaxNode(
                        ASTNodeType.typename,
                        getThisType().getTokenEquivalent()
                ),
                new AbstractSyntaxNode(
                        ASTNodeType.id,
                        new Token(TokenType.t_id, "this")
                )
        );
        AbstractSyntaxNode cast = new AbstractSyntaxNode(
                ASTNodeType.assignment,
                new AbstractSyntaxNode(
                        ASTNodeType.id,
                        new Token(TokenType.t_id, "this")
                ),
                new AbstractSyntaxNode(
                        ASTNodeType.cast,
                        new AbstractSyntaxNode(
                                ASTNodeType.typename,
                                getThisType().getTokenEquivalent()
                        ),
                        new AbstractSyntaxNode(
                                ASTNodeType.id,
                                new Token(TokenType.t_id, "__this")
                        )
                )
        );
        this.methodBody = new AbstractSyntaxNode(this.methodBody, true, define, cast);
        fixedMethodBody = true;
    }
}
