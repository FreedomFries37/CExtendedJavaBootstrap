package radin.core.utility;

public class UniversalCompilerSettings {
    
    private ICompilationSettings<?, ?, ?> settings;
    private static UniversalCompilerSettings instance = new UniversalCompilerSettings();
    
    public static UniversalCompilerSettings getInstance() {
        return instance;
    }
    
    public ICompilationSettings<?, ?, ?> getSettings() {
        return settings;
    }
    
    public void setSettings(ICompilationSettings<?, ?, ?> settings) {
        this.settings = settings;
    }
    
}
