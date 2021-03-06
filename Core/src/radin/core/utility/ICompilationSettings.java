package radin.core.utility;


import radin.core.IFrontEndUnit;
import radin.core.JodinLogger;
import radin.core.chaining.IToolChain;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public interface ICompilationSettings<Front, Mid, Back> {
    
    JodinLogger debugLog = new JodinLogger("debug.log");
    /**
     * The logger for the interpreter
     */
    JodinLogger ilog = new JodinLogger("interpreter.log", "interpreter");
    JodinLogger typeLog = new JodinLogger(debugLog,"types.log", "types");
    JodinLogger interpreterStateLogger = new JodinLogger("states.log", "interpreter.states");
    
    static File createFile(String filename) {
        String directory = UniversalCompilerSettings.getInstance().getSettings().getDirectory();
        if(directory.equals("")) {
            ICompilationSettings.debugLog.info("Created file " + filename);
            File file = new File(filename);
            File parentFile = file.getParentFile();
            parentFile.mkdirs();
            return file;
        } else {
            File dir = new File(directory);
            dir.mkdirs();
            if(!dir.exists()) {
                ICompilationSettings.debugLog.severe("Did not create file " + filename + " in " + dir);
                return null;
            }
            
            ICompilationSettings.debugLog.info("Created file " + filename + " in " + dir.getAbsolutePath());
            File file = new File(dir, filename);
            File parentFile = file.getParentFile();
            parentFile.mkdirs();
            if(!parentFile.exists()) {
                return null;
            }
            return file;
        }
    }
    
    static String defaultInclude() {
        String jodin_home = System.getenv("JODIN_HOME");
        if (jodin_home != null) {
            return jodin_home + "/include";
        } else {
            return null;
        }
    }
    
    default void copySettingsFrom(ICompilationSettings<?, ?, ?> other) {
        setDirectory(other.getDirectory());
        for (File file : other.includeDirectories()) {
            try {
                addIncludeDirectories(file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (File additionalSource : other.getAdditionalSources()) {
            try {
                addAdditionalSourceDirectory(additionalSource.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Level level = debugLog.getLevel();
        debugLog.setLevel(Level.OFF);
        setThisPassedOffAsParameter(other.isThisPassedOffAsParameter());
        setLookForMainFunction(other.isLookForMainFunction());
        setOptimizationLevel(other.getOptimizationLevel());
        setUseStackTrace(other.getUseStackTrace());
        setUseTryCatch(other.getUseTryCatch());
        setIndent(other.getIndent());
        setvTableName(other.getvTableName());
        setShowErrorStackTrace(other.isShowErrorStackTrace());
        setTabSize(other.getTabSize());
        setOutputPostprocessingOutput(other.isOutputPostprocessingOutput());
        setHideClassPrivateDeclarations(other.isHideClassPrivateDeclarations());
        setAllowUseStatements(other.isAllowUseStatements());
        setDirectivesMustStartAtColumn1(other.isDirectivesMustStartAtColumn1());
        setInRuntimeCompilationMode(other.isInRuntimeCompilationMode());
        setOutputAST(other.isOutputAST());
        setOutputTAST(other.isOutputTAST());
        debugLog.setLevel(level);
    }
    
    static File createBuildFile(String filename) {
        return createFile("target/" + filename);
    }
    
    static File getBuildFile(String filename) {
        return createFile("target/" + filename);
    }
    
    static File getCoreFile(String filename) {
        String jodin_home = System.getenv("JODIN_HOME");
        if (jodin_home != null) {
            return new File(jodin_home + "/core/" + filename);
        } else {
            return null;
        }
    }
    
    String getDirectory();
    
    void setDirectory(String directory);
    
    /**
     * Use experimental settings while compiling
     * @param value
     */
    default void setExperimental(boolean value) {
        if(value) {
            debugLog.warning("Using Experimental Settings are not guaranteed to work properly, and can potentially be" +
                    " very inefficient");
            try {
                for (Method declaredMethod : ICompilationSettings.class.getDeclaredMethods()) {
                    if (declaredMethod.isAnnotationPresent(ExperimentalSetting.class)) {
                        ExperimentalSetting annotation = declaredMethod.getAnnotation(ExperimentalSetting.class);
                        debugLog.warning("Using experimental setting for " + declaredMethod.getName());
                        if (annotation.useBooleanValue()) {
                            boolean val = annotation.defaultBooleanUseSetting();
                            declaredMethod.invoke(this, val);
                        } else if(annotation.useIntegerValue()) {
                            int val = annotation.defaultIntUseSetting();
                            declaredMethod.invoke(this, val);
                        } else if(annotation.useStringValue()) {
                            String val = annotation.defaultStringUseSetting();
                            declaredMethod.invoke(this, val);
                        }
            
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    File[] includeDirectories();
    void addIncludeDirectories(String dir) throws IOException;
    
    File[] getAdditionalSources();
    void addAdditionalSource(String sourceFile) throws IOException;
    default void addAdditionalSourceDirectory(String dir) throws IOException {
        if(dir == null) throw new NullPointerException();
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) throw new IOException();
    
        File[] files = dirFile.listFiles();
        if (files == null) throw new IOException();
        for (File file : files) {
            addAdditionalSource(file.getAbsolutePath());
        }
    }
    
    boolean isThisPassedOffAsParameter();
    
    void setThisPassedOffAsParameter(boolean thisPassedOffAsParameter);
    
    boolean isLookForMainFunction();

    void setLookForMainFunction(boolean lookForMainFunction);
    
    int getOptimizationLevel();

    /**
     * Optimize output code
     * @param value
     */
    @ExperimentalSetting(useIntegerValue = true)
    void setOptimizationLevel(int value);
    
    boolean getUseStackTrace();

    /**
     * All function calls now run through a stack
     *
     */
    @ExperimentalSetting(useBooleanValue = true)
    void setUseStackTrace(boolean value);
    
    boolean getUseTryCatch();

    /**
     * Compile with added option of try/catch blocks
     * @param value
     */
    @ExperimentalSetting(useBooleanValue = true)
    void setUseTryCatch(boolean value);
    
    /**
     * string literals are automatically turned into string objects
     * @param value
     */
    @ExperimentalSetting(useBooleanValue = true)
    void setAutoCreateStrings(boolean value);
    
    boolean autoCreateStrings();
    
    String getIndent();
    
    /**
     * the indent to use while indenting outputed programs
     * @param s
     */
    void setIndent(String s);
    
    String getvTableName();
    
    /**
     * Set the name of the vtable field
     * @param vTableName name
     */
    void setvTableName(String vTableName);
    
    boolean isReduceIndirection();
    
    /**
     * Determines if when a field of a compound type pointer, it uses a (*x).y or
     * x->y
     * @param reduceIndirection
     */
    void setReduceIndirection(boolean reduceIndirection);
    
    boolean isShowErrorStackTrace();
    
    void setShowErrorStackTrace(boolean showErrorStackTrace);
    
    int getTabSize();
    
    void setTabSize(int tabSize);
    
    default void setLogLevel(Level logLevel) {
        debugLog.setLevel(logLevel);
        ilog.setLevel(logLevel);
        typeLog.setLevel(logLevel);
        interpreterStateLogger.setLevel(logLevel);
    }
    
    boolean isOutputPostprocessingOutput();
    
    void setOutputPostprocessingOutput(boolean outputPostprocessingOutput);
    
    IFrontEndUnit<? extends Front> getFrontEndUnit();
    
    void setFrontEndUnit(IFrontEndUnit<? extends Front> frontEndUnit);
    
    IToolChain<? super Front, ? extends Mid> getMidToolChain();
    
    void setMidToolChain(IToolChain<? super Front, ? extends Mid> midToolChain);
    
    IToolChain<? super Mid, ? extends Back> getBackToolChain();
    
    void setBackToolChain(IToolChain<? super Mid, ? extends Back> backToolChain);
    
    boolean isHideClassPrivateDeclarations();
    
    /**
     * Determines when a class definition is referrred to using a {@code using} statement, if it's private and protected
     * members are "hidden" using sized buffers.
     */
    @ExperimentalSetting(useBooleanValue = true)
    void setHideClassPrivateDeclarations(boolean hideClassPrivateDeclarations);
    
    boolean isAllowUseStatements();
    
    @ExperimentalSetting(useBooleanValue = true)
    void setAllowUseStatements(boolean allowUseStatements);
    
    boolean isDirectivesMustStartAtColumn1();
    
    void setDirectivesMustStartAtColumn1(boolean directivesMustStartAtColumn1);
    
    boolean isInRuntimeCompilationMode();
    
    void setInRuntimeCompilationMode(boolean inRuntimeCompilationMode);
    
    boolean isOutputAST();
    
    void setOutputAST(boolean outputAST);
    
    boolean isOutputTAST();
    
    void setOutputTAST(boolean outputTAST);
    
    enum SupportedWordSize {
        arch64,
        arch32
    }
}
