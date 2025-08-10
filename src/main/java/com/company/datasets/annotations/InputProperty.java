package com.company.datasets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Fields in DataSets, describing how to turn user input into values for that Field
 * Used by DataCollectors
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputProperty {
    /**
     * message to be printed to ask for user input
     */
    String message() default "";

    /**
     * set to force user input to matcher the regex
     * ignored if message is empty, multiline true or options is not an empty array
     */
    String regex() default "";

    /**
     * set to force user input to be any of the options
     * ignored if message is empty or multiline true
     */
    String[] options() default {};

    /**
     * a second set of options, where the first set is mapped as keys to this list, only if both are same length, otherwise this is ignored
     */
    String[] options2() default {};

    /**
     * set to false to not force the options
     * does not ignore emptyToNull, but doesn't add empty string to options
     */
    boolean forceOptions() default true;

    /**
     * set to true to take input longer than one line
     */
    boolean multiline() default false;

    /**
     * used to guarantee the order in which user inputs are taken
     */
    int order();

    /**
     * order for fields with the same order value, allows only specifying values except for parsingFunc for the lowest groupOrder
     * only use values higher than 0 and always keep the lowest (first) field at default/0
     */
    int groupOrder() default 0;

    /**
     * function in utils.ParseUtils that returns a value of the type of the Field and takes a String as argument
     * leave empty for String identity
     */
    String parsingFunc() default "";

    /**
     * if not left empty a method from the Builder of the class that returns a boolean that if false causes this field to be skipped
     */
    String checkCondition() default "";

    /**
     * skips parsingFunc and passes null to the Field if the input String has length 0
     * does not bypass regex, but adds empty string to options automatically
     * also applies if parsingFunc is empty
     */
    boolean emptyToNull() default false;

}
