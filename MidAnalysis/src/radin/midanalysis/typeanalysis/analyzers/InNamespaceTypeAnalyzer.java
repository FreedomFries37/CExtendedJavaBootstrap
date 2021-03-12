package radin.midanalysis.typeanalysis.analyzers;

import radin.core.lexical.Token;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXIdentifier;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXCompoundType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.semantics.types.primitives.PointerType;
import radin.midanalysis.TypeAugmentedSemanticNode;
import radin.output.typeanalysis.TypeAnalyzer;

import java.util.LinkedList;
import java.util.List;

public class InNamespaceTypeAnalyzer extends TypeAnalyzer {
    public InNamespaceTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }

    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        AbstractSyntaxNode identityNode = node.getASTChild(ASTNodeType.id).getASTNode();
        Token token = identityNode.getToken();
        CXIdentifier namespace = new CXIdentifier(token);

        getCurrentTracker().enterNamespace(namespace);
        TypeAugmentedSemanticNode within = node.getChild(1);
        switch (within.getASTType()) {
            case top_level_decs: {
                ProgramTypeAnalyzer programTypeAnalyzer = new ProgramTypeAnalyzer(within);
                if (!determineTypes(programTypeAnalyzer)) return false;
                break;
            }

            case typedef: {
                node.setType(((TypedAbstractSyntaxNode) node.getASTNode()).getCxType());
                break;
            }
            case class_type_definition: {
                ClassTypeAnalyzer classTypeAnalyzer = new ClassTypeAnalyzer(within);
                if(!determineTypes(classTypeAnalyzer)) {
                    setIsFailurePoint(within);
                    return false;
                }
                break;
            }
            case in_namespace:
            {
                if (!determineTypes(within)) return false;
                break;
            }
            case function_definition: {
                FunctionTypeAnalyzer functionTypeAnalyzer = new FunctionTypeAnalyzer(within);
                if(!determineTypes(functionTypeAnalyzer)) {
                    node.printTreeForm();
                    return false;
                }

                TypedAbstractSyntaxNode astNode =
                        ((TypedAbstractSyntaxNode) within.getASTNode());

                CXIdentifier name = functionTypeAnalyzer.getName();
                CXType returnType = astNode.getCxType();
                node.setType(returnType);
                if(!getCurrentTracker().functionExists(name)) {
                    getCurrentTracker().addFunction(name, returnType, true);

                    TypeAugmentedSemanticNode astChild = within.getASTChild(ASTNodeType.parameter_list);
                    List<CXType> typeList = new LinkedList<>();
                    for (TypeAugmentedSemanticNode param : astChild.getChildren()) {
                        typeList.add(((TypedAbstractSyntaxNode) param.getASTNode()).getCxType());
                    }
                    CXFunctionPointer pointer = new CXFunctionPointer(returnType, typeList);


                    getCurrentTracker().addLocalVariable(name, pointer);
                    within.getASTChild(ASTNodeType.id).setType(pointer);
                }
                break;
            }
            case declarations: {
                StatementDeclarationTypeAnalyzer analyzer = new StatementDeclarationTypeAnalyzer(within);
                if(!determineTypes(analyzer)) return false;
                break;
            }
            case implement: {
                CXClassType subType = (CXClassType) ((PointerType) ((TypedAbstractSyntaxNode) within.getASTNode()).getCxType()).getSubType();
                ImplementationTypeAnalyzer implementationTypeAnalyzer = new ImplementationTypeAnalyzer(within,
                        subType);

                if(!determineTypes(implementationTypeAnalyzer)) {
                    setIsFailurePoint(within);
                    return false;
                }
                break;
            }
            case qualifiers_and_specifiers: {
                if(within.getASTNode().getChild(0) instanceof TypedAbstractSyntaxNode) {
                    CXType declarationType = ((TypedAbstractSyntaxNode) within.getASTNode().getChild(0)).getCxType();

                    if(declarationType instanceof CXCompoundType) {
                        CXCompoundType cxCompoundType = ((CXCompoundType) declarationType);
                        if(!getCurrentTracker().isTracking(cxCompoundType)) {
                            getCurrentTracker().addBasicCompoundType(cxCompoundType);
                            getCurrentTracker().addIsTracking(cxCompoundType);
                        }
                    }
                }
                break;
            }
            case generic:
            default:
                throw new IllegalStateException("Unexpected value: " + within.getASTType());
        }
        getCurrentTracker().exitNamespace();
        return true;
    }
}
