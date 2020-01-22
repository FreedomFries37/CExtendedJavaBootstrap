package radin.core.utility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExperimentalSetting {
    
    /**
     * Determines the default boolean setting
     * @return the default setting for an experimental setting
     */
    boolean defaultBooleanUseSetting() default true;
    
    /**
     *
     * @return whether to use the boolean value
     */
    boolean useBooleanValue() default false;
    
    int defaultIntUseSetting() default 0;
    boolean useIntegerValue() default false;
    
    String defaultStringUseSetting() default "";
    boolean useStringValue() default false;
}
