package com.ruoyi.system.anno;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author a1152
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InitializeWith {

    String stringValue() default "";

    int intValue() default 0;

    double doubleValue() default 0.0;

    boolean booleanValue() default false;

    long longValue() default 0L;

    float floatValue() default 0.0f;

    char charValue() default ' ';

    short shortValue() default 0;

    byte byteValue() default 0;

}
