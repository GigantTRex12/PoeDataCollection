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
         * Evaluate value as percentages of each different value
         */
        PERCENTAGE_BASED,
        /**
         * Like PERCENTAGE_BASED but also gives a varianz of the probability based on number of attempts
         * (the probability range the actual probability is in with 99% probability according to the data)
         */
        PERCENTAGE_BASED_VARIANCE,
        /**
         * used for Collections to count occurrences with a Counter
         */
        COUNTER_BASED,
        /**
         * statistic based on numbers in form of a graph
         * only use on whole number types (byte, int, long) including the Wrapper classes
         */
        NUMBER_STATISTIC_GRAPH,
        /**
         * custom evaluation mode
         * Analyzer needs to overwrite customMode() method and have manual implementation for each custom evaluation mode
         */
        CUSTOM
    }

}
