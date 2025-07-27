package com.company.datasets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.company.datasets.annotations.Evaluate.EvaluationMode.PERCENTAGE_BASED;

/**
 * Annotation for Fields or Methods in DataSets, marking their values to be possible to be evaluated
 * If a Fields is annotated will evaluate that fields value
 * If a Method is annotated will evaluate that methods return value
 * Used by DataAnalyzers
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Evaluate {

    /**
     * describes how the value gets evaluated
     */
    EvaluationMode evaluationMode() default PERCENTAGE_BASED;

    enum EvaluationMode {
        /**
         * Evaluate value as percantages of each different value
         */
        PERCENTAGE_BASED,
        /**
         * puts all results in a Counter and displays the counter
         * especially useful for List values
         */
        COUNTER_BASED,
    }

}
