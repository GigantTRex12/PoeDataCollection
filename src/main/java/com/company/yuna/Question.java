package com.company.yuna;

import berlin.yuna.typemap.model.Type;
import berlin.yuna.typemap.model.LinkedTypeMap;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Represents a single survey question definition including prompt, key, condition,
 * validator, and normalizer. Encapsulates all logic for asking and processing
 * user input without relying on reflection.
 */
public record Question(
        String key,
        String prompt,
        Predicate<LinkedTypeMap> condition,
        BiFunction<Type<String>, LinkedTypeMap, Type<String>> validator,
        BiFunction<Type<String>, LinkedTypeMap, Object> normalizer
) {
    public Question {
        requireNonNull(key);
        requireNonNull(prompt);
        condition  = condition  != null ? condition  : answers -> true;
        validator  = validator  != null ? validator  : (answer, answers) -> Type.empty();
        normalizer = normalizer != null ? normalizer : (answer, answers) -> answer.asString();
    }

    public static Builder ask(final String key, final String prompt) { return new Builder(key, prompt); }

    /**
     * Builder for creating {@link Question} instances with optional condition,
     * validation and normalization logic.
     */
    public static final class Builder {
        private final String key;
        private final String prompt;
        private Predicate<LinkedTypeMap> condition;
        private BiFunction<Type<String>, LinkedTypeMap, Type<String>> validator;
        private BiFunction<Type<String>, LinkedTypeMap, Object> normalizer;

        private Builder(final String key, final String prompt) { this.key = key; this.prompt = prompt; }

        public Builder when(final Predicate<LinkedTypeMap> condition) { this.condition = condition; return this; }
        public Builder validate(final BiFunction<Type<String>, LinkedTypeMap, Type<String>> validator) { this.validator = validator; return this; }
        public Builder normalize(final BiFunction<Type<String>, LinkedTypeMap, Object> normalizer) { this.normalizer = normalizer; return this; }
        public Question build() { return new Question(key, prompt, condition, validator, normalizer); }
    }
}
