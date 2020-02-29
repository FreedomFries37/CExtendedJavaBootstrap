package radin.core.output.backend.compilation;

import radin.core.output.backend.microcompilers.IndentPrintWriter;
import radin.core.output.midanalysis.TypeAugmentedSemanticNode;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.utility.UniversalCompilerSettings;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RuntimeCompiler extends AbstractIndentedOutputSingleOutputCompiler {
    
    
    private TypeEnvironment environment;
    
    public RuntimeCompiler(TypeEnvironment environment) throws IOException {
        super(new PrintWriter(new FileWriter(new File("runtime.jdn"))), 0);
        this.environment = environment;
    }
    
    public static <T> Predicate<T> distinctBy(Function<? super T, ?> f) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(f.apply(t));
    }
    
    @Override
    public boolean compile() {
        
        if(System.getenv("JODIN_HOME") != null) {
            File baseRuntimeFile = new File(new File(System.getenv("JODIN_HOME")), "runtime.i");
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(baseRuntimeFile));
                
                while (bufferedReader.ready()) {
                    println(bufferedReader.readLine());
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        println("void __init_reflection();");
        println("int __main(int argc, std::String argv[]);");
        println("in std {");
        setIndent(getIndent() + 1);
        List<CXClassType> cxClassTypes = environment.getAllCreated().stream().filter(distinctBy((t) -> environment.getTypeId(t))).collect(Collectors.toList());
        int classInfoIndex = 0;
        for (int i = 0; i < cxClassTypes.size(); i++) {
            if(cxClassTypes.get(i).getTypeName().equals("std::ClassInfo")) {
                classInfoIndex = i;
                break;
            }
        }
        CXClassType classInfo = cxClassTypes.remove(classInfoIndex);
        cxClassTypes.add(0, classInfo);
        for (CXClassType cxClassType :
                cxClassTypes) {
            println("ClassInfo " + cxClassType.getCTypeName() + "_info = 0; // class_id = " + environment.getTypeId(cxClassType));
        }
        
        
        
        println("ClassInfo __get_class(class_id id) {");
        setIndent(getIndent() + 1);
        for (CXClassType cxClassType :
                cxClassTypes) {
            String identifier = cxClassType.getCTypeName() + "_info";
            println("if (id == " + environment.getTypeId(cxClassType) + ") return " + identifier + ";");
        }
        println("return nullptr;");
        setIndent(getIndent() - 1);
        println("}");
        setIndent(getIndent() - 1);
        println("}");
        
        println("int main(int argc, char* argv[]) {");
        setIndent(getIndent() + 1);
        println("__init_reflection();");
        println("std::String args[argc];");
        println("for (int i = 0; i < argc; i++) args[i] = new std::String(argv[i]);");
        println("int output = __main(argc, args);");
        println("for (int i = 0; i < argc; i++) args[i]->drop();");
        println("return output;");
        setIndent(getIndent() - 1);
        println("}");
    
        println("void __init_reflection() {");
        setIndent(getIndent() + 1);
        for (CXClassType cxClassType :
                cxClassTypes) {
            String identifier = cxClassType.getCTypeName() + "_info";
            /*
            println(identifier + " = (std::ClassInfo) malloc(sizeof(std::ClassInfo));");
            println(identifier + "->name = new std::String(\"" + cxClassType.getTypeName() +"\");");
            println(identifier + "->classHash = " + cxClassType.getCTypeName().hashCode() +";");
            
             */
            println(identifier + " = new std::ClassInfo();");
            println(identifier + "->name = new std::String(\"" + cxClassType.getTypeName() +"\");");
            println(identifier + "->classHash = " + cxClassType.hashCode() +";");
            if(cxClassType.getTypeName().equals("std::ClassInfo")) {
                println(identifier + "->info = " + identifier +";");
            }
        }
    
        for (CXClassType cxClassType :
                cxClassTypes) {
            
            String identifier = cxClassType.getCTypeName() + "_info";
            if(cxClassType.getParent() == null) {
                println(identifier + "->parent = nullptr;");
            } else {
                println(identifier + "->parent = __get_class(" + environment.getTypeId(cxClassType.getParent()) + ");");
            }
            
        }
        
        setIndent(getIndent() - 1);
        
        
        println("}");
        flush();
        close();
        return true;
    }
    
    @Override
    protected void setIndent(int indent) {
        super.setIndent(indent);
        setPrintWriter(new IndentPrintWriter(getPrintWriter(), getIndent(), UniversalCompilerSettings.getInstance().getSettings().getIndent()));
    }
}
