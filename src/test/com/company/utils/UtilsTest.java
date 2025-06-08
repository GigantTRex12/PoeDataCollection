package test.com.company.utils;

import main.com.company.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    void test_joinArrayNoSeperator() {
        // given
        String[] strings = {"aaa", "bb", "cccc"};

        // when
        String actual = Utils.join(strings);

        // then
        assertEquals("aaabbcccc", actual);
    }

    @Test
    void test_joinEmptyArrayNoSeperator() {
        // given
        String[] strings = {};

        // when
        String actual = Utils.join(strings);

        // then
        assertEquals("", actual);
    }

    @Test
    void test_joinOneElementArrayNoSeperator() {
        // given
        String[] strings = {"42"};

        // when
        String actual = Utils.join(strings);

        // then
        assertEquals("42", actual);
    }

    @Test
    void test_joinArraySeperator() {
        // given
        String[] strings = {"aaa", "bb", "cccc"};

        // when
        String actual = Utils.join(strings, "+");

        // then
        assertEquals("aaa+bb+cccc", actual);
    }

    @Test
    void test_joinEmptyArraySeperator() {
        // given
        String[] strings = {};

        // when
        String actual = Utils.join(strings, "+");

        // then
        assertEquals("", actual);
    }

    @Test
    void test_joinOneElementArraySeperator() {
        // given
        String[] strings = {"42"};

        // when
        String actual = Utils.join(strings, "+");

        // then
        assertEquals("42", actual);
    }



    @ParameterizedTest
    @ValueSource(strings = {"12", "4.2", "0.75", "55.0", ".89", "+44", "+33.22", "+.76", "-90", "-20.20", "-.666", "0",
            "00", "-0"})
    void test_isNumberTrue(String num) {
        // when
        boolean actual = Utils.isNumber(num);

        // then
        assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"aa", "2b", "cc3", "2,1", " ", " 22", "84 ", "-a", "+ccc", "1.", "5S5"})
    void test_isNumberFalse(String num) {
        // when
        boolean actual = Utils.isNumber(num);

        // then
        assertFalse(actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void test_isNumberNull(String num) {
        // when
        boolean actual = Utils.isNumber(num);

        // then
        assertFalse(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "+44", "-90", "0", "00", "-0"})
    void test_isWholeNumberTrue(String num) {
        // when
        boolean actual = Utils.isWholeNumber(num);

        // then
        assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"aa", "2b", "cc3", "2,1", " ", " 22", "84 ", "-a", "+ccc", "1.", "5S5", "4.2", "0.75",
            "55.0", ".89", "+33.22", "+.76", "-20.20", "-.666"})
    void test_isWholeNumberFalse(String num) {
        // when
        boolean actual = Utils.isWholeNumber(num);

        // then
        assertFalse(actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void test_isWholeNumberNull(String num) {
        // when
        boolean actual = Utils.isWholeNumber(num);

        // then
        assertFalse(actual);
    }
}
