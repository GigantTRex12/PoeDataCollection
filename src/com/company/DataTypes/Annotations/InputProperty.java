package com.company.DataTypes.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputProperty {
    public String message() default "";
    public String regex() default "";
    public boolean multiline() default false;
    public boolean nullable() default true;
    public int order();
}
