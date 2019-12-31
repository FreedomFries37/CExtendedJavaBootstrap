package radin.interphase;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public interface ICompilationSettings {
    
    /**
     * Use experimental settings while compiling
     * @param value
     */
    default void setExperimental(boolean value) {
        if(value) {
            try {
                for (Method declaredMethod : ICompilationSettings.class.getDeclaredMethods()) {
                    if (declaredMethod.isAnnotationPresent(ExperimentalSetting.class)) {
                        ExperimentalSetting annotation = declaredMethod.getAnnotation(ExperimentalSetting.class);
                        if (annotation.useBooleanValue()) {
                            boolean val = annotation.defaultBooleanUseSetting();
                            declaredMethod.invoke(val);
                        } else if(annotation.useIntegerValue()) {
                            int val = annotation.defaultIntUseSetting();
                            declaredMethod.invoke(val);
                        } else if(annotation.useStringValue()) {
                            String val = annotation.defaultStringUseSetting();
                            declaredMethod.invoke(val);
                        }
            
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Optimize output code
     * @param value
     */
    @ExperimentalSetting(useIntegerValue = true)
    void setOptimizationLevel(int value);
    int getOptimizationLevel();
    
    /**
     * All function calls now run through a stack
     * @return
     */
    @ExperimentalSetting
    void setUseStackTrace(boolean value);
    boolean getUseStackTrace();
    
    /**
     * Compile with added option of try/catch blocks
     * @param value
     */
    @ExperimentalSetting
    void setUseTryCatch(boolean value);
    boolean getUseTryCatch();
    
    /**
     * string literals are automatically turned into string objects
     * @param value
     */
    @ExperimentalSetting
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
    public void setvTableName(String vTableName);
    
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
}
