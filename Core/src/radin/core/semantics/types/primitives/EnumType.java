package radin.core.semantics.types.primitives;

import radin.core.lexical.Token;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.ICXWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnumType extends AbstractCXPrimitiveType{
    
    private final List<Token> members;
    private final CXIdentifier name;
    
    public EnumType(CXIdentifier name, List<Token> tokens) {
        this.name = name;
        members = new ArrayList<>(tokens);
    }
    
    @Override
    public String generateCDefinition() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("enum { ");
        stringBuilder.append(members.stream().map(Token::getImage).collect(Collectors.joining(", ")));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
    
    @Override
    public String generateCDeclaration(String identifier) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("enum %s { ", identifier));
        stringBuilder.append(members.stream().map(Token::getImage).collect(Collectors.joining(", ")));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
    
    @Override
    public boolean isValid(TypeEnvironment e) {
        return true;
    }
    
    @Override
    public long getDataSize(TypeEnvironment e) {
        return (long) (Math.log(members.size()) / Math.log(2)) / 8;
    }
    
    @Override
    public boolean is(CXType other, TypeEnvironment e, boolean strictPrimitiveEquality) {
        if(other instanceof ICXWrapper) return this.is(((ICXWrapper) other).getWrappedType(), e, strictPrimitiveEquality);
        return this == other;
    }
    
    @Override
    public boolean isIntegral() {
        return false;
    }
    
    public List<Token> getMembers() {
        return members;
    }
    
    @Override
    public String toString() {
        return "enum " + name;
    }
    
    @Override
    public CXIdentifier getIdentifier() {
        return name;
    }
}
