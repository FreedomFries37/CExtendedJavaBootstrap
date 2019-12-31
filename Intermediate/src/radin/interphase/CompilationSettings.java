package radin.interphase;

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
        useStackTrace = value;
    }
    
    @Override
    public boolean getUseTryCatch() {
        return useTryCatch;
    }
    
    @Override
    public void setUseTryCatch(boolean value) {
        useTryCatch = value;
    }
    
    @Override
    public boolean autoCreateStrings() {
        return createStrings;
    }
    
    @Override
    public void setAutoCreateStrings(boolean value) {
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
        this.vTableName = vTableName;
    }
    
    @Override
    public boolean isReduceIndirection() {
        return reduceIndirection;
    }
    
    @Override
    public void setReduceIndirection(boolean reduceIndirection) {
        this.reduceIndirection = reduceIndirection;
    }
    
    @Override
    public boolean isShowErrorStackTrace() {
        return showErrorStackTrace;
    }
    
    @Override
    public void setShowErrorStackTrace(boolean showErrorStackTrace) {
        this.showErrorStackTrace = showErrorStackTrace;
    }
    
    @Override
    public int getTabSize() {
        return tabSize;
    }
    
    @Override
    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }
}
