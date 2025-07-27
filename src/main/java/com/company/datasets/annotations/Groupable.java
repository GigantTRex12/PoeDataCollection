package com.company.datasets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Fields or Methods in DataSets, marking their values to be possible to group by
 * If a Fields is annotated will group by that fields value
 * If a Method is annotated will group by that methods return value
 * Used by DataAnalyzers
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Groupable {
    /**
     * set to true to always group by the annotated value
     */
    boolean force() default false;

    /**
     * set to true to filter out any datasets where the value is null
     * if this groupable isn't used then this filter won't apply
     */
    boolean ignoreNulls() default false;

    /**
     * set to true to instead filter for values that evaluate as true
     * assumes return type can be casted to boolean
     */
    boolean filter() default false;

}
