package com.company.yuna;

import berlin.yuna.typemap.model.Type;
import berlin.yuna.typemap.model.LinkedTypeMap;
import com.company.exceptions.InvalidInputFormatException;
import com.company.utils.ThrowingFunction;
import com.company.utils.Utils;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.company.utils.Utils.contains;
import static com.company.utils.Utils.join;
import static java.lang.System.lineSeparator;
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
        NormalizerBiFunction normalizer,
        boolean multiline
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
        private NormalizerBiFunction normalizer;
        private boolean multiline = false;
        private String conditionPrompt;

        private Builder(final String key, final String prompt) { this.key = key; this.prompt = prompt; }

        public Builder when(final Predicate<LinkedTypeMap> condition) { this.condition = condition; return this; }
        public Builder validate(final BiFunction<Type<String>, LinkedTypeMap, Type<String>> validator) { this.validator = validator; this.conditionPrompt = null; return this; }
        public Builder normalize(final NormalizerBiFunction normalizer) { this.normalizer = normalizer; return this; }
        public Builder multiline() { multiline = !multiline; return this; }
        // easier ways to create validator/normalizer
        public Builder normalize(final ThrowingFunction<String, Object, InvalidInputFormatException> parser) {
            this.normalizer = (answer, answers) -> {
                final String string = answer.asString().strip();
                return parser.apply(string);
            };
            return this;
        }
        public Builder regex(final String regex) {
            validator = (t, m) -> {
                if (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(t.orElse("")).find()) return Type.empty();
                else return Type.typeOf("Input needs to match pattern " + regex);
            };
            conditionPrompt = "Format: " + regex;
            return this;
        }
        public Builder options(String[] options) {
            validator = (t, m) -> {
                if (contains(options, t.value())) return Type.empty();
                else return Type.typeOf("Input needs to be one of the given options");
            };
            conditionPrompt = "Options: " + join(options, ", ");
            return this;
        }
        public Builder options(String[] options1, String[] options2) {
            validator = (t, m) -> {
                if (contains(options1, t.value()) || contains(options2, t.value())) return Type.empty();
                else return Type.typeOf("Input needs to be one of the given options");
            };
            conditionPrompt = "Options: " + join(options1, options2, ", ");
            return this;
        }
        public Builder emptyToNull() {
            validator = validator == null ? null : (t, m) -> "".equals(t.orElse("")) ? Type.empty() : validator.apply(t, m);
            normalizer = normalizer == null ? null : (t, m) -> "".equals(t.orElse("")) ? null : normalizer.apply(t, m);
            return this;
        }

        public Question build() { return new Question(
                key, conditionPrompt == null ? prompt : prompt + lineSeparator() + conditionPrompt,
                condition, validator, normalizer, multiline
        ); }
    }
}
