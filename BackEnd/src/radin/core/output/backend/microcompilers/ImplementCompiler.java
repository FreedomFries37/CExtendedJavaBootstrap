package radin.core.output.backend.microcompilers;

import radin.core.output.backend.compilation.AbstractIndentedOutputSingleOutputCompiler;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.tags.ImplementMethodTag;
import radin.core.output.tags.PriorConstructorTag;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.methods.CXConstructor;
import radin.core.semantics.types.wrapped.CXMappedType;

import java.io.PrintWriter;

public class ImplementCompiler extends AbstractIndentedOutputSingleOutputCompiler {
    
    private TypeAugmentedSemanticNode block;
    
    public ImplementCompiler(PrintWriter printWriter, int indent, TypeAugmentedSemanticNode block) {
        super(printWriter, indent);
        this.block = block;
    }
    
    @Override
    public boolean compile() {
        CXClassType parentType = ((CXClassType) block.getCXType());
        for (TypeAugmentedSemanticNode child : block.getChildren()) {
            if(child.getASTType() == ASTNodeType.function_definition) {
                ImplementMethodTag compilationTag = child.getCompilationTag(ImplementMethodTag.class);
                FunctionCompiler functionCompiler = new MethodCompiler(getPrintWriter(), getIndent(),
                        compilationTag.getMethod(), child.getASTChild(ASTNodeType.compound_statement));
                if(!functionCompiler.compile()) return false;
            } else if(child.getASTType() == ASTNodeType.constructor_definition) {
                child.printTreeForm();
                ImplementMethodTag compilationTag = child.getCompilationTag(ImplementMethodTag.class);
                CXConstructor cxConstructor = (CXConstructor) compilationTag.getMethod();
                PriorConstructorTag priorConstructorTag = null;
                if(child.containsCompilationTag(PriorConstructorTag.class)) {
                    priorConstructorTag = child.getCompilationTag(PriorConstructorTag.class);
                }
                ConstructorCompiler constructorCompiler = new ConstructorCompiler(getPrintWriter(), cxConstructor, child.getASTChild(ASTNodeType.compound_statement),
                        priorConstructorTag);
                
                if(!constructorCompiler.compile()) return false;
            }
        }
        return true;
    }
}
