package radin.core.output.backend.compilation;

import radin.core.output.backend.microcompilers.IndentPrintWriter;
import radin.core.semantics.TypeEnvironment;
import radin.core.semantics.types.compound.CXClassType;
import radin.core.utility.ICompilationSettings;
import radin.core.utility.UniversalCompilerSettings;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static radin.core.utility.ICompilationSettings.createBuildFile;

public class RuntimeCompiler extends AbstractIndentedOutputSingleOutputCompiler {
    
    
    private TypeEnvironment environment;
    private String entrancePoint = "main";
    private String jodinEntrancePoint = "__main";
    
    public RuntimeCompiler(TypeEnvironment environment) throws IOException {
        super(new PrintWriter(new FileWriter(createBuildFile("runtime.jdn"))), 0);
        this.environment = environment;
    }
    
    public static <T> Predicate<T> distinctBy(Function<? super T, ?> f) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(f.apply(t));
    }
    
    @Override
    public boolean compile() {
        
        if(System.getenv("JODIN_HOME") != null) {
            File baseRuntimeFile = ICompilationSettings.getCoreFile("runtime.i");
            if(baseRuntimeFile == null) throw new NullPointerException("JODIN_HOME not set, can't create runtime");
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
        println("void __init_heap();");
        println("void __free_heap();");
        println("int " + jodinEntrancePoint + "(int argc, std::String argv[]);");
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
            println("ClassInfo " + cxClassType.getCTypeName() + "_info = nullptr; // class_id = " + environment.getTypeId(cxClassType));
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
        
        println("int " + entrancePoint + "(int argc, char* argv[]) {");
        setIndent(getIndent() + 1);
        println("__init_heap();");
        println("__init_reflection();");
        println("std::String args[argc];");
        println("for (int i = 0; i < argc; i++) args[i] = new std::String(argv[i]);");
        println("int output = " + jodinEntrancePoint + "(argc, args);");
        println("for (int i = 0; i < argc; i++) args[i]->drop();");
        println("__free_heap();");
        println("return output;");
        setIndent(getIndent() - 1);
        println("}");
    
        
        flush();
        close();
        return true;
    }
    
    public void setEntrancePoint(String entrancePoint) {
        this.entrancePoint = entrancePoint;
    }
    
    public void setJodinEntrancePoint(String jodinEntrancePoint) {
        this.jodinEntrancePoint = jodinEntrancePoint;
    }
    
    @Override
    protected void setIndent(int indent) {
        super.setIndent(indent);
        setPrintWriter(new IndentPrintWriter(getPrintWriter(), getIndent(), UniversalCompilerSettings.getInstance().getSettings().getIndent()));
    }
}
