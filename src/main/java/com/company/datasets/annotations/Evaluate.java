package com.company.datasets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotation for Fields or Methods in DataSets, marking their values to be possible to be evaluated
// If a Fields is annotated will evaluate that fields value
// If a Method is annotated will evaluate that methods return value
// Used by DataAnalyzers
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Evaluate {
    // set to true if the annotated value should be able to be evaluated numerically
    boolean isNum() default false;
}
