package radin.output.tags;

import radin.core.lexical.Token;
import radin.core.semantics.types.primitives.EnumType;

public class EnumMemberTag extends AbstractCompilationTag{
    
    public final EnumType type;
    public final Token member;
    public EnumMemberTag(EnumType type, Token member) {
        super("ENUM MEMBER {" + type + "." + member + "}");
        this.type = type;
        this.member = member;
    }
}
