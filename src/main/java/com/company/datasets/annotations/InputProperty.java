package com.company.datasets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputProperty {
    public String message() default "";
    public String regex() default "";
    public String[] options() default {};
    public boolean multiline() default false;
    public boolean nullable() default true;
    public int order();
    public int groupOrder() default 0;
    public String parsingFunc() default "";
}
