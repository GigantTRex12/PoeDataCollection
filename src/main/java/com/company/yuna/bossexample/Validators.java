package com.company.yuna.bossexample;

import berlin.yuna.typemap.model.LinkedTypeMap;
import berlin.yuna.typemap.model.Type;

import java.util.function.BiFunction;

import static berlin.yuna.typemap.model.Type.typeOf;

@SuppressWarnings({"SameParameterValue", "java:S106"})
public class Validators {

    public static final BiFunction<Type<String>, LinkedTypeMap, Type<String>> VALIDATE_YES_NO = (answer, answers) ->
            switch (answer.asString().toLowerCase()) {
                case "y", "yes", "n", "no", "1", "0", "true", "false" -> Type.empty();
                default -> typeOf("Please answer y/yes or n/no");
            };

    public static final BiFunction<Type<String>, LinkedTypeMap, Type<String>> IS_NUMBER = (answer, answers) ->
            isEmpty(answer) || answer.asInt() != null
                    ? Type.empty()
                    : Type.typeOf("Only empty or digits allowed");


    public static BiFunction<Type<String>, LinkedTypeMap, Type<String>> isNotEmpty(final String msg) {
        return (answer, answers) ->
                answer == null || answer.asString().isBlank()
                        ? typeOf(msg)
                        : Type.empty();
    }

    public static BiFunction<Type<String>, LinkedTypeMap, Type<String>> regex(final String pattern, final String msg) {
        return (answer, answers) ->
                answer.asString().matches(pattern)
                        ? Type.empty()
                        : typeOf(msg);
    }

    public static BiFunction<Type<String>, LinkedTypeMap, Type<String>> intRange(final int min, final int max, final String msg) {
        return (answer, answers) ->
                isNotEmpty(answer) && answer.asIntOpt().filter(n -> n >= min && n <= max).isPresent()
                        ? Type.empty()
                        : typeOf(msg);
    }

    private static boolean isNotEmpty(final Type<String> answer) {
        return !isEmpty(answer);
    }

    private static boolean isEmpty(final Type<String> answer) {
        return answer == null || answer.value().isBlank();
    }

    private Validators() {
        // Utility class, no instances allowed
    }
}
