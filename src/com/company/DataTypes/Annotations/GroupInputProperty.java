package com.company.DataTypes.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GroupInputProperty {
    public String message() default "";
    public String regex() default "";
    public int id();
    public int pos();
    public boolean nullable() default true;
    public int order();
}
