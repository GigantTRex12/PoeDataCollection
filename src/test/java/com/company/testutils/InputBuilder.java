package com.company.testutils;

import com.company.utils.IOUtils;

public class InputBuilder {

    private static final String LINEBREAK = System.lineSeparator();

    private String input;

    private InputBuilder() {
        input = "";
    }

    public static InputBuilder start() {
        return new InputBuilder();
    }

    public InputBuilder emptyLine() {
        return line("");
    }

    public InputBuilder line(String string) {
        input += string + LINEBREAK;
        return this;
    }

    public InputBuilder line(int number) {
        return line(Integer.toString(number));
    }

    public InputBuilder multiLine(String[] strings) {
        for (String string : strings) {
            line(string);
        }
        input += LINEBREAK;
        return this;
    }

    public void set() {
        IOUtils.setInputStream(input);
    }

}
