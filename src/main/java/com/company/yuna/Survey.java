package com.company.yuna;

import berlin.yuna.typemap.model.LinkedTypeMap;
import berlin.yuna.typemap.model.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.List;

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
    public static LinkedTypeMap run(final Question... questions) {
        return run(List.of(questions));
    }

    /**
     * Runs the survey for a given list of {@link Question}s and returns all
     * collected answers as a {@link LinkedTypeMap}.
     *
     * @param questions the survey questions to ask
     * @return a map of keys to validated, normalized user answers
     */
    public static LinkedTypeMap run(final List<Question> questions) {
        final LinkedTypeMap answers = new LinkedTypeMap();
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (final Question question : questions) {
            if (!question.condition().test(answers)) continue;

            while (true) {
                final String raw = readLine(in, question.prompt() + ": " + lineSeparator());
                final Type<String> input = Type.typeOf(raw);

                final Type<String> error = question.validator().apply(input, answers);
                if (error.isPresent()) {
                    System.err.println("! " + error.asString());
                    continue;
                }

                final Object normalized = question.normalizer().apply(input, answers);
                answers.put(question.key(), (normalized instanceof Type<?> t) ? t.value() : normalized);
                break;
            }
        }
        return answers;
    }

    /**
     * Reads a single line of text input from the user via the console.
     *
     * @param in     the buffered reader to use
     * @param prompt the message to show before reading
     * @return the trimmed user input, never null
     */
    private static String readLine(final BufferedReader in, final String prompt) {
        System.out.print(prompt);
        try {
            final String line = in.readLine();
            return line != null ? line.trim() : "";
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Survey() {
        // Utility class, no instances allowed
    }
}