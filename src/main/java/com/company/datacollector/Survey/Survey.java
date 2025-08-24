package com.company.datacollector.Survey;

import berlin.yuna.typemap.model.LinkedTypeMap;
import berlin.yuna.typemap.model.Type;
import com.company.datasets.other.metadata.Strategy;
import com.company.exceptions.InvalidInputFormatException;
import com.company.exceptions.SomethingIsWrongWithMyCodeException;

import java.util.List;

import static com.company.utils.IOUtils.*;
import static java.lang.System.lineSeparator;

/**
 * Utility class to execute a sequence of {@link Question}s in order, collecting
 * validated and normalized answers into a {@link LinkedTypeMap}.
 */
public class Survey {

    /**
     * Runs the survey for a given array of {@link Question}s and returns all
     * collected answers as a {@link LinkedTypeMap}.
     *
     * @param questions the survey questions to ask
     * @return a map of keys to validated, normalized user answers
     */
    public static LinkedTypeMap run(Strategy strategy, final Question... questions) {
        return run(List.of(questions), strategy);
    }

    /**
     * Runs the survey for a given list of {@link Question}s and returns all
     * collected answers as a {@link LinkedTypeMap}.
     *
     * @param questions the survey questions to ask
     * @return a map of keys to validated, normalized user answers
     */
    public static LinkedTypeMap run(final List<Question> questions, Strategy strategy) {
        final LinkedTypeMap answers = new LinkedTypeMap();
        answers.put("strategy", strategy);
        for (final Question question : questions) {
            if (!question.condition().test(answers)) continue;

            while (true) {
                // already have methods for this, no need to implement a new one
                final String raw = question.multiline() ? multilineInput(question.prompt()) : input(question.prompt());
                final Type<String> input = Type.typeOf(raw);

                if (!validateAndPrintError(input, question, answers)) continue;

                try {
                    final Object normalized = question.normalizer().apply(input, answers);
                    putInMap(answers, question.key(), (normalized instanceof Type<?> t) ? t.value() : normalized);
                } catch (InvalidInputFormatException e) {
                    print("Invalid input: " + e.getMessage());
                    continue;
                }
                break;
            }
        }
        return answers;
    }

    private static boolean validateAndPrintError(Type<String> input, Question question, LinkedTypeMap answers) {
        Type<String> error = Type.empty();
        if (question.multiline() && !input.orElse("").equals("")) {
            for (String s : input.value().split(lineSeparator())) {
                error = question.validator().apply(Type.typeOf(s), answers);
                if (error.isPresent()) break;
            }
        } else error = question.validator().apply(input, answers);
        if (error.isPresent()) {
            print("Invalid input, try again");
            // TODO: test does not expect a specified message yet
            // print("Invalid input: " + error.asString()); // no need for error level at this point
            // also there's some weirdness when this is at error level that causes this to happen after the next input() call
            return false;
        }
        return true;
    }

    private static void putInMap(LinkedTypeMap answers, String key, Object value) {
        if (key.contains("&")) {
            if (!(value instanceof List)) throw new SomethingIsWrongWithMyCodeException("For multiple fields value has to be a List");
            List<?> values = (List<?>) value;
            String[] keys = key.split("&");
            if (values.size() != keys.length) throw new SomethingIsWrongWithMyCodeException("Different amount of keys and values.");
            for (int i = 0; i < keys.length; i++) answers.put(keys[i], values.get(i));
        } else answers.put(key, value);
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Survey() {
        // Utility class, no instances allowed
    }
}