package radin.core.utility;

public class CompilationSettings implements ICompilationSettings {
    
    private int optimizationLevel = 0;
    private boolean useStackTrace = false;
    private boolean useTryCatch = false;
    private boolean createStrings = false;
    
    private String indent = "    ";
    private String vTableName = "vtable";
    
    private boolean reduceIndirection = false;
    
    private boolean showErrorStackTrace = false;
    
    private int tabSize = 4;
    
    
    @Override
    public void setOptimizationLevel(int value) {
        debugLog.config("Optimization Level = " + value);
        optimizationLevel = value;
    }
    
    @Override
    public int getOptimizationLevel() {
        return optimizationLevel;
    }
    
    @Override
    public boolean getUseStackTrace() {
        
        return useStackTrace;
    }
    
    @Override
    public void setUseStackTrace(boolean value) {
        debugLog.config("Use Stack Trace = " + value);
        useStackTrace = value;
    }
    
    @Override
    public boolean getUseTryCatch() {
        return useTryCatch;
    }
    
    @Override
    public void setUseTryCatch(boolean value) {
        debugLog.config("Use Try/Catch blocks = " + value);
        useTryCatch = value;
    }
    
    @Override
    public boolean autoCreateStrings() {
        return createStrings;
    }
    
    @Override
    public void setAutoCreateStrings(boolean value) {
        debugLog.config("String literals are std::String objects = " + value);
        createStrings = value;
    }
    
    @Override
    public String getIndent() {
        return indent;
    }
    
    @Override
    public void setIndent(String s) {
        indent = s;
    }
    
    public String getvTableName() {
        return vTableName;
    }
    
    public void setvTableName(String vTableName) {
        debugLog.config("vtable field name = " + vTableName);
        this.vTableName = vTableName;
    }
    
    @Override
    public boolean isReduceIndirection() {
        
        return reduceIndirection;
    }
    
    @Override
    public void setReduceIndirection(boolean reduceIndirection) {
        debugLog.config("Reduce indirection = " + reduceIndirection);
        this.reduceIndirection = reduceIndirection;
    }
    
    @Override
    public boolean isShowErrorStackTrace() {
        return showErrorStackTrace;
    }
    
    @Override
    public void setShowErrorStackTrace(boolean showErrorStackTrace) {
        debugLog.config("Show error stack trace in Action Routine Applier = " + reduceIndirection);
        this.showErrorStackTrace = showErrorStackTrace;
    }
    
    @Override
    public int getTabSize() {
        return tabSize;
    }
    
    @Override
    public void setTabSize(int tabSize) {
        debugLog.config("default tab = \"" + " ".repeat(tabSize) + "\"");
        this.tabSize = tabSize;
    }
}
