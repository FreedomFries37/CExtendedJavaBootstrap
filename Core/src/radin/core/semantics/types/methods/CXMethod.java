package radin.core.semantics.types.methods;

import radin.core.lexical.Token;
import radin.core.lexical.TokenType;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.*;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.core.semantics.types.primitives.PointerType;
import radin.core.utility.UniversalCompilerSettings;


import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CXMethod implements CXEquivalent {
    
    private static String methodThisParameterName = "__this";
    
    private CXClassType parent;
    
    private Visibility visibility;
    private boolean isVirtual;
    private CXType returnType;
    private CXIdentifier name;
    private List<CXParameter> parameters;
    private AbstractSyntaxNode methodBody;
    
   
    
    public CXMethod(CXClassType parent, Visibility visibility, Token name, boolean isVirtual, CXType returnType,
                    List<CXParameter> parameters,
                    AbstractSyntaxNode methodBody) {
        this.parent = parent;
        this.visibility = visibility;
        this.name = new CXIdentifier(name, false);
        this.isVirtual = isVirtual;
        this.returnType = returnType;
        this.parameters = parameters;
        
        
        this.methodBody = methodBody;
    }
    
    
    
    public void setParent(CXClassType parent) {
        this.parent = parent;
    }
    
    public void setIdentifier(CXIdentifier name) {
        this.name = name;
    }
    
    public CXClassType getParent() {
        return parent;
    }
    
    public Visibility getVisibility() {
        return visibility;
    }
    
    public String getIdentifierName() {
        return name.getIdentifierString();
    }
    
    public CXIdentifier getName() {
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
    public List<CXParameter> getParametersExpanded() {
        LinkedList<CXParameter> cxParameters = new LinkedList<>(getParameters());
        cxParameters.add(0, new CXParameter(new PointerType(CXPrimitiveType.VOID), methodThisParameterName));
        return cxParameters;
    }
    
    public AbstractSyntaxNode getMethodBody() {
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
        output.append(returnType.generateCDeclaration(getCFunctionName()));
        output.append('(');
        if(getParametersExpanded().size() > 0) {
            boolean first = true;
            for (CXParameter parameter : getParametersExpanded()) {
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
        int hash = getParameterMangle().hashCode();
        if(parent != null)
            hash += parent.getTypeNameIdentifier().hashCode();
        hash += name.hashCode();
        hash = Math.abs(hash);
        String prefix = name.generateCDefinitionNoHash();
        
        return prefix + hash;
    }
    
    public String getCMethodName() {
        
        int hash = getParameterMangle().hashCode();
        hash += name.getIdentifierString().hashCode();
        hash = Math.abs(hash);
        String prefix = name.getIdentifierString();
        return prefix + hash;
        
        
    }
    
    public String methodCall(String thisValue, String sequence) {
        return getCMethodName() + "(" + thisValue + ", " + sequence + ")";
    }
    
    public String methodCall(String thisValue) {
        return getCMethodName() + "(" + thisValue + ")";
    }
    
    public String methodAsFunctionCall(String thisValue) {
        return getCFunctionName() + "(" + thisValue + ")";
    }
    
    public String methodAsFunctionCall(String thisValue, String sequence) {
        return getCFunctionName() + "(" + thisValue + ", " + sequence + ")";
    }
    
    
    
    
    @Override
    public String generateCDefinition() {
        StringBuilder output = new StringBuilder();
        output.append(returnType.generateCDeclaration(getCFunctionName()));
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
    
    public CXMethod createSuperMethod(CXClassType child_class, String vtablename, CXMethod replacement) {
        String replacementName = replacement.getCMethodName();
        
        Token name = variableAST("super_" + this.getCFunctionName()).getToken();
        
        AbstractSyntaxNode oldDec = new AbstractSyntaxNode(ASTNodeType.declarations,
                new TypedAbstractSyntaxNode(
                        ASTNodeType.declaration,
                        replacement.getFunctionPointer(),
                        new AbstractSyntaxNode(
                                ASTNodeType.id,
                                new Token(TokenType.t_id, "old")
                        )
                ));
        AbstractSyntaxNode fieldGet = new AbstractSyntaxNode(ASTNodeType.field_get,
                new AbstractSyntaxNode(ASTNodeType.indirection,
                        new AbstractSyntaxNode(ASTNodeType.field_get,
                                new AbstractSyntaxNode(ASTNodeType.indirection,
                                        thisAST()
                                ),
                                variableAST(UniversalCompilerSettings.getInstance().getSettings().getvTableName())
                        )
                ),
                variableAST(replacementName)
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
        
        List<AbstractSyntaxNode> superMethodCallParameters = new LinkedList<>();
        for (CXParameter parameter : getParametersExpanded()) {
            superMethodCallParameters.add(variableAST(parameter.getName()));
        }
        AbstractSyntaxNode sequenceNode = new AbstractSyntaxNode(ASTNodeType.sequence, superMethodCallParameters);
        AbstractSyntaxNode superMethodCall = new AbstractSyntaxNode(
                ASTNodeType.method_call,
                fieldGet,
                variableAST(vtablename),
                sequenceNode
        );
        AbstractSyntaxNode returnFunction = new AbstractSyntaxNode(
                ASTNodeType.assignment,
                fieldGet,
                new AbstractSyntaxNode(ASTNodeType.assignment_type, new Token(TokenType.t_assign)),
                variableAST("old")
        );
        if(returnType != CXPrimitiveType.VOID) {
            AbstractSyntaxNode output =new AbstractSyntaxNode(ASTNodeType.declarations,
                    new TypedAbstractSyntaxNode(
                            ASTNodeType.declaration,
                            returnType,
                            variableAST("output")
                    )
            );
            AbstractSyntaxNode saveOutput = new AbstractSyntaxNode(
                    ASTNodeType.assignment,
                    variableAST("output"),
                    new AbstractSyntaxNode(ASTNodeType.assignment_type, new Token(TokenType.t_assign)),
                    superMethodCall
            );
            
            AbstractSyntaxNode returnOutput = new AbstractSyntaxNode(
                    ASTNodeType._return,
                    variableAST("output")
            );
            AbstractSyntaxNode compound = new AbstractSyntaxNode(ASTNodeType.compound_statement, oldDec, saveOld,
                    reassign, output, saveOutput,
                    returnFunction, returnOutput);
            
            
            return new CXMethod(child_class, Visibility._private, name, false, returnType, parameters, compound);
        } else {
            
            AbstractSyntaxNode compound = new AbstractSyntaxNode(ASTNodeType.compound_statement, oldDec, saveOld,
                    reassign, superMethodCall,
                    returnFunction);
            
            return new CXMethod(child_class, Visibility._private, name, false, returnType, parameters, compound);
        }
    }
    
    private AbstractSyntaxNode thisAST() {
        return new AbstractSyntaxNode(
                ASTNodeType.id,
                new Token(TokenType.t_id, "this")
        );
    }
    
    public static AbstractSyntaxNode variableAST(String id) {
        return new AbstractSyntaxNode(
                ASTNodeType.id,
                new Token(TokenType.t_id, id)
        );
    }
    
    @Override
    public String toString() {
        return returnType.toString() + " " + name + " (" +
                getParameters().stream().map(CXParameter::getType).map(CXType::toString).collect(Collectors.joining("," +
                        " "))
                + ")";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CXMethod cxMethod = (CXMethod) o;
        boolean equals = returnType.equals(cxMethod.returnType);
        return isVirtual == cxMethod.isVirtual &&
                Objects.equals(parent, cxMethod.parent) &&
                visibility == cxMethod.visibility &&
                equals &&
                name.equals(cxMethod.name) &&
                parameters.equals(cxMethod.parameters);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(parent, visibility, isVirtual, returnType, name, parameters);
    }
}
