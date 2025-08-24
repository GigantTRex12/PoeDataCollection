package com.company.yuna.bossexample;

import com.company.yuna.NormalizerBiFunction;

@SuppressWarnings({"SameParameterValue", "java:S106"})
public class Normalizers {

    public static final NormalizerBiFunction TO_STRING = (answer, answers) -> answer.asString().strip();
    public static final NormalizerBiFunction TO_INTEGER = (answer, answers) -> answer.map(String::strip).asInt();
    public static final NormalizerBiFunction TO_BOOLEAN = (answer, answers) ->
            switch (answer.asString().toLowerCase()) {
                case "y", "yes", "true", "t", "1" -> true;
                case "n", "no", "false", "f", "0" -> false;
                default -> null;
            };

    private Normalizers() {
        // Utility class, no instances allowed
    }
}
