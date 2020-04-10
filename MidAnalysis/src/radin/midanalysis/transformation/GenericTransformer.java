package radin.midanalysis.transformation;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.generics.CXGenericFunction;
import radin.midanalysis.GenericModule;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.midanalysis.TypeAugmentedSemanticTree;
import radin.midanalysis.typeanalysis.analyzers.ProgramTypeAnalyzer;
import radin.output.tags.GenericLocationTag;

import java.util.List;

public class GenericTransformer extends TASTTransformer {
    
    public GenericTransformer(GenericModule genericModule) {
        this.genericModule = genericModule;
    }
    
    /**
     * Takes in the top level declarations of a program, and adds the generic implementations to the children
     * @return the altered top level declarations
     */
    @Override
    protected TypeAugmentedSemanticNode transform() {
        TypeAugmentedSemanticNode topLevelsDec = getHead();
    
        
        for (CXGenericFunction registeredGenericFunction : genericModule.getRegisteredGenericFunctions()) {
            GenericLocationTag tag = genericModule.genericLocationTag(registeredGenericFunction);
            
            for (AbstractSyntaxNode createdTree : registeredGenericFunction.getCreatedTrees()) {
                
                TypeAugmentedSemanticNode node = TypeAugmentedSemanticTree.convertAST(createdTree, registeredGenericFunction.getEnvironment());
    
                TypeAugmentedSemanticNode fakeTopLevelDecs = new TypeAugmentedSemanticNode(
                        new AbstractSyntaxNode(ASTNodeType.top_level_decs),
                        node
                );
                
                ProgramTypeAnalyzer typeAnalyzer = new ProgramTypeAnalyzer(fakeTopLevelDecs);
                if(!typeAnalyzer.determineTypes()) throw new Error("Oh shit");
    
                var fromTag = topLevelsDec.findFromTag(tag);
                if(fromTag != null)
                    insertAfter(fromTag, node);
            }
        }
        
        return topLevelsDec;
    }
}
