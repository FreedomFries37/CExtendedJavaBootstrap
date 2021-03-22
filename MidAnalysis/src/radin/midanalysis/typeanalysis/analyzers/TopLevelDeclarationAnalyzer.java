package radin.midanalysis.typeanalysis.analyzers;

import radin.core.semantics.ASTNodeType;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.semantics.types.primitives.PointerType;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.output.tags.ResolvedPathTag;
import radin.output.typeanalysis.IVariableTypeTracker;
import radin.output.typeanalysis.TypeAnalyzer;

import java.util.LinkedList;
import java.util.List;

public class TopLevelDeclarationAnalyzer extends TypeAnalyzer {

    public TopLevelDeclarationAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }

    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode child) {
        if(child.getASTType() == ASTNodeType.using) {
            CXIdentifier id = new CXIdentifier(child.getChild(0).getASTNode());
            if(child.hasASTChild(ASTNodeType.top_level_decs)) {
                getCurrentTracker().useNamespace(id);
                ProgramTypeAnalyzer typeAnalyzer = new ProgramTypeAnalyzer(child.getASTChild(ASTNodeType.top_level_decs));
                if(!determineTypes(typeAnalyzer)) return false;
                getCurrentTracker().stopUseNamespace(id);
            } else if(child.getChildren().size() > 1) {
                TypeAugmentedSemanticNode innerChild = child.getChildren().get(1);
                getCurrentTracker().useNamespace(id);
                if(!determineTypes(innerChild)) return false;
                getCurrentTracker().stopUseNamespace(id);
            } else {
                getCurrentTracker().useNamespace(id);
            }
        } else if(child.getASTType() == ASTNodeType.in_namespace) {
            InNamespaceTypeAnalyzer inNamespaceTypeAnalyzer = new InNamespaceTypeAnalyzer(child);
            if(!determineTypes(inNamespaceTypeAnalyzer)) {
                return false;
            }
            //throw new Error("Identifier Resolution for variables not yet implemented");
        }else if(child.getASTType() == ASTNodeType.class_type_definition) {

            ClassTypeAnalyzer classTypeAnalyzer = new ClassTypeAnalyzer(child);
            if(!determineTypes(classTypeAnalyzer)) {
                setIsFailurePoint(child);
                return false;
            }
        } else if(child.getASTType() == ASTNodeType.function_definition) {

            FunctionTypeAnalyzer functionTypeAnalyzer = new FunctionTypeAnalyzer(child);
            CXIdentifier name = new CXIdentifier(child.getASTChild(ASTNodeType.id).getToken());
            TypedAbstractSyntaxNode astNode =
                    ((TypedAbstractSyntaxNode) child.getASTNode());

            CXType returnType = astNode.getCxType();
            TypeAugmentedSemanticNode astChild = child.getASTChild(ASTNodeType.parameter_list);
            List<CXType> typeList = new LinkedList<>();
            for (TypeAugmentedSemanticNode param : astChild.getChildren()) {
                typeList.add(((TypedAbstractSyntaxNode) param.getASTNode()).getCxType());
            }
            CXFunctionPointer pointer = new CXFunctionPointer(returnType, typeList);
            child.setType(returnType);
            //if(!getCurrentTracker().functionExists(name)) {
            CXIdentifier id = getCurrentTracker().addFunction(name, pointer, true);
            getCurrentTracker().addGlobalVariable(name, pointer);

            // }

            child.getASTChild(ASTNodeType.id).setType(pointer);
            child.getASTChild(ASTNodeType.id).addCompilationTag(new ResolvedPathTag(id));

            if(!determineTypes(functionTypeAnalyzer)) {
                child.printTreeForm();
                return false;
            }



        } else if(child.getASTType() == ASTNodeType.declarations) {
            StatementDeclarationTypeAnalyzer analyzer = new StatementDeclarationTypeAnalyzer(child, IVariableTypeTracker.NameType.GLOBAL);

            if(!determineTypes(analyzer)) return false;

        } else if(child.getASTType() == ASTNodeType.qualifiers_and_specifiers) {

            if(child.getASTNode().getChild(0) instanceof TypedAbstractSyntaxNode) {
                CXType declarationType = ((TypedAbstractSyntaxNode) child.getASTNode().getChild(0)).getCxType();

                if(declarationType instanceof CXCompoundType) {
                    CXCompoundType cxCompoundType = ((CXCompoundType) declarationType);
                    if(!getCurrentTracker().isTracking(cxCompoundType)) {
                        getCurrentTracker().addBasicCompoundType(cxCompoundType);
                        getCurrentTracker().addIsTracking(cxCompoundType);
                    }
                }
            }
        } else if(child.getASTType() == ASTNodeType.typedef) {
            CXType declarationType = ((TypedAbstractSyntaxNode) child.getASTNode()).getCxType();
            if(declarationType instanceof CXCompoundType) {
                CXCompoundType cxCompoundType = ((CXCompoundType) declarationType);
                if(!getCurrentTracker().isTracking(cxCompoundType)) {
                    getCurrentTracker().addBasicCompoundType(cxCompoundType);
                    getCurrentTracker().addIsTracking(cxCompoundType);
                }
            }
            child.setType(declarationType);
        } else if(child.getASTType() == ASTNodeType.top_level_decs) {
            ProgramTypeAnalyzer programTypeAnalyzer = new ProgramTypeAnalyzer(child, false);
            if(!determineTypes(programTypeAnalyzer)) {
                setIsFailurePoint(child);
                return false;
            }
        } else if(child.getASTType() == ASTNodeType.implement) {
            CXClassType subType = (CXClassType) ((PointerType) ((TypedAbstractSyntaxNode) child.getASTNode()).getCxType()).getSubType();
            ImplementationTypeAnalyzer implementationTypeAnalyzer = new ImplementationTypeAnalyzer(child,
                    subType);

            if(!determineTypes(implementationTypeAnalyzer)) {
                setIsFailurePoint(child);
                return false;
            }


        } else if(child.getASTType() == ASTNodeType.generic) {
            GenericTypeAnalyzer genericTypeAnalyzer = new GenericTypeAnalyzer(child);

            if (!determineTypes(genericTypeAnalyzer)) return false;
        }

        return true;
    }
}
