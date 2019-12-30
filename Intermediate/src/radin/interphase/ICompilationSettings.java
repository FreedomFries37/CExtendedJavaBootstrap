package radin.interphase;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public interface ICompilationSettings {
    
    
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
    
    @ExperimentalSetting(useIntegerValue = true)
    void setOptimizationLevel(int value);
    int getOptimizationLevel();
    
    @ExperimentalSetting
    boolean getUseStackTrace();
    void setUseStackTrace(boolean value);
    
    @ExperimentalSetting
    boolean getUseTryCatch();
    void setUseTryCatch(boolean value);
    
    @ExperimentalSetting
    boolean autoCreateStrings();
    void setAutoCreateStrings(boolean value);
    
    String getIndent();
    void setIndent(String s);
    
    String getvTableName();
    public void setvTableName(String vTableName);
}
