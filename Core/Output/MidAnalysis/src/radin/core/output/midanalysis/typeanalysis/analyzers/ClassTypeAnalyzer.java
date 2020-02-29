package radin.core.output.midanalysis.typeanalysis.analyzers;

import radin.core.lexical.Token;
import radin.core.output.midanalysis.MethodTASNTracker;
import radin.core.semantics.types.TypedAbstractSyntaxNode;
import radin.core.semantics.types.methods.ParameterTypeList;
import radin.core.output.midanalysis.TypeAugmentedSemanticTree;
import radin.core.output.typeanalysis.errors.IncorrectReturnTypeError;
import radin.core.semantics.ASTNodeType;
import radin.core.semantics.AbstractSyntaxNode;
import radin.core.semantics.types.CXType;
import radin.core.semantics.types.primitives.PointerType;
import radin.core.semantics.types.Visibility;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.semantics.types.compound.CXFunctionPointer;
import radin.core.semantics.types.compound.CXStructType;
import radin.core.semantics.types.methods.CXMethod;
import radin.core.semantics.types.methods.CXParameter;
import radin.core.semantics.types.primitives.CXPrimitiveType;
import radin.core.output.typeanalysis.TypeAnalyzer;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.output.typeanalysis.errors.RedeclarationError;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;

import java.util.LinkedList;
import java.util.List;

import static radin.core.semantics.types.Visibility.*;

public class ClassTypeAnalyzer extends TypeAnalyzer {
    
    public ClassTypeAnalyzer(TypeAugmentedSemanticNode tree) {
        super(tree);
    }
    
