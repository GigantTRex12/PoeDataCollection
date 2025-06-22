package com.company.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IOUtilsTest {

    @Test
    void simpleInput() {
        // given
        String input = "42";
        IOUtils.setInputStream(input);

        // when
        String actual = IOUtils.input();

        // then
        Assertions.assertEquals(input, actual);
    }

    @Test
    void multipleInputs() {
        // given
        String input = "42" + System.lineSeparator() + "43";
        IOUtils.setInputStream(input);

        // when
        String actual1 = IOUtils.input();
        String actual2 = IOUtils.input();

        // then
        Assertions.assertEquals("42", actual1);
        Assertions.assertEquals("43", actual2);
    }

    @Test
    void multiLineInput() {
        // given
        String input = "42\n43\n\n\n";
        IOUtils.setInputStream(input);

        // when
        String actual1 = IOUtils.multilineInput();

        // then
        Assertions.assertEquals("42\n43", actual1);
    }
}
