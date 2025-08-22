package com.company.yuna.bossexample;

import berlin.yuna.typemap.model.LinkedTypeMap;
import berlin.yuna.typemap.model.Type;
import com.company.datasets.other.loot.Loot;
import com.company.exceptions.InvalidLootFormatException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.company.datasets.other.loot.Loot.parseToLoot;

@SuppressWarnings({"SameParameterValue", "java:S106"})
public class Normalizers {

    public static final BiFunction<Type<String>, LinkedTypeMap, Object> TO_STRING = (answer, answers) -> answer.asString().strip();
    public static final BiFunction<Type<String>, LinkedTypeMap, Object> TO_INTEGER = (answer, answers) -> answer.map(String::strip).asInt();
    public static final BiFunction<Type<String>, LinkedTypeMap, Object> TO_BOOLEAN = (answer, answers) ->
            switch (answer.asString().toLowerCase()) {
                case "y", "yes", "true", "t", "1" -> true;
                case "n", "no", "false", "f", "0" -> false;
                default -> null;
            };
    public static final BiFunction<Type<String>, LinkedTypeMap, Object> TO_LOOT = (answer, answers) -> {
        final String s = answer.asString().strip();
        try {
            //FIXME: complex object in simple may cause issues when using "toJson"
            //FIXME: Validation mixed with normalization is not ideal here, maybe a Question.parse() method could handle this
            // Can't catch the exception here. If this exception is thrown the loop is supposed to restart for a new user input
            // The logic might be too complex for a validator to handle without literally duplicating the logic of the normalizer
            return s.isEmpty() ? null : parseToLoot(s);
        } catch (InvalidLootFormatException e) {
            System.err.println(e.getMessage());
            return null;
        }
    };

    public static BiFunction<Type<String>, LinkedTypeMap, Object> toLootList(final Function<String, Loot> resolver) {
        return (a, answers) -> {
            final String s = a.asString();
            if (s.isBlank()) return List.<Loot>of();
            final String[] parts = s.replace("\r", "")
                    .replace(",", "\n")
                    .split("\n");
            final List<Loot> out = new ArrayList<>();
            for (final String p : parts) {
                final String token = p.strip();
                if (!token.isEmpty()) out.add(resolver.apply(token));
            }
            return List.copyOf(out);
        };
    }

    private Normalizers() {
        // Utility class, no instances allowed
    }
}