    @Override
    public boolean determineTypes(TypeAugmentedSemanticNode node) {
        assert node.getASTNode() instanceof TypedAbstractSyntaxNode;
        assert ((TypedAbstractSyntaxNode) node.getASTNode()).getCxType() instanceof CXClassType;
        
        CXClassType cxClassType = (CXClassType) ((TypedAbstractSyntaxNode) node.getASTNode()).getCxType();
        cxClassType.generateSuperMethods(UniversalCompilerSettings.getInstance().getSettings().getvTableName());
        
        for (CXMethod generatedSuper : cxClassType.getGeneratedSupers()) {
            typeTrackingClosure();
            
            CXStructType vTable = cxClassType.getVTable();
            getCurrentTracker().addBasicCompoundType(vTable);
            getCurrentTracker().addPrivateField(cxClassType, getCompilationSettings().getvTableName(),
                    new PointerType(vTable));
            
            getCurrentTracker().addVariable("__this", new PointerType(CXPrimitiveType.VOID));
            
            
            
            for (CXParameter parameter : generatedSuper.getParameters()) {
                getCurrentTracker().addVariable(parameter.getName(), parameter.getType());
            }
            TypeAugmentedSemanticNode tree = new TypeAugmentedSemanticTree(generatedSuper.getMethodBody(),
                    getEnvironment()).getHead();
            CompoundStatementTypeAnalyzer compoundStatementTypeAnalyzer = new CompoundStatementTypeAnalyzer(tree,
                    generatedSuper.getReturnType(), false);
            //tree.printTreeForm();
            /* // TODO: implement type checking for super methods. Currently have to be non type checked
            if(!determineTypes(compoundStatementTypeAnalyzer)) {
                tree.printTreeForm();
                return false;
            }
            
             */
            getMethods().put(generatedSuper, tree);
            releaseTrackingClosure();
        }
        
        typeTrackingClosure(cxClassType);
        
        
        
        List<TypeAugmentedSemanticNode> decs = node.getAllChildren(ASTNodeType.class_level_declaration);
        // STEP 1 -> set up fields and methods in tracker
        for (TypeAugmentedSemanticNode clsLevelDec : decs) {
            
            
            Visibility visibility =
                    getVisibility(clsLevelDec.getASTChild(ASTNodeType.visibility).getToken());
            
            if(clsLevelDec.hasASTChild(ASTNodeType.declarations)) {
                
                TypeAugmentedSemanticNode fields = clsLevelDec.getASTChild(ASTNodeType.declarations);
                DeclarationsAnalyzer fieldAnalyzers = new DeclarationsAnalyzer(
                        fields,
                        ASTNodeType.declaration,
                        cxClassType,
                        visibility
                );
                
                if(!determineTypes(fieldAnalyzers)) return false;
                
                
                
                
            } else if(clsLevelDec.hasASTChild(ASTNodeType.function_definition) || clsLevelDec.hasASTChild(ASTNodeType.function_description)) {
                ASTNodeType astType = clsLevelDec.getASTChild(ASTNodeType.function_definition) == null ?
                        ASTNodeType.function_description : ASTNodeType.function_definition;
                
                TypedAbstractSyntaxNode astNode =
                        ((TypedAbstractSyntaxNode) clsLevelDec.getASTChild(astType).getASTNode());
                
                CXType returnType = astNode.getCxType();
                Token name = astNode.getChild(ASTNodeType.id).getToken();
                List<CXType> parameterTypes = new LinkedList<>();
                /*
                for (AbstractSyntaxNode abstractSyntaxNode : astNode.getChild(ASTNodeType.parameter_list)) {
                    assert  abstractSyntaxNode instanceof TypeAbstractSyntaxNode;
                    CXType paramType = ((TypeAbstractSyntaxNode) abstractSyntaxNode).getCxType().getTypeRedirection(getEnvironment());
                    parameterTypes.add(paramType);
                }
               
                 */
                for (TypeAugmentedSemanticNode child :
                        clsLevelDec.getASTChild(astType).getASTChild(ASTNodeType.parameter_list).getChildren()) {
                    CXType paramType = ((TypedAbstractSyntaxNode) child.getASTNode()).getCxType();
                    parameterTypes.add(paramType);
                    child.setType(paramType);
                }
                
                
                
                CXFunctionPointer type = new CXFunctionPointer(returnType,
                        parameterTypes);
                clsLevelDec.getASTChild(astType).setType(type);
                
                boolean isVirtual  = astNode.hasChild(ASTNodeType._virtual);
                ParameterTypeList parameterTypeList = type.getParameterTypeList();
                
                if(isVirtual && getCurrentTracker().methodVisible(cxClassType, name.getImage(), parameterTypeList)) {
                    CXType virtType = getCurrentTracker().getMethodType(cxClassType, name.getImage(), parameterTypeList);
                    if(!is(returnType, virtType)) {
                        
                        throw new IncorrectReturnTypeError(virtType, returnType);
                    }
                } else {
                    
                    
                    assert visibility != null;
                    ICompilationSettings.debugLog.finest("Added " + visibility.toString() + " " + returnType.generateCDeclaration(name.getImage()) + parameterTypeList);
                    switch (visibility) {
                        case _public: {
                            
                            getCurrentTracker().addPublicMethod(cxClassType, name.getImage(), returnType,
                                    parameterTypeList);
                            break;
                        }
                        case internal: {
                            getCurrentTracker().addInternalMethod(cxClassType, name.getImage(), returnType, parameterTypeList);
                            break;
                        }
                        case _private: {
                            getCurrentTracker().addPrivateMethod(cxClassType, name.getImage(), returnType, parameterTypeList);
                            break;
                        }
                    }
                }
                if(astType == ASTNodeType.function_definition) {
                    CXMethod method = cxClassType.getMethod(name, parameterTypeList, null);
                    MethodTASNTracker.getInstance().add(method,
                            clsLevelDec.getASTChild(ASTNodeType.function_definition)
                                    .getASTChild(ASTNodeType.compound_statement));
                }
            } else if(clsLevelDec.hasASTChild(ASTNodeType.constructor_definition)) {
                TypeAugmentedSemanticNode def = clsLevelDec.getASTChild(ASTNodeType.constructor_definition);
                
                List<CXType> parameterTypes = new LinkedList<>();
                for (AbstractSyntaxNode abstractSyntaxNode :
                        def.getASTChild(ASTNodeType.parameter_list).getASTNode().getChildList()) {
                    assert  abstractSyntaxNode instanceof TypedAbstractSyntaxNode;
                    CXType paramType = ((TypedAbstractSyntaxNode) abstractSyntaxNode).getCxType().getTypeRedirection(getEnvironment());
                    parameterTypes.add(paramType);
                }
                
                ParameterTypeList typeList = new ParameterTypeList(parameterTypes);
                
                if(getCurrentTracker().constructorVisible(cxClassType, typeList)) {
                    throw new RedeclarationError("constructor for " + cxClassType+ " with parameters" + parameterTypes);
                }
                
                getCurrentTracker().addConstructor(visibility, cxClassType, typeList);
                
            }
        }
        
        //STEP 2 -> analyze functions and constructors
        List<TypeAugmentedSemanticNode> functions = node.getAllChildren(ASTNodeType.function_definition);
        for (TypeAugmentedSemanticNode function : functions) {
            FunctionTypeAnalyzer analyzer = new FunctionTypeAnalyzer(function, cxClassType);
            
            if(!determineTypes(analyzer)) return false;
            
        }
        
        List<TypeAugmentedSemanticNode> constructors = node.getAllChildren(ASTNodeType.constructor_definition);
        for (TypeAugmentedSemanticNode constructor : constructors) {
            ConstructorTypeAnalyzer analyzer = new ConstructorTypeAnalyzer(constructor, cxClassType);
            
            if(!determineTypes(analyzer)) return false;
        }
        
        for (CXMethod generatedSuper : cxClassType.getGeneratedSupers()) {
            ICompilationSettings.debugLog.info("Generated Super: " + generatedSuper.toString());
            AbstractSyntaxNode methodBody = generatedSuper.getMethodBody();
            TypeAugmentedSemanticNode tree =
                    new TypeAugmentedSemanticTree(methodBody, cxClassType.getEnvironment()).getHead();
            typeTrackingClosure();
            getCurrentTracker().addVariable("this", cxClassType.toPointer());
            
    
            for (CXParameter cxParameter : generatedSuper.getParametersExpanded()) {
                getCurrentTracker().addVariable(cxParameter.getName(), cxParameter.getType());
            }
    
            CXStructType vTable = cxClassType.getVTable();
            if(!getCurrentTracker().isTracking(vTable)) {
                getCurrentTracker().addBasicCompoundType(vTable);
                getCurrentTracker().addIsTracking(vTable);
            }
            getCurrentTracker().addPrivateField(cxClassType, UniversalCompilerSettings.getInstance().getSettings().getvTableName(), vTable.toPointer());
            for (CXMethod cxMethod : cxClassType.getParent().getVirtualMethodsOrder()) {
                getCurrentTracker().addVariable(cxMethod.getCFunctionName(), cxMethod.getFunctionPointer());
            }
            
            CompoundStatementTypeAnalyzer analyzer = new CompoundStatementTypeAnalyzer(tree,
                    generatedSuper.getReturnType(), false);
            if(!determineTypes(analyzer)) {
                ICompilationSettings.debugLog.severe("Generated super method incorrectly formed");
                ICompilationSettings.debugLog.severe("Super: " + generatedSuper.getCFunctionName());
                return false;
            }
            releaseTrackingClosure();
    
            MethodTASNTracker.getInstance().add(generatedSuper, tree);
        }
        
        
        releaseTrackingClosure();
        return true;
    }
}
